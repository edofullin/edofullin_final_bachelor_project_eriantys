package it.polimi.ingsw.network.messages;

public class MoveMotherNatureMessage extends Message {

    final private int moves;

    public MoveMotherNatureMessage(int moves) {
        this.moves = moves;
    }

    public MoveMotherNatureMessage(long messageId, int moves) {
        super(messageId);
        this.moves = moves;
    }

    public int getMoves() {
        return moves;
    }
}
