package it.polimi.ingsw.client.cli;

/**
 * Escape code for the CLI
 */
public enum EscapeCode {

    EMPTY_LINE(""),
    RED_TEXT("\u001b[91m"),
    GREEN_TEXT("\u001b[92m"),
    YELLOW_TEXT("\u001b[93m"),
    BLUE_TEXT("\u001b[94m"),
    PINK_TEXT("\u001b[95m"),
    CYAN_TEXT("\u001b[96m"),
    WHITE_TEXT("\u001b[97m"),
    CURSOR_TO_START("\u001b[0;0H"),
    CURSOR_TO_LAST_ACTION("\u001b[8F"),
    CLEAR_TERMINAL("\u001b[0J");

    final private String code;

    EscapeCode(String code) {
        this.code = code;
    }

    /**
     * Get the code
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return getCode();
    }
}
