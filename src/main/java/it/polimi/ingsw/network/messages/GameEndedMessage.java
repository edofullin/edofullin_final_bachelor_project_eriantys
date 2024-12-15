package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.Game;

public class GameEndedMessage extends Message {

    final private int winnerID;
    final private Game.WinningConditions winningCondition;

    public GameEndedMessage(int winnerID, Game.WinningConditions winningCondition) {
        super();
        this.winnerID = winnerID;
        this.winningCondition = winningCondition;
    }

    public GameEndedMessage(long messageId, int winnerID, Game.WinningConditions winningCondition) {
        super(messageId);
        this.winnerID = winnerID;
        this.winningCondition = winningCondition;
    }

    public int getWinnerID() {
        return this.winnerID;
    }

    public Game.WinningConditions getWinningCondition() {
        return winningCondition;
    }
}
