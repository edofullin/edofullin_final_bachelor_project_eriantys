package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Game;

// SESTO
public class CentaurCharacter extends CharacterCard {

    public CentaurCharacter() {
        super();
    }

    @Override
    public void applyEffect(Game game, Object parameter) {

    }

    @Override
    public void initializeGame(Board board) {

    }

    @Override
    public int getBaseCost() {
        return 3;
    }
}
