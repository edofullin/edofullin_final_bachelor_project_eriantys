package it.polimi.ingsw.model;

import it.polimi.ingsw.Support;
import it.polimi.ingsw.model.characters.IntCharacterParameters;
import it.polimi.ingsw.model.characters.StudentsCharacterParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Game gameTest;

    final private Player p1 = new Player("P1", 0, TowerColor.BLACK, Player.Magician.MARIN);
    final private Player p2 = new Player("P2", 1, TowerColor.WHITE, Player.Magician.XERXES);

    public Game buildTestGame() {
        List<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        return new Game(true, players);
    }

    public Board buildTestBoard2() {
        ArrayList<Player> players = new ArrayList<>();

        players.add(p1);
        players.add(p2);

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

    @BeforeEach
    void setUp() {
        gameTest = buildTestGame();
        gameTest.setCurrentTurn(p1);
    }

    /** Testing getting players
     *
     */
    @Test
    void getPlayers() {
        List<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        assertEquals(players, gameTest.getPlayers());
        assertEquals(7, gameTest.getGameBoard().getPlayerBoard(0).getStudents().size());
        assertEquals(7, gameTest.getGameBoard().getPlayerBoard(1).getStudents().size());
    }

    /** Testing getting mode
     *
     */
    @Test
    void getMode() {
        assertTrue(gameTest.getMode());
    }

    /** Testing coping a game
     *
     */
    @Test
    void copyTest() {
        List<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        Game gm = new Game(true, players);

        Game gm2 = new Game(gm);
        gm2.fixReferences();
    }

    /**
     * Testing Queue
     */
    @Test
    void queueTest() {
        List<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        Game gm = new Game(true, players);

        gm.calculateQueue(List.of(AssistCard.ASSISTANT_1, AssistCard.ASSISTANT_4));

        assertSame(p1, gm.getQueue().get(0));
        assertSame(p2, gm.getQueue().get(1));

        gm.calculateQueue(List.of(AssistCard.ASSISTANT_7, AssistCard.ASSISTANT_4));

        assertSame(p1, gm.getQueue().get(1));
        assertSame(p2, gm.getQueue().get(0));
    }

    /** Testing getting current player's board
     *
     */
    @Test
    void getCurrentPlayerBoard() {
        assertNotEquals(null, gameTest.getCurrentPlayerBoard());

        assertEquals(7, gameTest.getCurrentPlayerBoard().getStudents().size());
    }

    /** Testing getting current player
     *
     */
    @Test
    void getCurrentTurn() {
        assertSame(p1, gameTest.getCurrentTurn());
    }

    /** Testing setting current turn
     *
     */
    @Test
    void setCurrentTurn() {
        gameTest.setCurrentTurn(p2);
        assertSame(p2, gameTest.getCurrentTurn());
    }

    /** Testing getting current island
     *
     */
    @Test
    void getCurrentIsland() {
        assertSame(gameTest.getGameBoard().getIsland(0), gameTest.getCurrentIsland());
    }

    /** Testing getting a player by his ID
     *
     */
    @Test
    void getPlayerById() {
        assertSame(p1, gameTest.getPlayerById(0));
    }

    /** Testing player getting an island
     *
     */
    @Test
    void giveCloudToPlayer() {
        ArrayList<SPColor> oldValue = new ArrayList<>(gameTest.getGameBoard().getCloud(0).getStudents());

        gameTest.giveCloudToPlayer(0, p1);

        assertEquals(0, gameTest.getGameBoard().getCloud(0).getStudents().size());

        assertTrue(p1.getBoard().getStudents().containsAll(oldValue));

    }

    /** Testing getting coins
     *
     */
    @Test
    void getCoins() {
        assertEquals(18, gameTest.getGameBoard().getCoins());
    }

    /** Testing end of the game
     *
     */
    @Test
    void checkGameOver() {
        Game.WinNotification reason = gameTest.checkGameOver();
        assertNull(reason);

        gameTest.getCurrentPlayerBoard().getTowers().clear();
        reason = gameTest.checkGameOver();
        assertEquals(reason.condition(), Game.WinningConditions.NO_TOWERS);
        assertSame(reason.winner(), gameTest.getCurrentPlayerBoard().getOwner());

        gameTest.getCurrentPlayerBoard().getTowers().add(TowerColor.BLACK);
        gameTest.getPlayers().get(0).getHand().clear();
        reason = gameTest.checkGameOver();
        assertEquals(reason.condition(), Game.WinningConditions.EMPTY_HAND);
        assertSame(reason.winner(), gameTest.getCurrentPlayerBoard().getOwner());

        gameTest.getGameBoard().getPouch().clear();
        reason = gameTest.checkGameOver();
        assertEquals(reason.condition(), Game.WinningConditions.EMPTY_POUCH);

        gameTest.getGameBoard().getIslands().clear();
        gameTest.getGameBoard().getIslands().add(new Archipelago());
        reason = gameTest.checkGameOver();
        assertEquals(reason.condition(), Game.WinningConditions.TOO_FEW_ISLANDS);
    }

    /** Testing herbalist character
     *
     */
    @Test
    void testHerbalist() {
        Board board = buildTestBoard2();
        gameTest.setGameBoard(board);
        gameTest.getGameBoard().setCharacterCards();
        gameTest.getGameBoard().getIsland(1).removeStudent(gameTest.getGameBoard().getIsland(1).getStudents().get(0));
        gameTest.getGameBoard().getCharacterCards().get(0).applyEffect(gameTest, new IntCharacterParameters(1));
        gameTest.getGameBoard().getCharacterCards().get(0).setActive(true);
        gameTest.getGameBoard().getCharacterCards().get(0).setUsed(true);
        gameTest.getGameBoard().updateProfessors();
        gameTest.getGameBoard().updateInfluence(0);
        gameTest.getGameBoard().updateInfluence(1);
        assertEquals(p1.getPlayerTowersColor(), gameTest.getGameBoard().getIsland(0).getOwner());
        assertNull(gameTest.getGameBoard().getIsland(1).getOwner());
    }

    /** Testing centaur character
     *
     */
    @Test
    void testCentaur() {
        Board board = buildTestBoard2();
        gameTest.setGameBoard(board);
        gameTest.getGameBoard().setCharacterCards();
        gameTest.getGameBoard().getIsland(1).removeStudent(gameTest.getGameBoard().getIsland(1).getStudents().get(0));
        gameTest.getGameBoard().updateProfessors();
        gameTest.getGameBoard().updateInfluence(0);
        gameTest.getGameBoard().updateInfluence(1);
        assertEquals(p2.getPlayerTowersColor(), gameTest.getGameBoard().getIsland(1).getOwner());
        gameTest.getGameBoard().getIsland(1).addStudent(SPColor.GREEN);
        gameTest.getGameBoard().getIsland(1).addStudent(SPColor.GREEN);
        assertNull(gameTest.getGameBoard().updateInfluence(1));
        gameTest.getGameBoard().getCharacterCards().get(1).applyEffect(gameTest, new IntCharacterParameters(1));
        gameTest.getGameBoard().getCharacterCards().get(1).setActive(true);
        gameTest.getGameBoard().getCharacterCards().get(1).setUsed(true);
        gameTest.getGameBoard().updateInfluence(1);
        assertEquals(p1.getPlayerTowersColor(), gameTest.getGameBoard().getIsland(0).getOwner());
    }

    /** Testing merchant character
     *
     */
    @Test
    void testMerchant() {
        Board board = buildTestBoard2();
        gameTest.setGameBoard(board);
        gameTest.getGameBoard().setCharacterCards();
        gameTest.getGameBoard().getIsland(1).removeStudent(gameTest.getGameBoard().getIsland(1).getStudents().get(0));
        gameTest.getGameBoard().getCharacterCards().get(2).applyEffect(gameTest, new StudentsCharacterParameters(Collections.singletonList(SPColor.RED)));
        gameTest.getGameBoard().getCharacterCards().get(2).setActive(true);
        gameTest.getGameBoard().getCharacterCards().get(2).setUsed(true);
        gameTest.getGameBoard().updateProfessors();
        gameTest.getGameBoard().updateInfluence(0);
        gameTest.getGameBoard().updateInfluence(1);
        assertEquals(p1.getPlayerTowersColor(), gameTest.getGameBoard().getIsland(0).getOwner());
        assertEquals(11, gameTest.getGameBoard().getIslands().size());
    }

    /** Testing knight character
     *
     */
    @Test
    void testKnight() {
        Board board = buildTestBoard2();
        gameTest.setGameBoard(board);
        gameTest.getGameBoard().setCharacterCards();
        gameTest.getGameBoard().getIsland(1).removeStudent(gameTest.getGameBoard().getIsland(1).getStudents().get(0));
        gameTest.getGameBoard().getCharacterCards().get(3).applyEffect(gameTest, null);
        gameTest.getGameBoard().getCharacterCards().get(3).setActive(true);
        gameTest.getGameBoard().getCharacterCards().get(3).setUsed(true);
        gameTest.getGameBoard().updateProfessors();
        gameTest.getGameBoard().updateInfluence(1);
        gameTest.getGameBoard().updateInfluence(0);
        assertEquals(p1.getPlayerTowersColor(), gameTest.getGameBoard().getIsland(0).getOwner());
        assertEquals(11, gameTest.getGameBoard().getIslands().size());
    }

    /** Testing chef character
     *
     */
    @Test
    void testChef() {
        Board board = buildTestBoard2();
        gameTest.setGameBoard(board);
        gameTest.getGameBoard().setCharacterCards();
        gameTest.getGameBoard().getIsland(1).removeStudent(gameTest.getGameBoard().getIsland(1).getStudents().get(0));
        gameTest.getGameBoard().getCharacterCards().get(4).applyEffect(gameTest, null);
        gameTest.getGameBoard().getCharacterCards().get(4).setActive(true);
        gameTest.getGameBoard().getCharacterCards().get(4).setUsed(true);
        Support.moveStudent(SPColor.RED, null, gameTest.getGameBoard().getPlayerBoard(gameTest.getPlayers().get(0)).getCanteen());
        Support.moveStudent(SPColor.RED, null, gameTest.getGameBoard().getPlayerBoard(gameTest.getPlayers().get(0)).getCanteen());
        gameTest.getGameBoard().updateProfessors();
        assertTrue(gameTest.getGameBoard().getPlayerBoards().get(0).getCanteen().getProfessors().contains(SPColor.GREEN));
        assertTrue(gameTest.getGameBoard().getPlayerBoards().get(0).getCanteen().getProfessors().contains(SPColor.RED));
        gameTest.setCurrentTurn(p2);
        gameTest.getGameBoard().getCharacterCards().get(4).applyEffect(gameTest, null);
        gameTest.getGameBoard().updateProfessors();
        assertTrue(gameTest.getGameBoard().getPlayerBoards().get(0).getCanteen().getProfessors().contains(SPColor.GREEN));
        assertFalse(gameTest.getGameBoard().getPlayerBoards().get(0).getCanteen().getProfessors().contains(SPColor.RED));
        assertTrue(gameTest.getGameBoard().getPlayerBoards().get(1).getCanteen().getProfessors().contains(SPColor.RED));

    }
}