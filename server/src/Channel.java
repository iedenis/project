/**
 * Created by Alexey.Kurbatsky on 8/27/2016.
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * The class represents all chat options for the server<br>
 * such as: handling messages that server receive from clients,
 * retransmitting them and other.
 * @author Alexey Kurbatsky
 * @author Denis Ievlev
 *
 */
public class Channel implements Runnable
{
    private Socket socket;
    private Scanner reader;
    private PrintWriter writer;
    private boolean running;
    private String name = null;
    private ServerModel serverModel;
    private ServerView serverView;

    public Channel(Socket socket, ServerModel serverModel, ServerView serverView) {
        this.socket = socket;
        this.serverModel = serverModel;
        this.serverView = serverView;
    }

    /**
     * Start a new thread with chat channel
     */
    public void start() {
        Thread thread = new Thread(this);
        thread.setName("Channel thread");
        thread.start();
    }

    /**
     * Stops the current thread
     * @throws IOException when cannot close stream or current thread
     */
    public void stop() throws IOException {
        running = false;

        writer.close();
        reader.close();
        socket.close();
    }

    /**
     * Sends the message encoded by {@link Protocol#createMessage(int, String, String)}
     * @param message - represented by String.
     */
    public void send(String message) {
        writer.println(message);
        writer.flush();
    }

    @Override
    public void run() {
        try {
            OutputStream outputStream = socket.getOutputStream();
            writer = new PrintWriter(outputStream);

            InputStream inputStream = socket.getInputStream();
            reader = new Scanner(inputStream);
            running = true;

            while (running) {
                try {
                    String message = reader.nextLine();
                    handleMessage(message);
                }
                catch(NoSuchElementException e) {
                    System.err.println(name + " channel has closed");
                    e.getStackTrace();
                    disconnectMessageRequest();
                    break;
                }
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the received message encoded by {@link Protocol#createMessage(int, String, String)}<br>
     * Handles messages of types:<br>
     *  {@link Protocol#connectRequestMessage}<br>
     *  {@link Protocol#disconnectMessage}<br>
     *  {@link Protocol#privateMessage}<br>
     *  {@link Protocol#broadcastMessage}<br>
     * @param message - String
     */
    private void handleMessage(String message) {
        int messageType = Protocol.getType(message);
        String parsedMessage[] = Protocol.parseMessage(message);

        switch (messageType) {
            case Protocol.connectRequestMessage:
                connectMessageRequest(parsedMessage);
                break;
            case Protocol.disconnectMessage:
                disconnectMessageRequest();
                break;
            case Protocol.privateMessage:
                privateMessageRequest(parsedMessage, message);
                break;
            case Protocol.broadcastMessage:
                broadcastMessageRequest(message);
                break;
        }
    }

    /**
     * Method for handling the {@link Protocol#connectRequestMessage}<br>
     * Sends the message with answer.<br>
     * In case the username is in use sends "fail" otherwise "seccess"
     * @param parsedMessage - message parsed by {@link Protocol#parseMessage(String)}
     */
    private void connectMessageRequest(String parsedMessage[]) {
        String messageToSend = null;
        String desiredName = parsedMessage[0];
        Channel channel = serverModel.getClient(desiredName);

        if (channel == null) {
            messageToSend = Protocol.createMessage(Protocol.connectRequestMessage, desiredName, "success");
            name = desiredName;
            serverModel.putClient(name, this);
            send(messageToSend);
            sendUserNames();
            serverView.appendMessage("Client " + name + " entered to the chat\n");
        }
        else {
            messageToSend = Protocol.createMessage(Protocol.connectRequestMessage, desiredName, "fail");
            send(messageToSend);
        }
    }

    /**
     * Handles the received disconnecting message from client.<br>
     * Removes the user from online users list.
     */
    private void disconnectMessageRequest() {
        if (name != null) {
            serverModel.removeClient(name);
            serverView.appendMessage("Client " + name + " left the chat\n");
        }

        serverModel.removeChannel(this);
        sendUserNames();

        try {
            stop();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to stop the channel thread for user: " + name );
        }
    }

    /**
     * The method handles the {@link Protocol#privateMessage}<br>
     * and send the message to destination user.
     * @param parsedMessage - message parsed by {@link Protocol#parseMessage(String)} function
     * @param message - encoded message by {@link Protocol#createMessage(int, String, String)} function
     */
    private void privateMessageRequest(String parsedMessage[], String message) {
        String sendTo = parsedMessage[2];
        Channel channel = serverModel.getClient(sendTo);

        if (channel != null) {
            channel.send(message);
        }
        else {
            String errorMessage = Protocol.createMessage(Protocol.serverMessage, "", "Seems like user '" + sendTo + "' is offline.");
            send(errorMessage);
        }
    }

    /**
     * Handles the {@link Protocol#broadcastMessage} type.<br>
     * and sends the message to all online users
     * @param message - encoded by {@link Protocol#createMessage(int, String, String)}
     */
    private void broadcastMessageRequest(String message) {
        for (Channel channel : serverModel.getChannels()) {
            if (channel != this) {
                channel.send(message);
            }
        }
    }

    /**
     * Method sends to a client online users when server got<br>
     * message of type {@link Protocol#refreshOnlineUsers}
     */
    private void sendUserNames() {
        StringBuffer usersBuffer = new StringBuffer();

        serverModel.getClients().forEach((k, v) -> usersBuffer.append(k + ","));
        String users = usersBuffer.toString();

        String message = Protocol.createMessage(Protocol.refreshOnlineUsers, "", users);
        send(message);
        broadcastMessageRequest(message);
    }
}
