package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Game;

// NUM 3
public class MessengerCharacter extends CharacterCard {

    public MessengerCharacter() {
        super();
    }

    @Override
    public void applyEffect(Game game, Object parameter) throws InvalidCharacterArgumentException {

        IntCharacterParameters params = (IntCharacterParameters) parameter;

        if (params.islandNumber < 0 || params.islandNumber >= game.getGameBoard().getIslands().size())
            throw new InvalidCharacterArgumentException("invalid island's number", this);

        game.getGameBoard().updateInfluence(params.islandNumber);
    }

    @Override
    public int getBaseCost() {
        return 3;
    }

    @Override
    public void initializeGame(Board board) {

    }
}
