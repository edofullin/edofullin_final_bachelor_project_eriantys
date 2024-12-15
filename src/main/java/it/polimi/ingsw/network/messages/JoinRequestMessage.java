package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.Player;

public class JoinRequestMessage extends Message {

    final private String playerName;
    final private Player.Magician mage;

    public JoinRequestMessage(String playerName, Player.Magician playerMagician) {
        super();
        this.playerName = playerName;
        this.mage = playerMagician;
    }

    public JoinRequestMessage(long messageId, String playerName, Player.Magician playerMagician) {
        super(messageId);
        this.playerName = playerName;
        this.mage = playerMagician;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public Player.Magician getPlayerMagician() {
        return this.mage;
    }
}
