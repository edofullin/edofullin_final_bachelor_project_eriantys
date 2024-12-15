package it.polimi.ingsw.server.gamephases;

import it.polimi.ingsw.model.characters.CharacterEnum;
import it.polimi.ingsw.network.messages.EvaluateMoveMessage;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.ModelUpdateMessage;
import it.polimi.ingsw.network.messages.MoveMotherNatureMessage;
import it.polimi.ingsw.server.Client;
import it.polimi.ingsw.server.IConnectionManager;
import it.polimi.ingsw.server.MatchController;

public class PlayerMoveMotherNature implements IGamePhase {

    final private Client currentClient;

    public PlayerMoveMotherNature(Client currentClient) {
        this.currentClient = currentClient;
    }

    @Override
    public void preAction(MatchController matchController, IConnectionManager connectionManager) {

    }

    @Override
    public void messageReceived(MatchController matchController, Client client, Message message) {

        if (!client.equals(currentClient)) {
            client.sendMessageAsync(new EvaluateMoveMessage(EvaluateMoveMessage.MoveResponse.MOVE_KO, matchController.getGameModel()));
            return; // non dovevi giocare tu pirla
        }

        // la carta giocata da il numero massimo di mosse
        int maxMoves = matchController.getGameModel()
                .getGameBoard().getGraveyard(currentClient.getId())
                .getTopCard().getMoves();
        if (matchController.getGameModel().getGameBoard().getActiveCharacter() != null && matchController.getGameModel().getGameBoard().getActiveCharacter().getEnumType() == CharacterEnum.POSTMAN)
            maxMoves += 2;

        if (message instanceof MoveMotherNatureMessage mmnMessage) {

            if (mmnMessage.getMoves() < 1 || mmnMessage.getMoves() > maxMoves) {
                // no maria non si può vada a far altro
                client.sendMessageAsync(new EvaluateMoveMessage(mmnMessage.getMessageId(), EvaluateMoveMessage.MoveResponse.MOVE_KO, "Invalid number of moves", matchController.getGameModel()));
                return;
            }

            matchController.getGameModel().getGameBoard().moveMotherNature(mmnMessage.getMoves());
            matchController.getGameModel().getGameBoard().updateInfluence(matchController.getStrippedGameModel().getCurrentIsland());

            client.sendMessageAsync(new EvaluateMoveMessage(mmnMessage.getMessageId(), EvaluateMoveMessage.MoveResponse.MOVE_OK, null, matchController.getGameModel()));
            matchController.releasePhase();
        }

    }

    @Override
    public void postAction(MatchController matchController, IConnectionManager connectionManager) {
        // informo tutti tranne current del nuovo model (eccetto current che lo ha già ricevuto nella evaluatemove)
        connectionManager.broadcastMessageExcept(currentClient, new ModelUpdateMessage(matchController.getStrippedGameModel()));
    }

    @Override
    public IGamePhase nextPhase(MatchController matchController) {
        return new PlayerTakesCloud(currentClient);
    }
}
