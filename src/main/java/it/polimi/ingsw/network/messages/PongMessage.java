package it.polimi.ingsw.network.messages;

public class PongMessage extends Message {

    public PongMessage() {
    }

    public PongMessage(long messageId) {
        super(messageId);
    }
}
