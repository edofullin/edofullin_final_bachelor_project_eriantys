package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.GameSettings;

public class CreateNewGameRequestMessage extends Message {

    private final GameSettings settings;

    public CreateNewGameRequestMessage(GameSettings settings) {
        this.settings = settings;
    }

    public GameSettings getSettings() {
        return settings;
    }
}
