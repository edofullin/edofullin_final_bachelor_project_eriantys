package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private Player playerTest;

    public Player buildPlayerTest(){
        return new Player("P1", 1, TowerColor.BLACK, Player.Magician.MARIN);
    }

    @BeforeEach
    void setUp() {
        playerTest = buildPlayerTest();
    }

    /** Testing if graveyard is correctly assigned
     *
     */
    @Test
    void setGraveyard() {
        CardStack csTest = new CardStack(playerTest);
        assertNotNull(playerTest.getGraveyard());
        assertEquals(playerTest.getGraveyard(), csTest);
    }

    /** Testing if board is correctly assigned
     *
     */
    @Test
    void setBoard() {
        PlayerBoard pbTest = new PlayerBoard(playerTest, 2);
        assertNotNull(playerTest.getBoard());
        assertEquals(playerTest.getBoard(), pbTest);
    }

    /** Testing getting name
     *
     */
    @Test
    void getName() {
        assertEquals("P1", playerTest.getName());
    }

    /** Testing getting mage
     *
     */
    @Test
    void getMagician() {
        assertEquals(Player.Magician.MARIN, playerTest.getMagician());
    }

    /** Testing if the removed card is no more present
     *
     */
    @Test
    void removeCard() {
        playerTest.removeCard(AssistCard.ASSISTANT_0);
        assertFalse(playerTest.getHand().contains(AssistCard.ASSISTANT_0));
    }

    /** Testing if the coins are correctly modified
     *
     */
    @Test
    void modifyCoins() {
        playerTest.modifyCoins(5);
        assertEquals(5, playerTest.getCoins());
        playerTest.modifyCoins(-7);
        assertEquals(-2, playerTest.getCoins());
    }
}