package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.network.ClientGameState;

public class GameStartedMessage extends Message {

    final private Game model;
    private final boolean fromDisk;

    private final ClientGameState clientGameState;

    public GameStartedMessage(Game model, boolean fromDisk, ClientGameState clientGameState) {
        super();
        this.model = model;
        this.fromDisk = fromDisk;
        this.clientGameState = clientGameState;
    }

    public GameStartedMessage(long messageId, Game model, boolean fromDisk, ClientGameState clientGameState) {
        super(messageId);
        this.model = model;
        this.fromDisk = fromDisk;
        this.clientGameState = clientGameState;
    }

    //get client game state
    public ClientGameState getClientGameState() {
        return clientGameState;
    }

    public Game getModel() {
        return model;
    }

    public boolean isFromDisk() {
        return fromDisk;
    }
}
