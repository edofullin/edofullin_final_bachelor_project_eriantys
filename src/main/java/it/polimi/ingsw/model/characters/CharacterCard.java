package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Game;

public abstract class CharacterCard {

    protected boolean isUsed;
    protected boolean isActive;

    public CharacterCard() {
        this.isUsed = false;
        this.isActive = false;
    }

    /**
     * Initialize the card at the start of the game
     *
     * @param board the game's board
     */
    public abstract void initializeGame(Board board);

    /**
     * Applies the card's effect
     *
     * @param game      the game
     * @param parameter different parameters
     */
    public abstract void applyEffect(Game game, Object parameter);

    /**
     * Get the card's base cost
     *
     * @return the card's base cost
     */
    public abstract int getBaseCost();

    /**
     * Return if the card has been used
     *
     * @return if the card is used
     */
    public boolean isUsed() {
        return isUsed;
    }

    /**
     * Set the card as used or not
     *
     * @param used boolean
     */
    public void setUsed(boolean used) {
        this.isUsed = used;
    }

    /**
     * Return if the card is active
     *
     * @return if the card is active
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Set the card as active or not
     *
     * @param active boolean
     */
    public void setActive(boolean active) {
        this.isActive = active;
    }

    /**
     * Return current card's cost
     *
     * @return the card's cost
     */
    public int getCost() {
        return this.getBaseCost() + (isUsed ? 1 : 0);
    }

    /**
     * Get the enum of the character card
     *
     * @return the enum
     */
    public CharacterEnum getEnumType() {
        String name = this.getClass().getSimpleName();
        name = name.replace("Character", "").toUpperCase();

        return CharacterEnum.valueOf(name);
    }
}
