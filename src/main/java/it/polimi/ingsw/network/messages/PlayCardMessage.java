package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.AssistCard;

public class PlayCardMessage extends Message {

    final private AssistCard playedCard;

    public PlayCardMessage(AssistCard playedCard) {
        super();
        this.playedCard = playedCard;
    }

    public PlayCardMessage(long messageId, AssistCard playedCard) {
        super(messageId);
        this.playedCard = playedCard;
    }

    public AssistCard getPlayedCard() {
        return playedCard;
    }
}
