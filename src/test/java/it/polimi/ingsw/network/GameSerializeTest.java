package it.polimi.ingsw.network;

import com.google.gson.Gson;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameSerializeTest {

    @Test
    void serializeGame() {

        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player("player0", 0, TowerColor.BLACK, Player.Magician.MARIN));
        players.add(new Player("player1", 1, TowerColor.WHITE, Player.Magician.XERXES));
        players.add(new Player("player2", 2, TowerColor.GRAY, Player.Magician.RIAS));

        Game game = new Game(false, players);
        game.setCurrentTurn(game.getPlayerById(0));
        game.calculateQueue(List.of(AssistCard.ASSISTANT_0, AssistCard.ASSISTANT_1, AssistCard.ASSISTANT_2));

        String json = new Gson().toJson(game);

        Game deserialized = new Gson().fromJson(json, Game.class);
        deserialized.fixReferences();

        assertNotNull(deserialized);

        for (Player p : deserialized.getPlayers()) {
            assertNotNull(p.getBoard());
            assertNotNull(p.getGraveyard());
        }

        for (PlayerBoard p : deserialized.getGameBoard().getPlayerBoards()) {
            assertNotNull(p.getOwner());
        }

        assertEquals(3, deserialized.getQueue().size());
        assertSame(deserialized.getCurrentTurn(), deserialized.getPlayerById(0));
        assertSame(deserialized.getPlayerById(0), deserialized.getCurrentTurn());
        assertSame(deserialized.getQueue().get(0), deserialized.getPlayerById(0));
        assertSame(deserialized.getQueue().get(1), deserialized.getPlayerById(1));
        assertSame(deserialized.getQueue().get(2), deserialized.getPlayerById(2));
    }
}
