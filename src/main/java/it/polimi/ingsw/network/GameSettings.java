package it.polimi.ingsw.network;

public class GameSettings {
    private final String lobbyName;
    private final int lobbySize;
    private final boolean expertMode;
    private final boolean saveOnDisk;
    private int port;
    private boolean fromDisk;

    public GameSettings(String lobbyName, int lobbySize, int port, boolean expertMode, boolean fromDisk, boolean saveOnDisk) {
        this.lobbyName = lobbyName;
        this.lobbySize = lobbySize;
        this.port = port;
        this.expertMode = expertMode;
        this.fromDisk = fromDisk;
        this.saveOnDisk = saveOnDisk;
    }

    public GameSettings(GameSettings s) {
        this(s.getLobbyName(), s.getLobbySize(), s.getPort(), s.getExpertMode(), s.isFromDisk(), s.getSaveOnDisk());
    }

    public GameSettings(String lobbyName, int lobbySize, int port, boolean expertMode, boolean saveOnDisk) {
        this(lobbyName, lobbySize, port, expertMode, false, saveOnDisk);
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public int getLobbySize() {
        return lobbySize;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean getExpertMode() {
        return expertMode;
    }

    @Override
    public String toString() {
        if (fromDisk) {
            return "Unfinished game %s".formatted(lobbyName);
        }

        return "Game %s on port %d (%d players%s)".formatted(lobbyName, port, lobbySize, expertMode ? ", expert mode" : "");
    }

    public boolean isFromDisk() {
        return fromDisk;
    }

    public void setFromDisk(boolean fromDisk) {
        this.fromDisk = fromDisk;
    }

    public boolean getSaveOnDisk() {
        return saveOnDisk;
    }
}
