import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Denis Ievlev
 * @author Samer Hadeed
 */
public class ScriptThread implements Runnable {

    private ServerView serverView;
    private ServerModel serverModel;
    private Process pr = null;
    //private String[] mainScript = {"your_path_to_the_script/main.sh"}; could be use in case the different path of the script
    private BufferedReader reader;
    private boolean running;
    private Path scriptPath = Paths.get(System.getProperty("user.dir")).getParent();


    /**
     * Constructor for Script Thread class
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
     * Sometimes there is a problem to kill the process. I think it's because of the Java's bug
     * appearing here  http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4770092
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
        String path = scriptPath.toString();

        //Option to choose between European pattern and Israeli pattern. By default it is the european pattern
        //because we have to continue training the ALPR program for better results.
        String options[] = {"European", "Israeli"};
        Object selected = JOptionPane.showInputDialog(null, "Choose the license plate pattern", "Selection", JOptionPane.DEFAULT_OPTION, null, options, "0");
        String selectedString = selected.toString();
        System.out.println("User chose " + selected);

        if (selectedString.equals(options[0])) selectedString = "eu";
        else if (selectedString.equals(options[1])) selectedString = "il";
        else this.stop(); //in case the user canceled

        //if executed with jar file located in /Project/jar_files
        if (!path.contains("/Project"))
            path = System.getProperty("user.dir").concat("/Project");

        try {
            serverView.appendMessage("LOG: executed from directory: " + path);
            //Creates a new process that runs the script with the chosen parameter (european or israeli pattern)
            ProcessBuilder builder = new ProcessBuilder(path + "/main.sh", selectedString);
            builder.redirectErrorStream(true);
            pr = builder.start();
            serverView.appendMessage("LOG: Starting the main script from directory " + path);

        } catch (IOException e)

        {

            System.err.println("Wrong script folder");
            serverView.appendMessage("The script file doesn't exist in folder " + path);
            this.stop();

        }

        if (running) {
            reader = new BufferedReader(new InputStreamReader(
                    pr.getInputStream()));
            String s;

            try {
                while ((s = reader.readLine()) != null && running) {
                    serverView.appendMessage("Script output: " + s);
                    //The program server.jar executed not from terminal
                }
            } catch (IOException e) {
                this.stop();
                System.err.println("Failed to read from the script");
                this.serverView.appendMessage("Failed to read from the main.sh\nServer is going down...");
                //e.printStackTrace();
            }
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
