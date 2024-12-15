package it.polimi.ingsw.client;

import it.polimi.ingsw.network.messages.JoinResponseMessage;

/**
 * Thrown when the login is invalid
 */
public class InvalidLoginException extends RuntimeException {

    private final JoinResponseMessage.JoinReponseCode errorCode;

    public InvalidLoginException(String message, JoinResponseMessage.JoinReponseCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public InvalidLoginException(JoinResponseMessage.JoinReponseCode errorCode) {
        this.errorCode = errorCode;
    }

    public JoinResponseMessage.JoinReponseCode getErrorCode() {
        return errorCode;
    }
}
