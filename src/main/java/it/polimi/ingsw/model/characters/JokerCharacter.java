package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.IStudentContainer;
import it.polimi.ingsw.model.SPColor;

import java.util.ArrayList;
import java.util.List;

// TERZULTIMO
public class JokerCharacter extends CharacterCard implements IStudentContainer {

    private final ArrayList<SPColor> students;


    public JokerCharacter() {
        super();
        this.students = new ArrayList<>();
    }

    @Override
    public void applyEffect(Game game, Object parameter) throws InvalidCharacterArgumentException {
        StudentsCharacterParameters params = (StudentsCharacterParameters) parameter;
        int size = params.students.size() / 2;

        for (int i = 0; i < size; i++) {

            if (!this.students.contains(params.students.get(i)))
                throw new InvalidCharacterArgumentException("invalid card's student", this);
            this.removeStudent(params.students.get(i));
            game.getCurrentPlayerBoard().addStudent(params.students.get(i));

            if (!game.getCurrentPlayerBoard().getStudents().contains(params.students.get(i + size)))
                throw new InvalidCharacterArgumentException("invalid entrance's student", this);
            game.getCurrentPlayerBoard().removeStudent(params.students.get(i + size));
            this.addStudent(params.students.get(i + size));
        }
    }

    @Override
    public int getBaseCost() {
        return 1;
    }

    @Override
    public void initializeGame(Board board) {
        for (int i = 0; i < 6; i++) {
            SPColor student = board.extractStudentFromPouch();
            if (student != null) this.students.add(student);
        }
    }

    /**
     * Add a student
     *
     * @param student the student to be added
     */
    @Override
    public void addStudent(SPColor student) {
        this.students.add(student);
    }

    /**
     * Remove a student
     *
     * @param student the student to be removed
     */
    @Override
    public void removeStudent(SPColor student) {
        this.students.remove(student);
    }

    /**
     * Get the students
     *
     * @return the list of students
     */
    @Override
    public List<SPColor> getStudents() {
        return this.students;
    }
}
