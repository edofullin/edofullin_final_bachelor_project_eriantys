package it.polimi.ingsw.server.gamephases;

import it.polimi.ingsw.model.AssistCard;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.server.Client;
import it.polimi.ingsw.server.IConnectionManager;
import it.polimi.ingsw.server.MatchController;

import java.util.ArrayList;
import java.util.List;

public class Preparation implements IGamePhase {

    private final List<AssistCard> playedCards;
    private Client currentClient;
    private int numClients;

    public Preparation(List<AssistCard> playedCards) {
        this.playedCards = playedCards;
    }

    @Override
    public void preAction(MatchController matchController, IConnectionManager connectionManager) {
        currentClient = matchController.getQueue().get(playedCards.size()); // potrebbe andare a fuoco
        numClients = connectionManager.getClients().size();

        connectionManager.broadcastMessage(new TurnChangedMessage(currentClient.getId())); // inform all clients who plays now
    }

    @Override
    public void messageReceived(MatchController matchController, Client client, Message message) {

        if (message instanceof PlayCardMessage psMessage) {

            if (client.equals(currentClient)) {
                // carte in mano del giocatore
                ArrayList<AssistCard> availableCards = new ArrayList<>(List.of(matchController.getGameModel().getPlayers().get(currentClient.getId()).getHand().toArray(new AssistCard[0])));

                // tolgo quelle già giocate in preparation
                for (AssistCard card : playedCards) {
                    availableCards.remove(card);
                }

                // se il giocatore potva giocare la carta che ha giocato
                if (availableCards.contains(psMessage.getPlayedCard()) || (availableCards.size() == 0 && playedCards.contains(psMessage.getPlayedCard()))) {
                    // risponde con un check-ok
                    playedCards.add(psMessage.getPlayedCard());
                    matchController.getGameModel().getPlayers().get(currentClient.getId()).removeCard(psMessage.getPlayedCard());
                    matchController.getGameModel().getGameBoard().getGraveyard(currentClient.getId()).addCard(psMessage.getPlayedCard());
                    client.sendMessageAsync(new EvaluateMoveMessage(message.getMessageId(), EvaluateMoveMessage.MoveResponse.MOVE_OK, "ok", matchController.getStrippedGameModel()));
                    matchController.releasePhase(); // possiamo procedere signora mia
                } else {
                    client.sendMessageAsync(new EvaluateMoveMessage(message.getMessageId(), EvaluateMoveMessage.MoveResponse.MOVE_KO, "Card non available", matchController.getStrippedGameModel()));
                }
            }

            // gestisci altri giocatori che fanno i furbi... o non gestirli

        }

        // gestisci altri tipi di messaggi inaspettati

    }

    @Override
    public void postAction(MatchController matchController, IConnectionManager connectionManager) {
        connectionManager.broadcastMessageExcept(currentClient, new ModelUpdateMessage(matchController.getStrippedGameModel(currentClient)));
        if (playedCards.size() == numClients) {
            matchController.setQueue(playedCards);
        }
    }

    @Override
    public IGamePhase nextPhase(MatchController matchController) {
        if (playedCards.size() == numClients) {
            return new PlayerMoveStudents(0, matchController.getQueue().get(0));
        } else return new Preparation(playedCards);
    }
}
