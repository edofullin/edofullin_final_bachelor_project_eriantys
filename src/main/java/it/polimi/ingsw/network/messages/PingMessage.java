package it.polimi.ingsw.network.messages;

public class PingMessage extends Message {

    public PingMessage() {
    }

    public PingMessage(long messageId) {
        super(messageId);
    }
}
