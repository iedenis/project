import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;

/**
 * Represents the View part of the MVC pattern.<br>
 * This is a GUI for the server
 *
 * @author Alexey Kurbatsky
 * @author Denis Ievlev
 */
public class ServerView extends JFrame {
    private JPanel mainPanel;
    private JButton serverStateTurnButton;
    private boolean serverStateRunning = false;
    private JTextArea systemMessagesArea;
    private JScrollPane scroll;

    public ServerView() {
       // mainPanel.setLayout(new BorderLayout(0,0));
        //scroll = new JScrollPane();
        //systemMessagesArea = new JTextArea();
        systemMessagesArea.setEditable(false);

       // scroll.setViewportView(systemMessagesArea);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       // add(scroll);
        add(mainPanel);

        setTitle("Server");
    }

    /**
     * Displaying the text in text view.
     *
     * @param text String message.
     */
    public void appendMessage(String text) {
        systemMessagesArea.append(text + "\n");

        int contentLength = systemMessagesArea.getDocument().getLength();
        systemMessagesArea.setCaretPosition(contentLength);
    }

    /**
     * Sets a name for server button. (Connect/Disconnect button)
     *
     * @param text Button state
     */
    public void setButtonText(String text) {
        serverStateTurnButton.setText(text);
    }

    /**
     * return true when server is running
     *
     * @return boolean
     */
    public boolean isServerStateRunning() {
        return serverStateRunning;
    }

    /**
     * Sets the statement of the server: running/not running
     *
     * @param serverStateRunning boolean running - true not running - false
     */
    public void setServerStateRunning(boolean serverStateRunning) {
        this.serverStateRunning = serverStateRunning;
    }

    /**
     * Adding the listener
     *
     * @param listenForServerState
     */
    void addServerStateListener(MouseListener listenForServerState) {
        serverStateTurnButton.addMouseListener(listenForServerState);
    }

    /**
     * Adding the window listener for X button of the window.<br>
     * Stops the server and close all connection whe button is clicked
     *
     * @param serverWindowListener - window listener.
     */
    void addServerWindowListener(WindowListener serverWindowListener) {
        this.addWindowListener(serverWindowListener);
    }


}
