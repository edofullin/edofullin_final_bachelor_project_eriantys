package it.polimi.ingsw.network;

import it.polimi.ingsw.model.SPColor;
import it.polimi.ingsw.model.characters.*;
import it.polimi.ingsw.network.messages.ChooseCharacterMessage;
import it.polimi.ingsw.network.messages.Message;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChooseCharacterMessageTest {

    @Test
    void chooseCharacterSerializeTest() {

        Message m = new ChooseCharacterMessage(
            CharacterEnum.MUSICIAN, new StudentsCharacterParameters((Arrays.asList(SPColor.GREEN, SPColor.RED, SPColor.RED, SPColor.RED)))
        );


        String serialize = m.toJson();

        Message deserial = Message.fromJson(serialize);
        ChooseCharacterMessage casted = (ChooseCharacterMessage) deserial;

        assertEquals(CharacterEnum.MUSICIAN, casted.getCharacter());
        assertEquals(4, ((StudentsCharacterParameters)casted.getObject()).students.size());


    }

}
