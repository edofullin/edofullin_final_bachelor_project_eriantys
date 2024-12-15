package it.polimi.ingsw.server;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.messages.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class handles a single client
 */
public abstract class Client {

    private static final Logger logger = LogManager.getLogger(Client.class);
    private final int id;
    private final boolean isGM;
    protected ClientMessageSubscriber subscriber;
    private ClientState status;
    private Player gamePlayer;

    public Client(int id, boolean isGM) {
        this.id = id;
        this.isGM = isGM;
        this.status = ClientState.UNKNOWN;
    }

    public int getId() {
        return id;
    }

    public boolean isGM() {
        return isGM;
    }

    public ClientState getStatus() {
        return status;
    }

    public void setStatus(ClientState status) {
        this.status = status;
    }

    public void registerMessageSubscriber(ClientMessageSubscriber subscriber) {
        logger.debug("registered message subscriber for client %d".formatted(id));
        this.subscriber = subscriber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Client client = (Client) o;

        return id == client.id;
    }

    public Player getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(Player gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    @Override
    public int hashCode() {
        return id;
    }


    /**
     * Send a message to the client.
     *
     * @param m the message to send
     */
    public abstract void sendMessageAsync(Message m);

    /**
     * Send a message to the client and wait for a response.
     *
     * @param m the message to send
     * @return the response message
     */
    public abstract Message sendMessage(Message m) throws InterruptedException;

    /**
     * Disconnect this client.
     *
     * @param reason the reason for disconnecting
     */
    public abstract void disconnect(String reason);

    /**
     * Disconnect this client.
     */
    public abstract void disconnect();

    public enum ClientState {
        CONNECTING,
        CONNECTED,
        CONNECTION_LOST,
        DISCONNECTED,
        UNKNOWN
    }

}
