package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Game;

// #4
public class PostmanCharacter extends CharacterCard {

    public PostmanCharacter() {
        super();
    }

    @Override
    public void applyEffect(Game game, Object parameter) {

    }

    @Override
    public int getBaseCost() {
        return 1;
    }

    @Override
    public void initializeGame(Board board) {

    }
}
