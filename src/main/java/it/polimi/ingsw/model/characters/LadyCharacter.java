package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.IStudentContainer;
import it.polimi.ingsw.model.SPColor;

import java.util.ArrayList;
import java.util.List;

// PENULTIMO
public class LadyCharacter extends CharacterCard implements IStudentContainer {

    private final ArrayList<SPColor> students;

    public LadyCharacter() {
        super();
        this.students = new ArrayList<>();
    }

    @Override
    public void applyEffect(Game game, Object parameter) throws InvalidCharacterArgumentException {

        StudentsCharacterParameters params = (StudentsCharacterParameters) parameter;

        if (!this.students.contains(params.students.get(0)))
            throw new InvalidCharacterArgumentException("invalid student", this);

        this.removeStudent(params.students.get(0));
        game.getCurrentPlayerBoard().getCanteen().addStudent(params.students.get(0));
        SPColor student = game.getGameBoard().extractStudentFromPouch();
        if (student != null) this.addStudent(student);
    }

    @Override
    public int getBaseCost() {
        return 2;
    }

    @Override
    public void initializeGame(Board board) {
        for (int i = 0; i < 4; i++) {
            this.students.add(board.extractStudentFromPouch());
        }
    }

    /**
     * Add a student
     *
     * @param student the student
     */
    @Override
    public void addStudent(SPColor student) {
        this.students.add(student);
    }

    /**
     * Get the students
     *
     * @return the students
     */
    @Override
    public List<SPColor> getStudents() {
        return this.students;
    }

    /**
     * Remove a student
     *
     * @param student the student
     */
    @Override
    public void removeStudent(SPColor student) {
        this.students.remove(student);
    }
}
