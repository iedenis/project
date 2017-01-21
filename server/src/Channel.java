
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
 * @author Denis Ievlev
 * @author Samer Hadeed
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

    /*
    public void send(String message) {
        writer.println(message);
        writer.flush();
    }
*/
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
                    //handleMessage(message);
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
     * Handles the received disconnecting message from client.<br>
     * Removes the user from online users list.
     */
    private void disconnectMessageRequest() {
        if (name != null) {
            serverModel.removeClient(name);
            serverView.appendMessage("Client " + name + " left the chat\n");
        }

        serverModel.removeChannel(this);
        //sendUserNames();

        try {
            stop();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to stop the channel thread for user: " + name );
        }
    }

}
