package it.polimi.ingsw.client;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.messages.EvaluateMoveMessage;
import it.polimi.ingsw.network.messages.JoinResponseMessage;

import java.io.IOException;

public interface IView {

    void beginRequestLoginInfo(JoinResponseMessage.JoinReponseCode respCode);

    /**
     * Request an assistant card to be played from the view
     *
     * @param b true if this is a retry
     */
    void beginRequestAssistantCard(boolean b);

    /**
     * Request a character card to be played from the view
     *
     * @param b not null if this is a retry, with the reason it failed
     */
    void beginRequestCharacter(String b);

    /**
     * Request a student to be played from the view
     *
     * @param code   not null if this is a retry, with the reason it failed
     * @param reason not null if this is a retry, with the reason it failed
     */
    void beginRequestStudentMove(EvaluateMoveMessage.MoveResponse code, String reason);

    /**
     * Request mother nature to be played from the view
     *
     * @param b true if this is a retry
     */
    void beginRequestMotherNatureMoves(boolean b);

    /**
     * Request the view to select a cloud
     *
     * @param b true if this is a retry
     */
    void beginRequestCloud(boolean b);

    /**
     * Informs the view that the model has been updated
     *
     * @param model the new model
     */
    void modelUpdated(Game model);

    /**
     * Informs the view that the turn has changed
     *
     * @param currentPlayerTurn the current turn player
     */
    void turnChanged(Player currentPlayerTurn);

    /**
     * Informs the view that the game has ended
     *
     * @param winner the winner of the game
     */
    void gameEnded(Player winner);

    void start() throws IOException;

    /**
     * Informs the view that the login worked
     */
    void notifyLoginOk();

    /**
     * Request the view to quit the game
     */
    void exitGame(String message);

    /**
     * Informs the view that the game has started
     */
    void notifyGameStarted();
}
