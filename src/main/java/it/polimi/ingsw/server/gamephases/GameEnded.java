package it.polimi.ingsw.server.gamephases;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.network.messages.GameEndedMessage;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.server.Client;
import it.polimi.ingsw.server.IConnectionManager;
import it.polimi.ingsw.server.MatchController;

public class GameEnded implements IGamePhase {

    private final Game.WinNotification winNotification;

    public GameEnded(Game.WinNotification win) {
        winNotification = win;
    }

    @Override
    public void preAction(MatchController matchController, IConnectionManager connectionManager) {
        connectionManager.broadcastMessage(new GameEndedMessage(winNotification.winner().getId(), winNotification.condition()));
        matchController.releasePhase();
    }

    @Override
    public void messageReceived(MatchController matchController, Client client, Message message) {

    }

    @Override
    public void postAction(MatchController matchController, IConnectionManager connectionManager) {
        matchController.deleteGameFile();

    }

    @Override
    public IGamePhase nextPhase(MatchController matchController) {
        return null;
    }
}
