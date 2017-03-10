import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Class represents server controller.<br>
 * It connects between {@link ServerModel} class and {@link ServerView} class
 *
 * @author Denis Ievlev
 * @author Samer Hadeed
 */
public class ServerController implements Runnable {
    private ServerModel serverModel;
    private ServerView serverView;
    private ScriptThread scriptWriter;
    private int serverPort;
    private ServerSocket serverSocket;
    private boolean running;

    /**
     * Constructor for Server Controller.
     *
     * @param serverModel {@link ServerModel} object
     * @param serverView  {@link ServerView} GUI object
     */
    public ServerController(ServerModel serverModel, ServerView serverView) {
        this.serverModel = serverModel;
        this.serverView = serverView;

        this.running = false;
        this.serverSocket = null;
        this.serverPort = 55555;

        this.serverView.addServerStateListener(new ServerStateListener());
        this.serverView.addServerWindowListener(new ServerWindowLisrener());
    }

    /**
     * Creates a new server socket
     *
     * @throws IOException when cannot create a socket
     */
    public void bind() throws IOException {
        serverSocket = new ServerSocket(serverPort);
    }

    /**
     * Starts the server in a new thread
     */
    public void start() {
        Thread thread = new Thread(this);
        thread.setName("Server");
        thread.start();


    }

    /**
     * Stops the server and sends to all users <br>
     * to disconnect before it stops.
     *
     * @throws IOException when cannot stop
     */
    public void stop() throws IOException {
        for (Channel channel : serverModel.getChannels()) {
            channel.stop();

        }

        scriptWriter.stop();
        serverModel.dropClients();
        serverModel.dropChannels();
        running = false;
        serverSocket.close();

    }

    /**
     * Creates a new thread for new user when receives a socket from user.
     */
    @Override
    public void run() {

        Socket newSocket = null;
        Channel channel = null;
        // ScriptThread script= null;
        running = true;
        scriptWriter = new ScriptThread(serverModel, serverView);
        scriptWriter.start();
        while (running) {
            try {
                //printText("Trying to connect... ");
                newSocket = serverSocket.accept();
                printText("Connected to client");
                channel = new Channel(newSocket, serverModel, serverView);
                serverModel.addChannel(channel);
                channel.start();
            } catch (SocketException e) {
                printText("The socket was closed");
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("LOG: Failed to add socket");
            }

        }
    }

    void printText(String text) {
        serverView.appendMessage(text);
    }

    /**
     * Handles the {@link ServerController#stop()} method
     *
     * @throws IOException when cannot stop server
     */
    private void stopServerHandler() {
        try {
            stop();
        } catch (IOException e) {
            System.err.println("Failed to stop server");
            e.printStackTrace();
        }
        printText("Server stopped");
        serverView.setButtonText("Start Server");
        serverView.setServerStateRunning(false);
    }

    /**
     * Starting the serve with method {@link ServerController#start()}
     *
     * @throws IOException when cannot start the server<br>
     *                     and can't create the server socket
     */
    private void startServerHandler() {
        try {
            bind();
        } catch (IOException e) {
            System.err.println("Failed to bind server socket");
            e.printStackTrace();
        }
        start();


        printText("Server is up and running\n");
        serverView.setButtonText("Stop Server ");
        serverView.setServerStateRunning(true);
        //serverView.appendMessage("Starting the main script\n");

    }

    /**
     * The class represents listener for GUI.
     */
    class ServerStateListener implements MouseListener {
        /**
         * Mose listener. When button "Start" clicked<br>
         * call method {@link ServerController#stopServerHandler()} to stop the server<br>
         * and {@link ServerController#startServerHandler()} to start the server
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            if (serverView.isServerStateRunning()) {
                stopServerHandler();
            } else {
                startServerHandler();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }

    /**
     * The class represent a listener for window
     */
    class ServerWindowLisrener implements WindowListener {

        @Override
        public void windowOpened(WindowEvent e) {

        }

        /**
         * Method to close the window. When clicking X (close) on window<br>
         * it stops the server
         *
         * @throws IOException when there is a problem to stop the server
         */
        @Override
        public void windowClosing(WindowEvent e) {
            if (serverView.isServerStateRunning()) {
                try {
                    stop();

                } catch (IOException err) {
                    System.err.println("Failed to stop server");
                    err.printStackTrace();
                }
            }
        }

        @Override
        public void windowClosed(WindowEvent e) {

        }

        @Override
        public void windowIconified(WindowEvent e) {

        }

        @Override
        public void windowDeiconified(WindowEvent e) {

        }

        @Override
        public void windowActivated(WindowEvent e) {

        }

        @Override
        public void windowDeactivated(WindowEvent e) {

        }
    }
}
