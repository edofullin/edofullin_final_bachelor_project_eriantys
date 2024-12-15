package it.polimi.ingsw.network.messages;

public class CreateNewGameResponseMessage extends Message {

    private final boolean success;
    private final int portNumber;

    public CreateNewGameResponseMessage(long messageId, boolean success, int portNumber) {
        super(messageId);
        this.success = success;
        this.portNumber = portNumber;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getPortNumber() {
        return portNumber;
    }
}
