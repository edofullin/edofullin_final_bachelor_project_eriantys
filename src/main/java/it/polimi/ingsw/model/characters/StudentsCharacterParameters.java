package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.SPColor;

import java.util.ArrayList;
import java.util.List;

public class StudentsCharacterParameters extends CharacterParametersBase {
    public List<SPColor> students;

    public StudentsCharacterParameters(List<SPColor> students) {
        this.students = students;
    }

    public StudentsCharacterParameters() {
        this.students = new ArrayList<>();
    }

    public List<SPColor> getStudents() {
        return this.students;
    }
}
