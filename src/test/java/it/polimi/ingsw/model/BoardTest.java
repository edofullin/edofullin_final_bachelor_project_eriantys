package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.Support;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BoardTest {

    private Board testBoard;
    final private Player p = new Player("p1", 1, TowerColor.BLACK, Player.Magician.MARIN);

    public Board buildTestBoard2() {
        ArrayList<Player> players = new ArrayList<>();

        players.add(p);
        players.add(new Player("p2", 2, TowerColor.WHITE, Player.Magician.XERXES));

        Board b = new Board(players);

        b.getIslands().get(0).addStudent(SPColor.GREEN);
        b.getIslands().get(0).addStudent(SPColor.GREEN);
        b.getIslands().get(0).addStudent(SPColor.GREEN);
        b.getIslands().get(0).addStudent(SPColor.RED);
        b.getIslands().get(0).addStudent(SPColor.BLUE);

        b.getIslands().get(1).addStudent(SPColor.RED);
        b.getIslands().get(1).addStudent(SPColor.RED);
        b.getIslands().get(1).addStudent(SPColor.BLUE);
        b.getIslands().get(1).addStudent(SPColor.GREEN);

        Support.moveStudent(SPColor.GREEN, null, b.getPlayerBoard(players.get(0)).getCanteen());
        Support.moveStudent(SPColor.GREEN, null, b.getPlayerBoard(players.get(0)).getCanteen());
        Support.moveStudent(SPColor.GREEN, null, b.getPlayerBoard(players.get(0)).getCanteen());

        Support.moveStudent(SPColor.RED, null, b.getPlayerBoard(players.get(1)).getCanteen());
        Support.moveStudent(SPColor.RED, null, b.getPlayerBoard(players.get(1)).getCanteen());

        return b;
    }

    public Board buildTestBoard3() {
        ArrayList<Player> players = new ArrayList<>();

        players.add(p);
        players.add(new Player("p2", 2, TowerColor.WHITE, Player.Magician.XERXES));
        players.add(new Player("p3", 3, TowerColor.GRAY, Player.Magician.RIAS));

        Board b = new Board(players);

        b.getIslands().get(0).addStudent(SPColor.GREEN);
        b.getIslands().get(0).addStudent(SPColor.GREEN);
        b.getIslands().get(0).addStudent(SPColor.GREEN);
        b.getIslands().get(0).addStudent(SPColor.RED);
        b.getIslands().get(0).addStudent(SPColor.BLUE);

        b.getIslands().get(1).addStudent(SPColor.RED);
        b.getIslands().get(1).addStudent(SPColor.RED);
        b.getIslands().get(1).addStudent(SPColor.BLUE);
        b.getIslands().get(1).addStudent(SPColor.GREEN);

        b.getIslands().get(10).addStudent(SPColor.GREEN);

        b.getIslands().get(11).addStudent(SPColor.GREEN);

        Support.moveStudent(SPColor.GREEN, null, b.getPlayerBoard(players.get(0)).getCanteen());
        Support.moveStudent(SPColor.GREEN, null, b.getPlayerBoard(players.get(0)).getCanteen());
        Support.moveStudent(SPColor.GREEN, null, b.getPlayerBoard(players.get(0)).getCanteen());

        Support.moveStudent(SPColor.RED, null, b.getPlayerBoard(players.get(1)).getCanteen());
        Support.moveStudent(SPColor.RED, null, b.getPlayerBoard(players.get(1)).getCanteen());

        Support.moveStudent(SPColor.RED, null, b.getPlayerBoard(players.get(2)).getCanteen());
        Support.moveStudent(SPColor.RED, null, b.getPlayerBoard(players.get(2)).getCanteen());
        Support.moveStudent(SPColor.YELLOW, null, b.getPlayerBoard(players.get(2)).getCanteen());
        Support.moveStudent(SPColor.YELLOW, null, b.getPlayerBoard(players.get(2)).getCanteen());

        return b;
    }

    @BeforeEach
    void setUp() {
        testBoard = buildTestBoard2();
    }

    /** Testing getting graveyard
     *
     */
    @Test
    void getGraveyards() {
        assertEquals(testBoard.getGraveyard(0), testBoard.getGraveyards().get(0));
        assertEquals(testBoard.getGraveyard(p), testBoard.getGraveyard(0));
    }

    /** Testing if it gives the correct island
     *
     */
    @Test
    void getMotherNatureIsland() {
        assertEquals(testBoard.getMotherNatureIsland(), testBoard.getIsland(0));
    }

    /** Testing if the influence is calculated correctly
     *
     */
    @Test
    void updateInfluence() {
        testBoard.updateProfessors();
        testBoard.getIsland(1).removeStudent(testBoard.getIsland(1).getStudents().get(0));
        assertEquals(1, testBoard.updateInfluence(0).getId());
        assertEquals(2, testBoard.updateInfluence(1).getId());
        testBoard.getIslands().get(1).addStudent(SPColor.GREEN);
        testBoard.getIslands().get(1).addStudent(SPColor.GREEN);
        testBoard.getIslands().get(1).addStudent(SPColor.GREEN);
        assertEquals(1, testBoard.updateInfluence(testBoard.getIsland(1)).getId());
        testBoard.getIslands().get(1).addStudent(SPColor.RED);
        testBoard.getIslands().get(1).addStudent(SPColor.RED);
        testBoard.getIslands().get(1).addStudent(SPColor.RED);
        assertEquals(1, testBoard.updateInfluence(testBoard.getIslands().get(0)).getId());
        assertEquals(2, testBoard.updateInfluence(testBoard.getIslands().get(1)).getId());
    }

    /** Testing if only the correct island has MotherNature
     *
     */
    @Test
    void moveMotherNatureNoLoop() {
        this.testBoard.moveMotherNature(5);
        for (int i = 0; i < this.testBoard.getIslands().size(); i++) {
            if (i == 5)
                assertTrue(testBoard.getIslands().get(i).getMotherNature());
            else
                assertFalse(testBoard.getIslands().get(i).getMotherNature());
        }
    }

    /** Testing if only the correct island has MotherNature when
     * it has to return on the first island
     */
    @Test
    void moveMotherNatureLoop() {
        this.testBoard.moveMotherNature(14);
        for (int i = 0; i < this.testBoard.getIslands().size(); i++) {
            if (i == 2)
                assertTrue(testBoard.getIslands().get(i).getMotherNature());
            else
                assertFalse(testBoard.getIslands().get(i).getMotherNature());
        }
    }

    /** Testing joining islands
     *
     */
    @Test
    void joinIsland() {
        testBoard = buildTestBoard3();
        testBoard.updateProfessors();
        testBoard.getIsland(1).removeStudent(testBoard.getIsland(1).getStudents().get(0));
        testBoard.getIsland(10).removeStudent(testBoard.getIsland(10).getStudents().get(0));
        testBoard.getIsland(11).removeStudent(testBoard.getIsland(11).getStudents().get(0));
        testBoard.updateInfluence(testBoard.getIsland(0));
        testBoard.moveMotherNature(1);
        testBoard.updateInfluence(testBoard.getIsland(1));
        assertTrue(testBoard.getIsland(0).getMotherNature());
        assertEquals(2, testBoard.getIsland(0).getNTowers());
        assertEquals(9, testBoard.getIsland(0).getStudents().size());
        testBoard.updateInfluence(testBoard.getIsland(9));
        testBoard.moveMotherNature(10);
        testBoard.updateInfluence(testBoard.getIsland(10));
        assertTrue(testBoard.getIsland(8).getMotherNature());
        assertEquals(4, testBoard.getIsland(8).getNTowers());
        assertEquals(11, testBoard.getIsland(8).getStudents().size());
    }

    /** Testing filling of the clouds two-player mode
     *
     */
    @Test
    void addStudentCloud2() {
        SPColor student1 = testBoard.getPouch().get(0);
        SPColor student2 = testBoard.getPouch().get(1);
        SPColor student3 = testBoard.getPouch().get(2);
        SPColor student4 = testBoard.getPouch().get(3);
        SPColor student5 = testBoard.getPouch().get(4);
        SPColor student6 = testBoard.getPouch().get(5);
        testBoard.addStudentsClouds();
        assertEquals(student1, testBoard.getClouds().get(0).getStudents().get(0));
        assertEquals(student2, testBoard.getClouds().get(0).getStudents().get(1));
        assertEquals(student3, testBoard.getClouds().get(0).getStudents().get(2));
        assertEquals(student4, testBoard.getClouds().get(1).getStudents().get(0));
        assertEquals(student5, testBoard.getClouds().get(1).getStudents().get(1));
        assertEquals(student6, testBoard.getClouds().get(1).getStudents().get(2));
    }

    /** Testing filling of the clouds three-player mode
     *
     */
    @Test
    void addStudentCloud3() {
        testBoard = buildTestBoard3();
        SPColor student1 = testBoard.getPouch().get(0);
        SPColor student2 = testBoard.getPouch().get(1);
        SPColor student3 = testBoard.getPouch().get(2);
        SPColor student4 = testBoard.getPouch().get(3);
        SPColor student5 = testBoard.getPouch().get(4);
        SPColor student6 = testBoard.getPouch().get(5);
        SPColor student7 = testBoard.getPouch().get(6);
        SPColor student8 = testBoard.getPouch().get(7);
        SPColor student9 = testBoard.getPouch().get(8);
        SPColor student10 = testBoard.getPouch().get(9);
        SPColor student11 = testBoard.getPouch().get(10);
        SPColor student12 = testBoard.getPouch().get(11);
        testBoard.addStudentsClouds();
        assertEquals(student1, testBoard.getCloud(0).getStudents().get(0));
        assertEquals(student2, testBoard.getCloud(0).getStudents().get(1));
        assertEquals(student3, testBoard.getCloud(0).getStudents().get(2));
        assertEquals(student4, testBoard.getCloud(0).getStudents().get(3));
        assertEquals(student5, testBoard.getCloud(1).getStudents().get(0));
        assertEquals(student6, testBoard.getCloud(1).getStudents().get(1));
        assertEquals(student7, testBoard.getCloud(1).getStudents().get(2));
        assertEquals(student8, testBoard.getCloud(1).getStudents().get(3));
        assertEquals(student9, testBoard.getCloud(2).getStudents().get(0));
        assertEquals(student10, testBoard.getCloud(2).getStudents().get(1));
        assertEquals(student11, testBoard.getCloud(2).getStudents().get(2));
        assertEquals(student12, testBoard.getCloud(2).getStudents().get(3));
    }

    /** Testing emptying of a cloud
     *
     */
    @Test
    void chooseCloudToEmpty() {
        testBoard.addStudentsClouds();
        SPColor student1 = testBoard.getCloud(0).getStudents().get(0);
        SPColor student2 = testBoard.getCloud(0).getStudents().get(1);
        SPColor student3 = testBoard.getCloud(0).getStudents().get(2);
        testBoard.chooseCloudToEmpty(testBoard.getPlayers().get(0), 0);
        assertTrue(testBoard.getCloud(0).getStudents().isEmpty());
        assertEquals(student1, testBoard.getPlayerBoard(0).getStudents().get(0));
        assertEquals(student2, testBoard.getPlayerBoard(0).getStudents().get(1));
        assertEquals(student3, testBoard.getPlayerBoard(0).getStudents().get(2));
    }

    /** Testing if it gives back the correct player's board
     *
     */
    @Test
    void findPBByProfessor() {
        testBoard.updateProfessors();
        assertEquals(testBoard.findPBByProfessor(SPColor.GREEN), testBoard.getPlayerBoard(0));
    }

    /** Testing where the professors are after the update
     *
     */
    @Test
    void updateProfessors() {
        this.testBoard.updateProfessors();
        assertTrue(testBoard.getPlayerBoards().get(0).getCanteen().getProfessors().contains(SPColor.GREEN));
        assertTrue(testBoard.getPlayerBoards().get(1).getCanteen().getProfessors().contains(SPColor.RED));
        Support.moveStudent(SPColor.GREEN, testBoard.getPlayerBoard(0).getCanteen(), testBoard.getPlayerBoard(1).getCanteen());
        Support.moveStudent(SPColor.GREEN, testBoard.getPlayerBoard(0).getCanteen(), testBoard.getPlayerBoard(1).getCanteen());
        this.testBoard.updateProfessors();
        assertTrue(testBoard.getPlayerBoards().get(1).getCanteen().getProfessors().contains(SPColor.GREEN));
        assertTrue(testBoard.getPlayerBoards().get(1).getCanteen().getProfessors().contains(SPColor.RED));
    }

    /** Testing movement of a professor prof board to a player
     *
     */
    @Test
    void professorBoardToPlayer() {
        for (SPColor c: SPColor.values()) {
            this.testBoard.professorBoardToPlayer(testBoard.getPlayerBoard(0), c);
            assertFalse(this.testBoard.getProfessors().contains(c));
            assertTrue(this.testBoard.getPlayerBoard(0).getCanteen().getProfessors().contains(c));
        }
    }

    /** Testing movement of a professor from a player to another
     *
     */
    @Test
    void professorPlayerToPlayer() {
        for (SPColor c: SPColor.values()) {
            this.testBoard.professorBoardToPlayer(testBoard.getPlayerBoard(0), c);
            this.testBoard.professorPlayerToPlayer(testBoard.getPlayerBoard(0), testBoard.getPlayerBoard(1), c);
            assertFalse(this.testBoard.getPlayerBoard(0).getCanteen().getProfessors().contains(c));
            assertTrue(this.testBoard.getPlayerBoard(1).getCanteen().getProfessors().contains(c));
        }
    }
}