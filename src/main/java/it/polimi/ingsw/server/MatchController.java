package it.polimi.ingsw.server;

import it.polimi.ingsw.Support;
import it.polimi.ingsw.model.AssistCard;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.characters.CharacterCard;
import it.polimi.ingsw.model.characters.InvalidCharacterArgumentException;
import it.polimi.ingsw.network.GameSettings;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.server.gamephases.GameStarting;
import it.polimi.ingsw.server.gamephases.IGamePhase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

/**
 * This class handles a match and how the game evolves
 */
public class MatchController implements ClientMessageSubscriber {

    final private static Logger logger = LogManager.getLogger(MatchController.class);
    private final IConnectionManager connManager;
    private final Semaphore phaseLock;
    private final boolean expertMode;
    private final GameSettings gameSettings;
    private IGamePhase currentPhase;
    private Game gameModel;
    private MatchState matchState;
    private boolean loadedFromDisk = false;

    public MatchController(IConnectionManager connManager, GameSettings settings) {
        this.connManager = connManager;
        this.phaseLock = new Semaphore(0);
        this.matchState = MatchState.GAME_NOT_STARTED;
        this.expertMode = settings.getExpertMode();
        this.gameSettings = settings;
    }


    public MatchController(IConnectionManager connManager, GameSave save) {
        this(connManager, save.settings());

        gameModel = save.model();
        gameModel.fixReferences();
        loadedFromDisk = true;
    }

    public MatchState getMatchState() {
        return matchState;
    }

    public void setState(IGamePhase gamePhase) {
        this.currentPhase = gamePhase;
    }

    public Semaphore getPhaseLock() {
        return phaseLock;
    }

