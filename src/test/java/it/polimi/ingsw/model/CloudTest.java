package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CloudTest {

    private Cloud myCloud;

    public Cloud buildTestCloud(){
        Cloud cloud = new Cloud();
        cloud.addStudent(SPColor.GREEN);
        cloud.addStudent(SPColor.RED);
        cloud.addStudent(SPColor.GREEN);
        cloud.addStudent(SPColor.RED);
        cloud.addStudent(SPColor.BLUE);
        return cloud;
    }

    @BeforeEach
    void setUp(){
        this.myCloud = buildTestCloud();
    }

    /** Testing if the students of one color have risen
     * Testing if the students have risen
     */
    @Test
    void addStudent() {
        myCloud.addStudent(SPColor.YELLOW);
        long after_number_color = myCloud.getStudents().stream().filter(s -> s == SPColor.YELLOW).count();
        long after_number_tot = myCloud.getStudents().size();
        assertEquals(after_number_color, 1);
        assertEquals(after_number_tot, 6);
    }

    /** Testing if the students of one color have decreased
     * Testing if the students have decreased
     */
    @Test
    void removeStudent() {
        myCloud.removeStudent(SPColor.GREEN);
        long after_number_color = myCloud.getStudents().stream().filter(s -> s == SPColor.GREEN).count();
        long after_number_tot = myCloud.getStudents().size();
        assertEquals(after_number_color, 1);
        assertEquals(after_number_tot, 4);
    }
}