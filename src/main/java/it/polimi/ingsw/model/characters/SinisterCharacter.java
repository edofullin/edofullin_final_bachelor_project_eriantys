package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.PlayerBoard;

// ULTIMO
public class SinisterCharacter extends CharacterCard {

    public SinisterCharacter() {
        super();
    }

    @Override
    public void applyEffect(Game game, Object parameter) throws InvalidCharacterArgumentException {

        StudentsCharacterParameters params = (StudentsCharacterParameters) parameter;

        if (params.students == null) throw new InvalidCharacterArgumentException("null student", this);

        for (int i = 0; i < game.getPlayers().size(); i++) {
            PlayerBoard board = game.getGameBoard().getPlayerBoard(i);
            for (int j = 0; j < 3; j++) {
                if (board.getCanteen().getStudents().stream().anyMatch(s -> s == params.students.get(0)))
                    board.getCanteen().removeStudent(params.students.get(0));
            }
        }
    }

    @Override
    public int getBaseCost() {
        return 3;
    }

    @Override
    public void initializeGame(Board board) {

    }
}
