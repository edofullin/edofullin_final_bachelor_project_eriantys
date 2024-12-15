package it.polimi.ingsw.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Canteen implements IStudentContainer {
    final private List<SPColor> professors;
    final private List<SPColor> students;

    public Canteen(@NotNull Canteen cc) {
        this.professors = new ArrayList<>(cc.professors);
        this.students = new ArrayList<>(cc.students);
    }

    public Canteen() {
        this.professors = new ArrayList<>();
        this.students = new ArrayList<>();
    }

    /**
     * Add a professor
     *
     * @param professor the professor to be added
     */
    public void addProfessor(SPColor professor) {
        this.professors.add(professor);
    }

    /**
     * Remove a professor
     *
     * @param professor the professor to be removed
     */
    public void removeProfessor(SPColor professor) {
        this.professors.remove(professor);
    }

    /**
     * Get the professors
     *
     * @return the professors
     */
    public List<SPColor> getProfessors() {
        return this.professors;
    }

    /**
     * Add a student
     *
     * @param student the student to be added
     */
    public void addStudent(SPColor student) {
        this.students.add(student);
    }

    /**
     * Remove a student
     *
     * @param student the student to be removed
     */
    public void removeStudent(SPColor student) {
        this.students.remove(student);
    }

    /**
     * Get the students
     *
     * @return the students
     */
    public List<SPColor> getStudents() {
        return this.students;
    }
}
