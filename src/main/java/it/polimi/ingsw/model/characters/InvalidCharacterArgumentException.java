package it.polimi.ingsw.model.characters;

public class InvalidCharacterArgumentException extends RuntimeException {

    CharacterCard card;

    public InvalidCharacterArgumentException(String message, CharacterCard card) {
        super(message);
        this.card = card;
    }
}
