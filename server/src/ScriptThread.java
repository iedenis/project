import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Denis Ievlev
 * @author Samer Hadeed
 */
public class ScriptThread implements Runnable {

    private ServerView serverView;
    private ServerModel serverModel;
    private Process pr = null;
    private String[] mainScript = {"main.sh"};
    private BufferedReader reader;
    private boolean running;

    /**
     * Constructor for Script Thread
     *
     * @param serverModel {@link ServerModel} Model object
     * @param serverView  {@link ServerView} The server GUI
     */
    public ScriptThread(ServerModel serverModel, ServerView serverView) {
        this.serverView = serverView;
        this.serverModel = serverModel;
    }

    /**
     * Thread start function
     */
    public void start() {
        Thread thread = new Thread(this);
        thread.setName("Writing from script");
        thread.start();
    }

    /**
     * Thread stop method
     * kills the running script
     */
    public void stop() {
        if (pr != null) {
            pr.destroyForcibly();
            pr.destroy();
        }
        running = false;
    }

    @Override
    public void run() {
        running = true;
        try {
            ProcessBuilder builder = new ProcessBuilder(mainScript);
            builder.redirectErrorStream(true);
            if (pr != null) {
                serverView.appendMessage("LOG: Starting the main script...\n");
                pr = builder.start();
            } else {
                serverView.appendMessage("The script file doesn't exist in this folder");
                this.stop();
            }

            // pr = Runtime.getRuntime().exec(mainScript);
        } catch (IOException e)

        {
            System.err.println("Wrong script folder");
        }


        reader = new BufferedReader(new InputStreamReader(
                pr.getInputStream()));
        String s;
        try

        {
            while ((s = reader.readLine()) != null && running)
                serverView.appendMessage("Script output: " + s);
        } catch (IOException e)

        {
            e.printStackTrace();
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
