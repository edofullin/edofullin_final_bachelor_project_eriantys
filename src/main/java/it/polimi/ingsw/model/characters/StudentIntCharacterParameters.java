package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.SPColor;

public class StudentIntCharacterParameters extends CharacterParametersBase {
    public SPColor chosenStudent;
    public int islandNumber;

    public StudentIntCharacterParameters(SPColor chosenStudent, int islandNumber) {
        this.chosenStudent = chosenStudent;
        this.islandNumber = islandNumber;
    }

    public StudentIntCharacterParameters() {

    }

    public void setChosenStudent(SPColor chosenStudent) {
        this.chosenStudent = chosenStudent;
    }

    public void setIslandNumber(int islandNumber) {
        this.islandNumber = islandNumber;
    }
}
