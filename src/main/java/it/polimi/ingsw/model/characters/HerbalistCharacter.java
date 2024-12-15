package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Game;

// QUARTO
public class HerbalistCharacter extends CharacterCard {

    private int bans;

    public HerbalistCharacter() {
        super();
    }

    @Override
    public void applyEffect(Game game, Object parameter) throws InvalidCharacterArgumentException {

        IntCharacterParameters params = (IntCharacterParameters) parameter;

        if (params.islandNumber < 0 || params.islandNumber >= game.getGameBoard().getIslands().size())
            throw new InvalidCharacterArgumentException("invalid island's number", this);
        if (this.getBans() == 0) throw new InvalidCharacterArgumentException("not enough bans", this);

        this.bans--;
        game.getGameBoard().getIsland(params.islandNumber).addBans(1);
    }

    @Override
    public int getBaseCost() {
        return 2;
    }

    @Override
    public void initializeGame(Board board) {
        this.bans = 4;
    }

    /**
     * Return a ban
     */
    public void returnBan() {
        this.bans++;
    }

    /**
     * Get the bans on this card
     *
     * @return the bans
     */
    public int getBans() {
        return this.bans;
    }
}