package it.polimi.ingsw.server;

import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.Support;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.GameSettings;
import it.polimi.ingsw.network.messages.Message;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents how a game is saved on disk
 */
public class GameSave {

    private final Game model;

    private final Date saveDate;
    private final GameSettings settings;

    public GameSave(Date saveDate, Game model, GameSettings settings) {
        this.model = model;
        this.saveDate = saveDate;
        this.settings = settings;
    }

    /**
     * Loads a game from a file.
     *
     * @param lobbyName the lobby to load
     * @return the loaded game
     * @throws FileNotFoundException if the file does not exist
     */
    public static GameSave load(String lobbyName) throws FileNotFoundException {
        File file = new File(Support.getAppDataPath().resolve("saves").resolve(lobbyName + ".json").toString());

        JsonReader reader = new JsonReader(new FileReader(file.toString()));

        return Message.GSON().fromJson(reader, GameSave.class);
    }

    /**
     * @return
     * @deprecated
     */
    public Game model() {
        return getModel();
    }

    public Game getModel() {
        return model;
    }

    public Date getSaveDate() {
        return saveDate;
    }

    /**
     * @return
     * @deprecated
     */
    public GameSettings settings() {
        return getSettings();
    }

    public GameSettings getSettings() {
        return settings;
    }

    public List<String> getPlayerNames() {
        return model.getPlayers().stream().map(Player::getName).collect(Collectors.toList());
    }

    public String getSaveFileName() {
        return settings.getLobbyName();
    }
}
