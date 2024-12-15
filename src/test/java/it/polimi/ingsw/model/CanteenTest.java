package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CanteenTest {

    private Canteen canteen;

    public Canteen buildTestCanteen() {
        Canteen canteen = new Canteen();
        SPColor prof1 = SPColor.GREEN;
        SPColor prof2 = SPColor.RED;
        for (int i = 0; i < 20; i++) {
            SPColor s = SPColor.GREEN;
            canteen.addStudent(s);
        }
        canteen.addProfessor(prof1);
        canteen.addProfessor(prof2);
        return canteen;
    }

    @BeforeEach
    void setUp() {
        this.canteen = buildTestCanteen();
    }

    /** Testing if the professor who was added is present
     *
     */
    @Test
    void addProfessor() {
        SPColor prof = SPColor.PINK;
        canteen.addProfessor(prof);
        assertTrue(canteen.getProfessors().contains(prof));
    }

    /** Testing if the professor who was removed is absent
     *
     */
    @Test
    void removeProfessor() {
        SPColor first = canteen.getProfessors().get(0);
        canteen.removeProfessor(first);
        assertFalse(canteen.getProfessors().contains(first));
    }

    /** Testing if the students of one color have risen
     * Testing if the students have risen
     */
    @Test
    void addStudent() {
        canteen.addStudent(SPColor.GREEN);
        long after_number_color = canteen.getStudents().stream().filter(s -> s == SPColor.GREEN).count();
        long after_number_tot = canteen.getStudents().size();
        assertEquals(after_number_color, 21);
        assertEquals(after_number_tot, 21);
    }

    /** Testing if the students of one color have decreased
     * Testing if the students have decreased
     */
    @Test
    void removeStudent() {
        SPColor first = canteen.getStudents().get(0);
        canteen.removeStudent(first);
        long after_number_color = canteen.getStudents().stream().filter(s -> s == first).count();
        long after_number_tot = canteen.getStudents().size();
        assertEquals(after_number_color, 19);
        assertEquals(after_number_tot, 19);
    }
}