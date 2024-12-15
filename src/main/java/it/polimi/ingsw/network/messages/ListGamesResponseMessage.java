package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.GameSettings;

import java.util.List;

public class ListGamesResponseMessage extends Message {

    private final List<GameSettings> games;

    public ListGamesResponseMessage(long messageId, List<GameSettings> games) {
        super(messageId);
        this.games = games;
    }

    public List<GameSettings> getGames() {
        return games;
    }
}
