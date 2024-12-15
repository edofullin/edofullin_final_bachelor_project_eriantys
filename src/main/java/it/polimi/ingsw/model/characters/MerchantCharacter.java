package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.SPColor;

// QUARTULTIMO
public class MerchantCharacter extends CharacterCard {

    private SPColor student;

    @Override
    public void applyEffect(Game game, Object parameter) throws InvalidCharacterArgumentException {

        StudentsCharacterParameters params = (StudentsCharacterParameters) parameter;

        if (params.students.get(0) == null) throw new InvalidCharacterArgumentException("null color", this);
        this.student = params.students.get(0);
    }

    @Override
    public int getBaseCost() {
        return 3;
    }

    @Override
    public void initializeGame(Board board) {

    }

    /**
     * Get the current color
     *
     * @return the color
     */
    public SPColor getStudent() {
        return student;
    }
}
