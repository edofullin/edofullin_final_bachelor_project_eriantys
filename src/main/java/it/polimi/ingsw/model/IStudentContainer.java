package it.polimi.ingsw.model;

import java.util.List;

/**
 * Implemented by any class containing Students.
 * Enables movement of the students.
 */
public interface IStudentContainer {

    void addStudent(SPColor student);

    List<SPColor> getStudents();

    void removeStudent(SPColor student);

}
