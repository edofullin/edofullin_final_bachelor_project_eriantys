package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import org.jetbrains.annotations.NotNull;

// QUINTULTIMO
public class KnightCharacter extends CharacterCard {

    private Player player;

    public KnightCharacter() {
        super();
        this.player = null;
    }

    @Override
    public void applyEffect(@NotNull Game game, Object parameter) {
        this.player = game.getCurrentTurn();
    }

    @Override
    public int getBaseCost() {
        return 2;
    }

    @Override
    public void initializeGame(Board board) {

    }

    /**
     * Get current owner of this card
     *
     * @return the current owner
     */
    public Player getPlayer() {
        return player;
    }
}
