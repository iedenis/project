import java.util.*;

/**
 * The class {@link ServerModel} represents the model part<br>
 * of the MVC pattern.
 *
 * @author Alexey Kurbatsky
 */
public class ServerModel {
    private Vector<Channel> channels = new Vector<>();
    private Hashtable<String, Channel> clients = new Hashtable<>();
    /**
     * Adds new channel to the channels vector
     *
     * @param channel new channel
     */
    public void addChannel(Channel channel) {
        channels.add(channel);
    }

    /**
     * Removes channel from the channels vector
     *
     * @param channel channel to remove
     */
    public void removeChannel(Channel channel) {
        channels.remove(channel);
    }

    /**
     * Getter of channels
     *
     * @return channels vector
     */
    public Vector<Channel> getChannels() {
        return channels;
    }


    /**
     * Drops channels vector
     */
    public void dropChannels() {
        channels.clear();
    }

    /**
     * Getter of clients
     *
     * @return clients vector
     */
    public Hashtable<String, Channel> getClients() {
        return clients;
    }

    /**
     * Returns client from clients hash table by keu
     *
     * @param name key
     * @return value
     */
    public Channel getClient(String name) {
        return clients.get(name);
    }

    /**
     * Adds new client to clients hash table
     *
     * @param key   key
     * @param value value
     */
    public void putClient(String key, Channel value) {
        clients.put(key, value);
    }

    /**
     * Removes client from clients hash table by key
     *
     * @param key key
     */
    public void removeClient(String key) {
        clients.remove(key);
    }

    /**
     * Drops clients hash table
     */
    public void dropClients() {
        clients.clear();
    }
}