    /**
     * Run the game
     */
    public void runGame(Consumer<Game> gameInitFunction) {
        if (logger.isDebugEnabled())
            logger.debug("GameController started");

        if (gameModel == null) {
            // il game non arriva da disco, ne creo uno nuovo
            gameModel = new Game(this.expertMode, connManager.getClients().stream().map(Client::getGamePlayer).toList());
        } else {
            // fixup players references in clients (for the case of reconnection)
            for (Client client : connManager.getClients()) {
                String searchName = client.getGamePlayer().getName();
                Player modelPlayer = gameModel.getPlayers().stream().filter(p -> p.getName().equals(searchName)).findFirst().get();
                modelPlayer.setId(client.getId());
                client.setGamePlayer(modelPlayer);
            }
        }

        // funzione di inizializzazione del game (per debug)
        if (gameInitFunction != null) {
            gameInitFunction.accept(gameModel);
        }

        this.connManager.registerMessageSubscriber(this);
        this.matchState = MatchState.GAME_RUNNING;

        currentPhase = new GameStarting();

        while (isGameRunning() && currentPhase != null) {

            if (logger.isDebugEnabled())
                logger.debug(String.format("Current phase %s", currentPhase.getClass().getName()));

            currentPhase.preAction(this, connManager);

            try {
                synchronized (phaseLock) {
                    phaseLock.acquire();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (matchState == MatchState.GAME_REQUEST_TERMINATE)
                break;

            currentPhase.postAction(this, connManager);

            if (logger.isDebugEnabled())
                logger.debug(String.format("Current phase %s ended", currentPhase.getClass().getName()));

            currentPhase = currentPhase.nextPhase(this);
        }

        logger.info("Match %s has ended".formatted(gameSettings.getLobbyName()));
        this.matchState = MatchState.GAME_TERMINATED;
    }

    public Game getGameModel() {
        return this.gameModel;
    }

    /**
     * Release the current phase lock
     */
    public void releasePhase() {
        if (currentPhase != null) {
            logger.debug("released lock for phase %s".formatted(currentPhase.getClass().getSimpleName()));
        }

        this.phaseLock.release();
    }

    /**
     * Called when a message is received from a client
     */
    @Override
    public void receivedClientMessageEvent(Client c, Message m) {
        if (m instanceof ChooseCharacterMessage ccm) {
            this.handleCharacter(c, ccm);
        }

        if (m instanceof DisconnectMessage) {
            connManager.broadcastMessage(new ClientDisconnectMessage(c.getId()));
            this.matchState = MatchState.GAME_REQUEST_TERMINATE;
            releasePhase();
            return;
        }

        if (currentPhase != null) {
            currentPhase.messageReceived(this, c, m);
        }
    }

    private void handleCharacter(Client c, ChooseCharacterMessage chooseCharacterMessage) {

        if (chooseCharacterMessage.getCharacter() == null && chooseCharacterMessage.getObject() == null) {
            c.sendMessageAsync(new EvaluateMoveMessage(EvaluateMoveMessage.MoveResponse.MOVE_OK, "ok my friend", gameModel));
            return;
        }

        CharacterCard selectedCharacter = gameModel.getGameBoard().getAvailableCharacterByType(chooseCharacterMessage.getCharacter());

        if (selectedCharacter == null) {
            c.sendMessageAsync(new EvaluateMoveMessage(EvaluateMoveMessage.MoveResponse.MOVE_KO, "invalid character", gameModel));
            return;
        }

        if (gameModel.getGameBoard().getActiveCharacter() != null) {
            c.sendMessageAsync(new EvaluateMoveMessage(EvaluateMoveMessage.MoveResponse.MOVE_KO, "a character is active already", gameModel));
            return;
        }

        if (!gameModel.getCurrentTurn().equals(c.getGamePlayer())) return;

        if (logger.isDebugEnabled()) {
            if (gameModel.getCurrentTurn().getCoins() < selectedCharacter.getCost()) {
                c.sendMessageAsync(new EvaluateMoveMessage(EvaluateMoveMessage.MoveResponse.MOVE_KO, "you don't have enough coins to activate this effect", gameModel));
                return;
            }
        }

        gameModel.getCurrentTurn().modifyCoins(-selectedCharacter.getCost());

        // controllare correttezza dei parametri dentro le varie applyEffect o minchiate similari

        try {
            selectedCharacter.applyEffect(gameModel, chooseCharacterMessage.getObject());
        } catch (InvalidCharacterArgumentException exc) {
            // diciamo all'utente che ha pippato male
            c.sendMessageAsync(new EvaluateMoveMessage(EvaluateMoveMessage.MoveResponse.MOVE_KO, exc.getMessage(), gameModel));
            return;
        }

        selectedCharacter.setActive(true);
        selectedCharacter.setUsed(true);

        gameModel.getGameBoard().updateProfessors();

        c.sendMessageAsync(new EvaluateMoveMessage(EvaluateMoveMessage.MoveResponse.MOVE_OK, null, gameModel));
    }

    public List<Client> getQueue() {
        List<Client> clients = new ArrayList<>();

        this.gameModel.getQueue().forEach(p -> clients.add(
                connManager.getClients().stream()
                        .filter(c -> c.getGamePlayer().equals(p)).findFirst()
                        .get()
        ));

        return clients;
    }

    public void setQueue(List<AssistCard> playedCards) {
        this.gameModel.calculateQueue(playedCards);
    }

    /**
     * Saves the current game to disk
     *
     * @throws IOException if an error occurs while saving the game
     */
    public void saveGame() throws IOException {

        File gameDir = Support.getAppDataPath().resolve("saves").toFile();

        if (!gameDir.exists())
            gameDir.mkdirs();

        logger.debug("Saving game under %s".formatted(Support.getAppDataPath().toAbsolutePath()));

        GameSave save = new GameSave(new Date(), gameModel, gameSettings);
        String data = Message.GSON().toJson(save);

        Files.writeString(Support.getAppDataPath().resolve("saves").resolve(save.getSaveFileName() + ".json"), data, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }

    /**
     * Delete the game from disk
     */
    public void deleteGameFile() {
        GameSave save = new GameSave(new Date(), gameModel, gameSettings);
        File gameFile = Support.getAppDataPath().resolve(save.getSaveFileName()).toFile();

        if (gameFile.exists()) {
            gameFile.delete();
        }
    }

    public Game getStrippedGameModel(Client c) {
        return gameModel;
    }

    public Game getStrippedGameModel() {
        return gameModel;
    }

    public boolean isGameRunning() {
        return matchState == MatchState.GAME_RUNNING;
    }

    public boolean isLoadedFromDisk() {
        return loadedFromDisk;
    }

    /**
     * Halts the match
     */
    public void halt() {
        this.matchState = MatchState.GAME_REQUEST_TERMINATE;
        connManager.disconnectAllClients();
        releasePhase();
    }

    public GameSettings getGameSettings() {
        return this.gameSettings;
    }

    public enum MatchState {
        GAME_NOT_STARTED,
        GAME_RUNNING,
        GAME_REQUEST_TERMINATE,
        GAME_TERMINATED
    }
}
