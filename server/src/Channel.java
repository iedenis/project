import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * @author Denis Ievlev
 * @author Samer Hadeed
 */
public class Channel implements Runnable {
    private Socket socket;
    private Scanner reader;
    //private PrintWriter writer;
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
     *
     * @throws IOException when cannot close stream or current thread
     */
    public void stop() throws IOException {
        running = false;

//        writer.close();
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
            running = true;
            while (running) {
                try {
                    saveFile();
                    socket.close();
                    System.out.println("LOG: Socket closed");
                } catch (NoSuchElementException e) {
                    System.err.println(name + " channel has been closed");
                    e.getStackTrace();
                    disconnectMessageRequest();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveFile() throws IOException {
        int filesize = 10000000;
        long start = System.currentTimeMillis();
        int bytesRead;
        int current = 0;
        byte[] mybytearray = new byte[filesize];
        DataInputStream in = new DataInputStream(socket.getInputStream());

        // destination path and name of file
        FileOutputStream fos = new FileOutputStream("image.jpg");
        int i;
        while ((i = in.read()) > -1) {
            fos.write(i);
        }
    }



/* accepting file code

//Accept File
            System.out.println("Connected");

            //receive code
            int filesize=450660;
            int bytesRead;
            int current=0;
            // receive file
            byte [] mybytearray  = new byte [filesize];
            InputStream is = sock.getInputStream();
            FileOutputStream fos = new FileOutputStream("C:\\Project Server\\Capture.png");
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bytesRead = is.read(mybytearray,0,mybytearray.length);
            current = bytesRead;

            do {
               bytesRead =
                  is.read(mybytearray, current, (mybytearray.length-current));
               if(bytesRead >= 0) current += bytesRead;
            } while(bytesRead > -1);

            bos.write(mybytearray, 0 , current);
            bos.flush();

            System.out.println("end-start");
        }
        catch(Exception e)
        {
            System.out.println(e);
            e.printStackTrace();
        }

    }
 */


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
            System.err.println("Failed to stop the channel thread for user: " + name);
        }
    }

}
