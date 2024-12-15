package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.characters.CharacterEnum;
import it.polimi.ingsw.model.characters.CharacterParametersBase;

public class ChooseCharacterMessage extends Message {

    final private CharacterEnum character;

    final private CharacterParametersBase object;

    public ChooseCharacterMessage(CharacterEnum character, CharacterParametersBase param) {
        this.character = character;
        this.object = param;
    }

    public ChooseCharacterMessage(long messageId, CharacterEnum character, CharacterParametersBase param) {
        super(messageId);
        this.character = character;
        this.object = param;
    }

    public CharacterEnum getCharacter() {
        return character;
    }

    public CharacterParametersBase getObject() {
        return object;
    }
}
