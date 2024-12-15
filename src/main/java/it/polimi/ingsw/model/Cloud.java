package it.polimi.ingsw.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Cloud implements IStudentContainer {

    final private ArrayList<SPColor> students;

    public Cloud(@NotNull Cloud cloud) {
        this.students = new ArrayList<>(cloud.students);
    }

    public Cloud() {
        students = new ArrayList<>();
    }

    /**
     * Get the students
     *
     * @return students on the cloud
     */
    public List<SPColor> getStudents() {
        return students;
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
}
