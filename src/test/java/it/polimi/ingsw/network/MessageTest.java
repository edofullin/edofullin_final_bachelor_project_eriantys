package it.polimi.ingsw.network;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.JoinRequestMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    @Test
    void toJson() {
        JoinRequestMessage reqJoin = new JoinRequestMessage(1, "player1", Player.Magician.MARIN);

        String json = reqJoin.toJson();

        Message mess = Message.fromJson(json);
        assertTrue(mess instanceof JoinRequestMessage);

        JoinRequestMessage rmess = (JoinRequestMessage)mess;
        assertEquals(rmess.getMessageId(), 1);
        assertEquals(rmess.getPlayerName(), "player1");
    }

}
