package it.polimi.ingsw.server.gamephases;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.ModelUpdateMessage;
import it.polimi.ingsw.server.Client;
import it.polimi.ingsw.server.IConnectionManager;
import it.polimi.ingsw.server.MatchController;

import java.util.ArrayList;

import static it.polimi.ingsw.Support.logger;

public class SetUpTurn implements IGamePhase {

    private Game.WinNotification winner;

    @Override
    public void preAction(MatchController matchController, IConnectionManager connectionManager) {
        winner = matchController.getGameModel().checkGameOver();

        matchController.getGameModel().getGameBoard().addStudentsClouds();
        matchController.releasePhase();
    }

    @Override
    public void messageReceived(MatchController matchController, Client client, Message message) {
        // this phase receives no client's message
    }

    @Override
    public void postAction(MatchController matchController, IConnectionManager connectionManager) {
        connectionManager.broadcastMessage(new ModelUpdateMessage(matchController.getStrippedGameModel()));
    }

    @Override
    public IGamePhase nextPhase(MatchController matchController) {
        if (winner != null && (winner.condition() == Game.WinningConditions.EMPTY_POUCH || winner.condition() == Game.WinningConditions.EMPTY_HAND)) {
            logger.debug("Detected %d winner".formatted(winner.winner().getId()));
            return new GameEnded(winner);
        }

        return new Preparation(new ArrayList<>());
    }
}
