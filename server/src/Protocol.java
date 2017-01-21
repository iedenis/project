/**
 * @author Denis Ievlev
 * @author Alexey Kurbatsky
 */
 /**
 * The class is represents a protocol for the messages that 
 * server and client send and receive.<br>
 * Also it parses a receiving messages and creats messages to send<br>
 * All methods of this class are static
 */
public class Protocol {

	public static final int broadcastMessage = 0, privateMessage = 1, connectRequestMessage = 2, disconnectMessage = 3,
			refreshOnlineUsers = 4, serverMessage = 5;
	
	/**
	 * Returns a type of the message:<br>
	 * 
	 * @param message is a String message that created by {@link #createMessage(int, String, String)}
	 * @return type such as:<br>
	 * broadcastMessage<br>
	 * privateMessage <br>
	 * connectRequestMessage<br>
	 * disconnectMessage<br>
	 * refreshOnlineUsers<br>
	 * serverMessage
	 */
	public static int getType(String message) {
		return Character.getNumericValue(message.charAt(0));
	}

	/**
	 * Creates a new message to send either to the server or to client.
	 * @param type the tyep of the sending message.
	 * @param from from whom sent the message (username).
	 * @param message the sending message.
	 * @return message with header
	 */
	public static String createMessage(int type, String from, String message) {
		return type + from+":" + message;
	}

	// result from connection request
	// answer = "success"/"fail"
	/**
	 * Setting the result message from "connectRequest" message.<br>
	 * The answer can be success when the connection established successfully<br>
	 * or fail when the requested username is in use by other user.
	 * @param ParsedMessage message parsed by {@link Protocol#parseMessage(String)}
	 * @param answer The answer from the server
	 */
	
	public static void setResultFromServer(String[] ParsedMessage, String answer) {
		ParsedMessage[1] = answer;
	}

	/**
	 * Method parses the received message either from client or server.
	 * @param message is the coded message by {{@link #createMessage(int, String, String)}
	 * @return returns an array of answers.
	 */
	public static String[] parseMessage(String message) {
		int messageType = getType(message);
		String[] res = new String[3];
		switch (messageType) {
		case broadcastMessage:
			// res[0] -> message
			// res[1] -> from
			String str[] = message.split(":", 2);
			res[0] = str[1];
			res[1] = str[0].substring(2);
			return res;
		case privateMessage:
			// res[0] -> message
			// res[1] -> from
			// res[2] -> to
			String[] s = message.split(":", 3);
			res[1] = s[0].substring(2);
			res[2] = s[1].substring(1);
			res[0] = s[2];
			return res;

		// all users separated by comma (,) in one String
		case refreshOnlineUsers:
			res[0] = message.substring(2);
			return res;

		case connectRequestMessage:
			// res[0] the requested name from client
			// res[1] the answer from server "success"/"fail"
			// res[2] the message from server (like try to choose another username)
			String[] st = message.split(":", 2);
			res[0] = st[0].substring(1);
			res[1] = st[1];
			return res;

		// nothing need to do. The client receive the disconnecting message and
		// disconnecting from chat
		case disconnectMessage:
			//res[0]=message.substring(1);
			return res;
		case serverMessage:
			res[0] = message.substring(1);
			return res;
		default:
			res[0] = "error";
			return res;
		}
	}
}
