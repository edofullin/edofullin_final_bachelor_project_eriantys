package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.Game;

public class EvaluateMoveMessage extends Message {

    final private MoveResponse response;
    final private String reason;
    final private Game model;

    public EvaluateMoveMessage(MoveResponse response, String reason, Game model) {
        this.response = response;
        this.reason = reason;
        this.model = model;
    }

    public EvaluateMoveMessage(MoveResponse response, Game model) {
        this(response, "", model);
    }

    public EvaluateMoveMessage(long messageId, MoveResponse response, String reason, Game model) {
        super(messageId);
        this.response = response;
        this.reason = reason;
        this.model = model;
    }

    public MoveResponse getResponse() {
        return response;
    }

    public String getReason() {
        return reason;
    }

    public Game getModel() {
        return model;
    }

    public enum MoveResponse {
        MOVE_OK,
        MOVE_KO
    }
}
