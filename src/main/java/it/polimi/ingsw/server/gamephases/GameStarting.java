package it.polimi.ingsw.server.gamephases;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.ClientGameState;
import it.polimi.ingsw.network.messages.GameStartedMessage;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.server.Client;
import it.polimi.ingsw.server.IConnectionManager;
import it.polimi.ingsw.server.MatchController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class GameStarting implements IGamePhase {

    private final Logger logger = LogManager.getLogger(GameStarting.class);

    @Override
    public void preAction(MatchController matchController, IConnectionManager connectionManager) {

        for (Client client : connectionManager.getClients()) {

            ClientGameState clientState = ClientGameState.CHOOSE_ASSIST;

            if (!matchController.getGameSettings().isFromDisk()) {
                client.sendMessageAsync(new GameStartedMessage(matchController.getGameModel(), false, clientState));
                continue;
            }

            List<Player> queue = matchController.getGameModel().getQueue();
            Player currentPlayer = matchController.getGameModel().getCurrentTurn();

            // find index of currentPlayer in queue
            int index = queue.indexOf(currentPlayer);
            int cindex = queue.indexOf(client.getGamePlayer());

            logger.debug("Calculating client state: index: %d, cindex: %d".formatted(index, cindex));

            if (cindex >= index) {
                // if client is ahead of current player, he must move students
                clientState = ClientGameState.MOVE_STUDENTS;
            }

            client.sendMessageAsync(new GameStartedMessage(matchController.getGameModel(), true, clientState));
        }

        matchController.releasePhase();
    }

    @Override
    public void messageReceived(MatchController matchController, Client client, Message message) {
        // this phase receives no client's message
    }

    @Override
    public void postAction(MatchController matchController, IConnectionManager connectionManager) {
        // this phase performs no postAction
    }

    @Override
    public IGamePhase nextPhase(MatchController matchController) {
        int curr = -1;

        for (int i = 0; i < matchController.getQueue().size(); i++) {
            if (matchController.getQueue().get(i).getGamePlayer().equals(matchController.getGameModel().getCurrentTurn()))
                curr = i;
        }

        if (matchController.isLoadedFromDisk()) {
            return new PlayerMoveStudents(0, matchController.getQueue().get(curr));
        } else {
            return new SetUpTurn();
        }
    }
}
