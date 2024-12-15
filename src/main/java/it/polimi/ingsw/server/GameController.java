package it.polimi.ingsw.server;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.network.GameSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * This represents a single game, with both its lobby and match.
 */
public class GameController implements Runnable {

    private static final Logger logger = LogManager.getLogger(GameController.class);
    private final IConnectionManager connectionManager;
    private final Consumer<Game> gameInitialization;

    private final GameSettings gameSettings;
    private final GameSave savegame;
    private MatchController match;
    private boolean aborted = false;

    public GameController(GameSettings settings, Consumer<Game> gameInitialization) {
        this.gameSettings = settings;
        this.connectionManager = new TCPConnectionManager(gameSettings.getPort(), gameSettings.getLobbySize());
        this.gameInitialization = gameInitialization;
        this.savegame = null;
    }


    public GameController(GameSave savegame, Consumer<Game> gameInitialization) {
        this.gameSettings = savegame.getSettings();
        this.connectionManager = new TCPConnectionManager(gameSettings.getPort(), gameSettings.getLobbySize(), new ArrayList<>(savegame.getModel().getPlayers()));
        this.gameInitialization = gameInitialization;
        this.savegame = savegame;
    }

    /**
     * Registers the listener for events
     *
     * @param listener the listener
     */
    public void registerActionListener(ActionListener listener) {
        connectionManager.registerActionListener(listener);
    }

    /**
     * Run the game waiting for players to connect
     */
    @Override
    public void run() {

        try {
            this.connectionManager.waitClients();
        } catch (InterruptedException exc) {
            logger.warn("waitclients interrupted");
            System.exit(-1);
        }

        if (connectionManager.getClients().size() == 0) {
            aborted = true;
            logger.info("Match %s aborted".formatted(gameSettings.getLobbyName()));
            return;
        }

        this.match = gameSettings.isFromDisk() ? new MatchController(this.connectionManager, savegame) : new MatchController(this.connectionManager, gameSettings);
        match.runGame(gameInitialization); // Sincrona, termina quando termina il gioco

        this.connectionManager.disconnectAllClients();
    }

    public int getPort() {
        return gameSettings.getPort();
    }


    public GameSettings getSettings() {
        return gameSettings;
    }

    public MatchController.MatchState getMatchState() {
        if (aborted) return MatchController.MatchState.GAME_TERMINATED;

        if (this.match == null) return MatchController.MatchState.GAME_NOT_STARTED;
        return match.getMatchState();
    }

    /**
     * Halts the current game
     */
    public void halt() {
        match.halt();
    }
}
