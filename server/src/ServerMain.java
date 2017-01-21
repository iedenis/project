import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

        ServerView serverView = new ServerView();
        ServerModel serverModel = new ServerModel();
        @SuppressWarnings("unused")
        ServerController serverController = new ServerController(serverModel, serverView);
        serverView.setSize(700,500);
        //serverView.pack();

        serverView.setLocationRelativeTo(null);
        serverView.setVisible(true);

    }
}
