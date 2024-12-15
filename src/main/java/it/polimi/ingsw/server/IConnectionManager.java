package it.polimi.ingsw.server;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.messages.Message;

import java.awt.event.ActionListener;
import java.util.List;

/**
 * Represents a connection manager
 */
public interface IConnectionManager {

    void sendMessageToClientAsync(Client c, Message m);

    Message sendMessageToClient(Client c, Message m) throws InterruptedException;

    List<Client> getClients();

    void waitClients() throws InterruptedException;

    void registerMessageSubscriber(ClientMessageSubscriber subs);

    void broadcastMessage(Message m);

    void broadcastMessageExcept(Client c, Message m);

    void registerActionListener(ActionListener listener);

    void disconnectAllClients();

    Client getClient(Player p);

    void halt();
}
