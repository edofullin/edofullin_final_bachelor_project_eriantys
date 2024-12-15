package it.polimi.ingsw.server.gamephases;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.characters.CharacterCard;
import it.polimi.ingsw.network.messages.ChooseCloudMessage;
import it.polimi.ingsw.network.messages.EvaluateMoveMessage;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.ModelUpdateMessage;
import it.polimi.ingsw.server.Client;
import it.polimi.ingsw.server.IConnectionManager;
import it.polimi.ingsw.server.MatchController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerTakesCloud implements IGamePhase {

    final private Client currentClient;
    Logger logger = LogManager.getLogger(PlayerTakesCloud.class);
    private Game.WinNotification winner;

    public PlayerTakesCloud(Client currentClient) {
        this.currentClient = currentClient;
    }


    @Override
    public void preAction(MatchController matchController, IConnectionManager connectionManager) {

    }

    @Override
    public void messageReceived(MatchController matchController, Client client, Message message) {

        if (!client.equals(currentClient)) {
            client.sendMessageAsync(new EvaluateMoveMessage(EvaluateMoveMessage.MoveResponse.MOVE_KO, matchController.getStrippedGameModel()));
            return; // non dovevi giocare tu pirla
        }

        if (message instanceof ChooseCloudMessage ccMessage) {

            if (ccMessage.getCloudID() < 0 || ccMessage.getCloudID() > matchController.getQueue().size() - 1) {
                // no maria non si può vada a far altro
                client.sendMessageAsync(new EvaluateMoveMessage(ccMessage.getMessageId(), EvaluateMoveMessage.MoveResponse.MOVE_KO, "Invalid cloud number", matchController.getStrippedGameModel()));
                return;
            }

            if (matchController.getGameModel().getGameBoard().getCloud(ccMessage.getCloudID()).getStudents().size() == 0 &&
                    matchController.getGameModel().getGameBoard().getPouch().size() > 0) {
                client.sendMessageAsync(new EvaluateMoveMessage(ccMessage.getMessageId(), EvaluateMoveMessage.MoveResponse.MOVE_KO, "cloud is empty", matchController.getStrippedGameModel())); //la cloud è vuota
                return;
            }

            matchController.getGameModel().giveCloudToPlayer(
                    ccMessage.getCloudID(), client.getGamePlayer()
            );

            client.sendMessageAsync(new EvaluateMoveMessage(ccMessage.getMessageId(), EvaluateMoveMessage.MoveResponse.MOVE_OK, null, matchController.getStrippedGameModel()));
            matchController.releasePhase();
        }

    }

    @Override
    public void postAction(MatchController matchController, IConnectionManager connectionManager) {

        CharacterCard card = matchController.getGameModel().getGameBoard().getActiveCharacter();
        if (card != null) card.setActive(false);

        // informo tutti tranne current del nuovo model (eccetto current che lo ha già ricevuto nella evaluatemove)
        connectionManager.broadcastMessageExcept(currentClient, new ModelUpdateMessage(matchController.getStrippedGameModel()));

        winner = matchController.getGameModel().checkGameOver();
    }

    @Override
    public IGamePhase nextPhase(MatchController matchController) {

        if (winner != null && (winner.condition() == Game.WinningConditions.NO_TOWERS || winner.condition() == Game.WinningConditions.TOO_FEW_ISLANDS)) {
            logger.debug("Detected %d winner".formatted(winner.winner().getId()));
            return new GameEnded(winner);
        }

        if (matchController.getQueue().indexOf(currentClient) < (matchController.getQueue().size() - 1)) {
            return new PlayerMoveStudents(0, matchController.getQueue().get(matchController.getQueue().indexOf(currentClient) + 1));
        }

        logger.debug("Changing turn");

        return new SetUpTurn();
    }
}
