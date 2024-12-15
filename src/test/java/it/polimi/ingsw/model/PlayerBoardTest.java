package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerBoardTest {

    private PlayerBoard board;

    public PlayerBoard buildTestPlayerBoard2(){
        Player player = new Player("Dani", 0, TowerColor.BLACK, Player.Magician.MARIN);
        PlayerBoard pb = new PlayerBoard(player, 2);

        for(int i=0; i<20; i++) {
            SPColor student = SPColor.values()[i % 5];
            pb.addStudent(student);
            pb.getCanteen().addStudent(student);
        }

        pb.getCanteen().addProfessor(SPColor.GREEN);
        pb.getCanteen().addProfessor(SPColor.PINK);

        return pb;
    }

    public PlayerBoard buildTestPlayerBoard3(){
        Player player = new Player("Dani", 0, TowerColor.BLACK, Player.Magician.MARIN);
        PlayerBoard pb = new PlayerBoard(player, 3);

        for(int i=0; i<20; i++) {
            SPColor student = SPColor.values()[i % 5];
            pb.addStudent(student);
            pb.getCanteen().addStudent(student);
        }

        pb.getCanteen().addProfessor(SPColor.GREEN);
        pb.getCanteen().addProfessor(SPColor.PINK);

        return pb;
    }

    @BeforeEach
    void setUp() {
        board = buildTestPlayerBoard2();
    }

    /** Testing if the towers have risen
     *
     */
    @Test
    void addTower() {
        board.addTower(TowerColor.BLACK);
        assertEquals(9, board.getTowers().size());
    }

    /** Testing if the towers have decreased
     *
     */
    @Test
    void removeTower() {
        board.removeTower(TowerColor.BLACK);
        assertEquals(7, board.getTowers().size());
    }

    /** Testing if the students of one color have risen
     * Testing if the students have risen
     */
    @Test
    void addStudent() {
        board.addStudent(SPColor.GREEN);
        assertEquals(5, board.getStudents().stream().filter(s -> s == SPColor.GREEN).count());
        assertEquals(21, board.getStudents().size());
    }

    /** Testing if the students of one color have decreased
     * Testing if the students have decreased
     */
    @Test
    void removeStudent() {
        board.removeStudent(SPColor.PINK);
        assertEquals(3, board.getStudents().stream().filter(s -> s == SPColor.PINK).count());
        assertEquals(19, board.getStudents().size());
    }

    /** Testing a three-player game
     *
     */
    @Test
    void threePlayer() {
        board = buildTestPlayerBoard3();
        assertEquals(board.getTowers().size(), 6);
    }
}