package it.polimi.ingsw.network.messages;

public class RequestDisconnectMessage extends Message {

    private final String reason;

    public RequestDisconnectMessage(String reason) {
        super();
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
