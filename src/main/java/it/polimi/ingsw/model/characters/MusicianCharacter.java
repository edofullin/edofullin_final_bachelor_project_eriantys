package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Game;

// TERZULTIMO
public class MusicianCharacter extends CharacterCard {


    public MusicianCharacter() {
        super();
    }

    @Override
    public void applyEffect(Game game, Object parameter) throws InvalidCharacterArgumentException {

        StudentsCharacterParameters params = (StudentsCharacterParameters) parameter;
        int size = params.students.size() / 2;

        if (size > 2) throw new InvalidCharacterArgumentException("too many students", this);

        for (int i = 0; i < size; i++) {
            if (!game.getCurrentPlayerBoard().getCanteen().getStudents().contains(params.students.get(i)))
                throw new InvalidCharacterArgumentException("invalid student to entrance", this);
            game.getCurrentPlayerBoard().getCanteen().removeStudent(params.students.get(i));
            game.getCurrentPlayerBoard().addStudent(params.students.get(i));

            if (!game.getCurrentPlayerBoard().getStudents().contains(params.students.get(i + size)))
                throw new InvalidCharacterArgumentException("invalid student to canteen", this);
            game.getCurrentPlayerBoard().removeStudent(params.students.get(i + size));
            game.getCurrentPlayerBoard().getCanteen().addStudent(params.students.get(i + size));
        }

    }

    @Override
    public int getBaseCost() {
        return 1;
    }

    @Override
    public void initializeGame(Board board) {

    }
}
