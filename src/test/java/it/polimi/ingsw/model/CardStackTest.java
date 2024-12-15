package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardStackTest {
    private CardStack TCS;
    final private Player p = new Player("Jack", 0, TowerColor.BLACK, Player.Magician.MARIN);

    public CardStack buildTestCardStack (){
        return new CardStack(p);
    }

    @BeforeEach
    void setUp(){
       TCS =  buildTestCardStack();
    }

    /** Testing extracting the top card
     *
     */
    @Test
    void getTopCard() {
        assertNull(TCS.getTopCard());
        TCS.addCard(AssistCard.ASSISTANT_0);
        assertEquals(TCS.getTopCard(), AssistCard.ASSISTANT_0);
    }

    /** Testing adding a card
     *
     */
    @Test
    void addCard() {
        TCS.addCard(AssistCard.ASSISTANT_0);
        assertEquals(TCS.getTopCard(), AssistCard.ASSISTANT_0);
        assertEquals(1, TCS.getCards().size());
    }

    /** Testing if the cards can be seen by outside
     *
     */
    @Test
    void isPresent() {
        TCS.addCard(AssistCard.ASSISTANT_0);
        assertTrue(TCS.isPresent(AssistCard.ASSISTANT_0));
        assertFalse(TCS.isPresent(AssistCard.ASSISTANT_1));
    }

    /** Testing getting owner
     *
     */
    @Test
    void getOwner() {
        assertEquals(p, TCS.getOwner());
    }
}