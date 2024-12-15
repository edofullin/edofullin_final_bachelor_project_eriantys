package it.polimi.ingsw.server.gamephases;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.server.Client;
import it.polimi.ingsw.server.IConnectionManager;
import it.polimi.ingsw.server.MatchController;

/**
 * This interface is used to define the behaviour of a game phase.
 **/
public interface IGamePhase {

    /**
     * actions to make before receiving responses from clients to decide what is the next game phase
     * CALLED FROM THE MAIN CONTROLLER THREAD, it blocks the thread after unless it is unblocked on this function
     *
     * @param matchController   the current state of the controller
     * @param connectionManager the connection manager
     */
    void preAction(MatchController matchController, IConnectionManager connectionManager);

    /**
     * Invoked when a message is recevied from a client
     * CALLED FROM THE THREAD OF THE SPECIFIC CLIENT
     *
     * @param matchController the current state of the controller
     * @param client          the client that ha sent the message
     * @param message         the message
     */
    void messageReceived(MatchController matchController, Client client, Message message);

    /**
     * Actions to perform after the next phase has been decided
     * CALLED FROM THE MAIN CONTROLLER THREAD
     *
     * @param matchController   the current state of the controller
     * @param connectionManager the connection manager
     */
    void postAction(MatchController matchController, IConnectionManager connectionManager);

    /**
     * @return the next game phase already ad an object
     */
    IGamePhase nextPhase(MatchController matchController);
}
