package it.polimi.ingsw.network.messages;

public class TurnChangedMessage extends Message {

    final private int currentTurnPlayerID;

    public TurnChangedMessage(int currentTurnPlayerID) {
        super();
        this.currentTurnPlayerID = currentTurnPlayerID;
    }

    public TurnChangedMessage(long messageId, int currentTurnPlayerID) {
        super(messageId);
        this.currentTurnPlayerID = currentTurnPlayerID;
    }

    public int getCurrentTurnPlayerID() {
        return currentTurnPlayerID;
    }
}
