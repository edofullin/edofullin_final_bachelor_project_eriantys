package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArchipelagoTest {

    private Archipelago testArchipelago;

    public Archipelago buildArchipelagoTest() {
        Archipelago ar = new Archipelago();
        ar.addStudent(SPColor.PINK);
        ar.addStudent(SPColor.GREEN);
        ar.addStudent(SPColor.YELLOW);
        return ar;
    }

    @BeforeEach
    void setUp() {
        testArchipelago = buildArchipelagoTest();
    }

    /** Testing setting owner
     *
     */
    @Test
    void setOwner() {
        testArchipelago.setOwner(TowerColor.BLACK);
        assertEquals(TowerColor.BLACK, testArchipelago.getOwner());
        assertEquals(1, testArchipelago.getNTowers());
    }

    /** Testing modifying towers
     *
     */
    @Test
    void modifyNTowers() {
        testArchipelago.setOwner(TowerColor.BLACK);
        testArchipelago.modifyNTowers(3);
        assertEquals(4, testArchipelago.getNTowers());
    }

    /** Testing removing a ban
     *
     */
    @Test
    void removeBan() {
        testArchipelago.addBans(2);
        testArchipelago.removeBan();
        assertEquals(1, testArchipelago.getBans());
    }

    /** Testing adding a student
     *
     */
    @Test
    void addStudent() {
        testArchipelago.addStudent(SPColor.PINK);
        assertEquals(2, testArchipelago.getStudents().stream().filter(s -> s == SPColor.PINK).count());
        assertEquals(4, testArchipelago.getStudents().size());
    }

    /** Testing removing a student
     *
     */
    @Test
    void removeStudent() {
        testArchipelago.removeStudent(SPColor.PINK);
        assertEquals(0, testArchipelago.getStudents().stream().filter(s -> s == SPColor.PINK).count());
        assertEquals(2, testArchipelago.getStudents().size());
    }
}