package it.polimi.ingsw.network.messages;

public class ClientDisconnectMessage extends Message {

    final private int playerID;

    public ClientDisconnectMessage(int playerID) {
        this.playerID = playerID;
    }

    public ClientDisconnectMessage(long messageId, int playerID) {
        super(messageId);
        this.playerID = playerID;
    }

    public int getPlayerID() {
        return playerID;
    }
}
