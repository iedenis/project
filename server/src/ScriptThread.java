
import java.io.*;

/**
 * @author Denis Ievlev
 * @author Samer Hadeed
 */
public class ScriptThread implements Runnable {

    private ServerView serverView;
    private ServerModel serverModel;
    private Process pr = null;
    private String[] mainScript = {"/home/fox/Project/main.sh"};
    private BufferedReader reader;
    private boolean running;

    public ScriptThread(ServerModel serverModel, ServerView serverView) {
        this.serverView = serverView;
        this.serverModel = serverModel;
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.setName("Writing from script");
        thread.start();
    }

    public void stop() {

        pr.destroyForcibly();
        running = false;
    }

    @Override
    public void run() {
        running=true;
        try {
            ProcessBuilder builder = new ProcessBuilder(mainScript);
            builder.redirectErrorStream(true);
            pr=builder.start();
           // pr = Runtime.getRuntime().exec(mainScript);
            serverView.appendMessage("LOG: Starting the main script...\n");
        } catch (IOException e)

        {
            serverView.appendMessage("The script file doesn't exist in this folder");
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
