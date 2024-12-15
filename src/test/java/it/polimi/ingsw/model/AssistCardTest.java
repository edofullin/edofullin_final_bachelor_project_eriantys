package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AssistCardTest {

    private AssistCard card;

    @BeforeEach
    void setUp() {
        card = AssistCard.ASSISTANT_0;
    }

    /** Testing getting animal
     *
     */
    @Test
    void getAnimal() {
        assertEquals("Lion", card.getAnimal());
    }

    /** Testing getting power
     *
     */
    @Test
    void getPower() {
        assertEquals(1, card.getPower());
    }

    /** Testing getting moves
     *
     */
    @Test
    void getMoves() {
        assertEquals(1, card.getMoves());
    }
}