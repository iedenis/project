import sun.plugin2.util.SystemUtil;

import javax.swing.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The main class of the server.<br>
 * Server is written in Model–view–controller design pattern<br>
 * where the controller connects between the view and the model
 *
 * @author Denis Ievlev
 * @author Samer Hadeed
 */

public class ServerMain {
    public static void main(String[] args) {

        Path path=Paths.get(System.getProperty("user.dir"));
        System.out.println(path);
        System.out.println(path.getParent());

        ServerView serverView = new ServerView();
        ServerModel serverModel = new ServerModel();
        @SuppressWarnings("unused")
        ServerController serverController = new ServerController(serverModel, serverView);
        serverView.setSize(700, 500);
        serverView.setLocationRelativeTo(null);
        serverView.setVisible(true);

        //Checking the OS, because the server can run only on the Linux Operating system
        if(!System.getProperty("os.name").equals("Linux")){
            JOptionPane.showMessageDialog(null,"This program can run only on GNU/Linux OS");
            System.exit(0);
        }
    }
}
