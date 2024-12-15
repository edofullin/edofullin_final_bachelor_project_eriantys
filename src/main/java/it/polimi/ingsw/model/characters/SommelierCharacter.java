package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.IStudentContainer;
import it.polimi.ingsw.model.SPColor;

import java.util.ArrayList;
import java.util.List;

// #1
public class SommelierCharacter extends CharacterCard implements IStudentContainer {

    final private ArrayList<SPColor> students;

    public SommelierCharacter() {
        super();
        this.students = new ArrayList<>();
    }

    @Override
    public void applyEffect(Game game, Object parameter) throws InvalidCharacterArgumentException {

        StudentIntCharacterParameters params = (StudentIntCharacterParameters) parameter;

        if (!this.students.contains(params.chosenStudent))
            throw new InvalidCharacterArgumentException("invalid student", this);
        if (params.islandNumber < 0 || params.islandNumber >= game.getGameBoard().getIslands().size())
            throw new InvalidCharacterArgumentException("invalid island's number", this);

        this.removeStudent(params.chosenStudent);
        game.getGameBoard().getIsland(params.islandNumber).addStudent(params.chosenStudent);
        SPColor student = game.getGameBoard().extractStudentFromPouch();
        if (student != null) this.addStudent(student);

    }

    @Override
    public int getBaseCost() {
        return 1;
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
