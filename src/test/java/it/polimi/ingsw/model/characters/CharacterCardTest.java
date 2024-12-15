package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class CharacterCardTest {

    private ArrayList<CharacterCard> characterCardsTest;

    final private ArrayList<Player> players = new ArrayList<>();

    final private Player p1 = new Player("P1", 0, TowerColor.BLACK, Player.Magician.MARIN);

    final private Player p2 = new Player("P2", 1, TowerColor.WHITE, Player.Magician.XERXES);

    public ArrayList<CharacterCard> buildCharacterCardTest() {
        ArrayList<CharacterCard> characterCards = new ArrayList<>();
        FactoryCharacterCard factoryCharacterCard = new FactoryCharacterCard();

        for (CharacterEnum charEnum: CharacterEnum.values()) {
            characterCards.add(factoryCharacterCard.createCharacter(charEnum));
        }
        return characterCards;
    }

    @BeforeEach
    void setUp() {
        characterCardsTest = buildCharacterCardTest();
    }

    /** Testing override method
     *
     */
    @Test
    void initializeGame() {
        players.add(p1);
        players.add(p2);
        for (CharacterCard charCard: characterCardsTest) {
            charCard.initializeGame(new Board(players));
            switch (charCard.getEnumType()) {
                case HERBALIST -> assertEquals(4, ((HerbalistCharacter) charCard).getBans());
                case JOKER -> assertEquals(6, ((JokerCharacter) charCard).getStudents().size());
                case LADY -> assertEquals(4, ((LadyCharacter) charCard).getStudents().size());
                case SOMMELIER -> assertEquals(4, ((SommelierCharacter) charCard).getStudents().size());
            }
        }
    }

    /** Testing override method
     *
     */
    @Test
    void applyEffect() {
        players.add(p1);
        players.add(p2);
        for (CharacterCard charCard: characterCardsTest) {
            Game game = new Game(false, players);
            charCard.initializeGame(game.getGameBoard());
            switch (charCard.getEnumType()) {
                case SOMMELIER -> {
                    charCard.applyEffect(game, new StudentIntCharacterParameters(((SommelierCharacter) charCard).getStudents().get(0), 1));
                    assertEquals(4, ((SommelierCharacter) charCard).getStudents().size());
                    assertEquals(2, game.getGameBoard().getIsland(1).getStudents().size());
                }
                case SINISTER -> {
                    game.getGameBoard().getPlayerBoard(0).getCanteen().addStudent(SPColor.GREEN);
                    game.getGameBoard().getPlayerBoard(0).getCanteen().addStudent(SPColor.GREEN);
                    game.getGameBoard().getPlayerBoard(0).getCanteen().addStudent(SPColor.GREEN);
                    game.getGameBoard().getPlayerBoard(0).getCanteen().addStudent(SPColor.GREEN);
                    game.getGameBoard().getPlayerBoard(1).getCanteen().addStudent(SPColor.GREEN);
                    game.getGameBoard().getPlayerBoard(1).getCanteen().addStudent(SPColor.GREEN);
                    charCard.applyEffect(game, new StudentsCharacterParameters(Collections.singletonList(SPColor.GREEN)));
                    assertEquals(1, game.getGameBoard().getPlayerBoard(0).getCanteen().getStudents().size());
                    assertEquals(0, game.getGameBoard().getPlayerBoard(1).getCanteen().getStudents().size());
                }
                case POSTMAN -> {
                    game.getPlayers().get(0).getGraveyard().addCard(AssistCard.ASSISTANT_0);
                    charCard.applyEffect(game, null);
                    charCard.setUsed(true);
                    charCard.setActive(true);
                    assertEquals(3, game.getPlayers().get(0).getGraveyard().getTopCard().getMoves() + 2);
                }
                case MUSICIAN -> {
                    game.setCurrentTurn(p1);
                    game.getGameBoard().getPlayerBoard(0).addStudent(SPColor.RED);
                    game.getGameBoard().getPlayerBoard(0).addStudent(SPColor.GREEN);
                    game.getGameBoard().getPlayerBoard(0).getCanteen().addStudent(SPColor.GREEN);
                    game.getGameBoard().getPlayerBoard(0).getCanteen().addStudent(SPColor.YELLOW);
                    ArrayList<SPColor> students = new ArrayList<>();
                    students.add(SPColor.GREEN);
                    students.add(SPColor.YELLOW);
                    students.add(SPColor.GREEN);
                    students.add(SPColor.RED);
                    charCard.applyEffect(game, new StudentsCharacterParameters(students));
                    assertEquals(SPColor.GREEN, game.getGameBoard().getPlayerBoard(0).getStudents().get(7));
                    assertEquals(SPColor.YELLOW, game.getGameBoard().getPlayerBoard(0).getStudents().get(8));
                    assertEquals(SPColor.GREEN, game.getGameBoard().getPlayerBoard(0).getCanteen().getStudents().get(0));
                    assertEquals(SPColor.RED, game.getGameBoard().getPlayerBoard(0).getCanteen().getStudents().get(1));
                }
                case MESSENGER -> {
                    game.getGameBoard().getIsland(6).addStudent(SPColor.GREEN);
                    game.getGameBoard().getPlayerBoard(0).getCanteen().addStudent(SPColor.GREEN);
                    game.getGameBoard().updateProfessors();
                    charCard.applyEffect(game, new IntCharacterParameters(6));
                    assertEquals(p1.getPlayerTowersColor(), game.getGameBoard().getIsland(6).getOwner());
                }
                case LADY -> {
                    game.setCurrentTurn(p1);
                    SPColor student = ((LadyCharacter) charCard).getStudents().get(0);
                    charCard.applyEffect(game, new StudentsCharacterParameters(Collections.singletonList(student)));
                    assertEquals(4, ((LadyCharacter) charCard).getStudents().size());
                    assertEquals(student, game.getGameBoard().getPlayerBoard(0).getCanteen().getStudents().get(0));
                }
                case JOKER -> {
                    game.setCurrentTurn(p1);
                    ArrayList<SPColor> students2 = new ArrayList<>();
                    students2.add(((JokerCharacter) charCard).getStudents().get(0));
                    students2.add(((JokerCharacter) charCard).getStudents().get(1));
                    students2.add(((JokerCharacter) charCard).getStudents().get(2));
                    students2.add(SPColor.BLUE);
                    students2.add(SPColor.PINK);
                    students2.add(SPColor.YELLOW);
                    game.getCurrentPlayerBoard().addStudent(SPColor.BLUE);
                    game.getCurrentPlayerBoard().addStudent(SPColor.BLUE);
                    game.getCurrentPlayerBoard().addStudent(SPColor.PINK);
                    game.getCurrentPlayerBoard().addStudent(SPColor.YELLOW);
                    charCard.applyEffect(game, new StudentsCharacterParameters(students2));
                    assertEquals(SPColor.BLUE, ((JokerCharacter) charCard).getStudents().get(3));
                    assertEquals(SPColor.PINK, ((JokerCharacter) charCard).getStudents().get(4));
                    assertEquals(SPColor.YELLOW, ((JokerCharacter) charCard).getStudents().get(5));
                    assertEquals(11, game.getCurrentPlayerBoard().getStudents().size());
                }
            }
        }
    }

    /** Testing override method
     *
     */
    @Test
    void getBaseCost() {
        for (CharacterCard charCard: characterCardsTest) {
            assertEquals(charCard.getBaseCost(), charCard.getCost());
        }
    }

    /** Testing override method
     *
     */
    @Test
    void setActive() {
        for (CharacterCard charCard: characterCardsTest) {
            assertFalse(charCard.isActive);
            charCard.setActive(true);
            assertTrue(charCard.isActive);
        }
    }

    /** Testing override method
     *
     */
    @Test
    void setUsed() {
        for (CharacterCard charCard: characterCardsTest) {
            charCard.setUsed(true);
            assertTrue(charCard.isUsed);
        }
    }

    /** Testing override method
     *
     */
    @Test
    void isUsed() {
        for (CharacterCard charCard: characterCardsTest) {
            assertFalse(charCard.isUsed());
        }
    }

    /** Testing override method
     *
     */
    @Test
    void isActive() {
        for (CharacterCard charCard: characterCardsTest) {
            assertFalse(charCard.isActive);
        }
    }

    /** Testing override method
     *
     */
    @Test
    void getCost() {
        for (CharacterCard charCard: characterCardsTest) {
            assertEquals(charCard.getBaseCost(), charCard.getCost());
            charCard.setUsed(true);
            assertEquals(charCard.getBaseCost() + 1, charCard.getCost());
        }
    }

    /** Testing override method
     *
     */
    @Test
    void getEnumType() {
        for (int i = 0; i < CharacterEnum.values().length; i++) {
            assertEquals(characterCardsTest.get(i).getEnumType(), CharacterEnum.values()[i]);
        }
    }

    /** Testing generating an exception
     *
     */
    @Test
    void exception() {
        players.add(p1);
        players.add(p2);
        try{
            characterCardsTest.get(10).applyEffect(new Game(true, players), new StudentsCharacterParameters(null));
        } catch (InvalidCharacterArgumentException ex) {
            assertTrue(true);
        }
    }
}