package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.Game;

public class ModelUpdateMessage extends Message {

    final private Game model;

    public ModelUpdateMessage(Game model) {
        super();
        this.model = model;
    }

    public ModelUpdateMessage(long messageId, Game model) {
        super(messageId);
        this.model = model;
    }

    public Game getModel() {
        return model;
    }
}
