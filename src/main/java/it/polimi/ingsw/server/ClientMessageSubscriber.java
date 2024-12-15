package it.polimi.ingsw.server;

import it.polimi.ingsw.network.messages.Message;

/**
 * Callback for received messages from client.
 */
public interface ClientMessageSubscriber {

    void receivedClientMessageEvent(Client c, Message m);

}
