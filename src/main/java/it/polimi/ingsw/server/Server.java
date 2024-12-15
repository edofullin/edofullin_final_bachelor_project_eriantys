package it.polimi.ingsw.server;

import it.polimi.ingsw.Support;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.network.GameSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.event.ActionListener;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Represents a server, contains all matches
 */
public class Server {

    private static final Logger logger = LogManager.getLogger(Server.class);

    private final ArrayList<GameController> gameControllers;
    private final NetworkGamesManager lobbyManager;
    private final Executor mainExecutor;
    private Thread lobbyManagerThread;

    public Server() {
        this.gameControllers = new ArrayList<>();
        this.lobbyManager = new NetworkGamesManager(this);
        this.mainExecutor = Executors.newCachedThreadPool();
    }

    /**
     * Starts a new match and doesnt wait for game started from a saved match
     *
     * @param savegame game save file
     * @param gameInit a function to modify the game after it has started (debug purpose)
     * @param listener callback for match events
     * @return the port used for the connection by clients
     */
    public int startNewMatchAsync(GameSave savegame, Consumer<Game> gameInit, ActionListener listener) {
        GameSettings settings = savegame.getSettings();
        int effectivePort = settings.getPort() == -1 ? findFirstAvailablePort() : settings.getPort();

        if (gameControllers.stream().anyMatch(gc -> gc.getPort() == effectivePort)) {
            logger.error("Cannot start server on port %d".formatted(effectivePort));
            throw new RuntimeException("server cannot start on a port where another server is running");
        }

        settings.setPort(effectivePort);
        GameController newController = new GameController(savegame, gameInit);

        gameControllers.add(
                newController
        );

        if (listener != null) {
            newController.registerActionListener(listener);
        }

        logger.info("Match %s started on port %d".formatted(newController.getSettings().getLobbyName(), newController.getPort()));

        mainExecutor.execute(newController);

        return effectivePort;
    }

    /**
     * Starts a new match and doesnt wait for game started from a saved match
     *
     * @param settings game settings
     * @param gameInit a function to modify the game after it has started (debug purpose)
     * @param listener callback for match events
     * @return the port used for the connection by clients
     */
    public int startNewMatchAsync(GameSettings settings, ActionListener listener, Consumer<Game> gameInit) {
        int effectivePort = settings.getPort() == -1 ? findFirstAvailablePort() : settings.getPort();

        if (gameControllers.stream().anyMatch(gc -> gc.getPort() == effectivePort)) {
            logger.error("Cannot start server on port %d".formatted(effectivePort));
            throw new RuntimeException("server cannot start on a port where another server is running");
        }

        settings.setPort(effectivePort);
        GameController newController = new GameController(settings, gameInit);

        gameControllers.add(
                newController
        );

        if (listener != null) {
            newController.registerActionListener(listener);
        }

        logger.info("Match %s started on port %d".formatted(newController.getSettings().getLobbyName(), newController.getPort()));

        mainExecutor.execute(newController);

        return effectivePort;
    }

    /**
     * Finds First Available Port
     *
     * @return port
     */
    private int findFirstAvailablePort() {
        if (gameControllers.size() == 0) return Support.SERVER_CONTROL_PORT + 1;

        return gameControllers.stream().mapToInt(GameController::getPort).max().getAsInt() + 1;
    }

    /**
     * Starts new game controller and waits for the server to be ready to accept connections
     *
     * @param settings game settings
     * @param gameInit a function to modify the game after it has started (debug purpose)
     * @return the port used for the connection by clients
     */
    public int startNewMatch(GameSettings settings, Consumer<Game> gameInit) {

        Object lock = new Object();

        int port = startNewMatchAsync(settings, e -> {
            if (e.getActionCommand().equals("server_ready")) {
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
        }, gameInit);

        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException exc) {
                logger.warn("startNewMatch interrupted");
            }
        }

        return port;
    }

    /**
     * Starts new game controller and waits for the server to be ready to accept connections
     *
     * @param save     game save file
     * @param gameInit a function to modify the game after it has started (debug purpose)
     * @return the port used for the connection by clients
     */
    public int startNewMatch(GameSave save, Consumer<Game> gameInit) {

        Object lock = new Object();

        int port = startNewMatchAsync(save, gameInit, e -> {
            if (Objects.equals(e.getActionCommand(), "server_ready")) {
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
        });

        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException exc) {
                logger.warn("startNewMatch interrupted");
            }
        }

        return port;
    }

    /**
     * forall game controllers returns their game settings
     *
     * @return game settings list
     */
    public List<GameSettings> getGamesList() {
        ArrayList<GameSettings> ret = new ArrayList<>();

        for (GameController gameController : gameControllers) {
            ret.add(gameController.getSettings());
        }

        return ret;
    }

    /**
     * forall game controllers returns their game settings if they are waiting for players
     *
     * @return game settings list
     */
    public List<GameSettings> getAvailableGamesList() {

        return gameControllers.stream()
                .filter(gc -> gc.getMatchState() == MatchController.MatchState.GAME_NOT_STARTED)
                .map(GameController::getSettings)
                .map(gameSettings -> {
                    GameSettings sett = new GameSettings(gameSettings);
                    sett.setFromDisk(false);
                    return sett;
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * starts lobby manager (which manages what games are running)
     */
    public void startLobbyManager() {
        lobbyManager.run();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> halt(2000)));
    }

    /**
     * starts lobby manager and catches exceptions in thread
     *
     * @param excHandler the handler for exceptions
     */
    public void startLobbyManager(Thread.UncaughtExceptionHandler excHandler) {
        lobbyManager.setExceptionHandler(excHandler);
        this.startLobbyManager();
    }

    /**
     * waits lobby manager ( prevent process end)
     */
    public void waitLobbyManager() {
        lobbyManager.waitCompletion();
    }

    /**
     * halts server
     *
     * @param stimeout how long to wait clients ending their game before shutting down the server
     */
    public void halt(int stimeout) {
        logger.info("Requesting server to close down");
        Duration timeout = Duration.ofSeconds(stimeout);

        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<Void>> futures = new ArrayList<>();

        for (GameController gameController : gameControllers) {
            futures.add(
                    executor.submit(() -> {
                        gameController.halt();
                        return null;
                    })
            );
        }

        for (Future<Void> future : futures) {
            try {
                future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
            } catch (TimeoutException | InterruptedException | ExecutionException e) {
                logger.error("Could not gracefully terminate all threads");
                return;
            }
        }
    }
}
