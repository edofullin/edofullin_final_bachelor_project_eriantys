package it.polimi.ingsw.network.messages;

public class JoinResponseMessage extends Message {

    final private JoinReponseCode respCode;
    final private int playerID;

    public JoinResponseMessage(long messageId, JoinReponseCode respCode, int playerID) {
        super(messageId);
        this.respCode = respCode;
        this.playerID = playerID;
    }

    public JoinReponseCode getRespCode() {
        return respCode;
    }

    public int getPlayerID() {
        return playerID;
    }

    public enum JoinReponseCode {
        JOIN_OK,
        JOIN_FAIL,
        JOIN_FAIL_USERNAME_TAKEN,
        JOIN_FAIL_MAGE_TAKEN,
        JOIN_FAIL_LOBBY_FULL,
        JOIN_FAIL_UNEXPECTED_USERNAME
    }
}
