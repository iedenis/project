import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.NoSuchElementException;


import static java.nio.file.Files.copy;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * @author Denis Ievlev
 * @author Samer Hadeed
 */
public class Channel implements Runnable {

    private Socket socket;
    private boolean running;
    private String name = null;
    private ServerModel serverModel;
    private ServerView serverView;
    private Path pathToStore = Paths.get(System.getProperty("user.dir")).getParent();
    private Path tempFilePath= Paths.get(System.getProperty("user.dir")).getParent();
    /**
     * Class constructor
     *
     * @param socket      user's socket
     * @param serverModel {@link ServerModel} Model object
     * @param serverView  {@link ServerView} View object
     */
    public Channel(Socket socket, ServerModel serverModel, ServerView serverView) {
        this.socket = socket;
        this.serverModel = serverModel;
        this.serverView = serverView;
       // System.out.println("LOG: The image will be stored in "+pathToStore+"/images");

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
     *
     * @throws IOException when cannot close stream or current thread
     */
    public void stop() {
        running = false;

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.print("Failed to close the socket");
        }
    }

    @Override
    public void run() {
        try {
            running = true;
            while (running) {
                try {
                    saveFile();
                    stop();
                    System.out.println("LOG: Socket closed");
                    serverView.appendMessage("Disconnected from client");
                } catch (NoSuchElementException e) {
                    System.err.println(name + " channel has been closed");
                    e.getStackTrace();
                    //disconnectMessageRequest(); unimplemented feature
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveFile() throws IOException {
        serverView.appendMessage("Receiving image...");
        int filesize = 10000000;
        long start = System.currentTimeMillis();
        //int bytesRead;
       // int current = 0;
        //byte[] mybytearray = new byte[filesize];
        DataInputStream in = new DataInputStream(socket.getInputStream());

        //TODO have to check the folder path
        // destination path and name of file

        System.out.println("LOG: The image will be stored in "+pathToStore+"/images directory");
       // try (FileOutputStream fos = new FileOutputStream(pathToStore + "/images/image.jpg")) {
        try (FileOutputStream fos = new FileOutputStream(pathToStore + "/temp/image.jpg")) {
            //  FileOutputStream fos = new FileOutputStream("path_to_store_image/image.jpg"); //for other folder
            int i;
            while ((i = in.read()) > -1) {
                fos.write(i);
            }
            fos.close();
            in.close();
        }

        File source =new File(pathToStore.toString()+"/temp/image.jpg");
        File dest =new File(pathToStore.toString()+"/images/image.jpg");

        // Files.copy(pathToStore+"temp/image.jpg", String.format("%s/images/image.jpg", pathToStore));
        serverView.appendMessage("Image received and saved in /temp folder");
        copy(source,dest);
    }

    private static void copy(File source,File dest){
        try {
            Files.copy(source.toPath(),dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Handles the received disconnecting message from client.<br>
     * Removes the user from online users list.
     */

    @SuppressWarnings("unused")
    /**
     * The function is not in use.
     */
    private void disconnectMessageRequest() {
        if (name != null) {
            serverModel.removeClient(name);
            serverView.appendMessage("Client " + name + " disconnected\n");
        }

        serverModel.removeChannel(this);
        stop();
    }
}
