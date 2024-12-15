package it.polimi.ingsw.client;

import it.polimi.ingsw.Support;
import it.polimi.ingsw.model.AssistCard;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.SPColor;
import it.polimi.ingsw.model.characters.CharacterEnum;
import it.polimi.ingsw.model.characters.CharacterParametersBase;
import it.polimi.ingsw.network.ClientGameState;
import it.polimi.ingsw.network.GameSettings;
import it.polimi.ingsw.network.messages.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author edoardo, daniele
 * This class handles the logic of the game on the client side (controller)
 */
public class GameManager implements Consumer<Message> {

    private final List<Message> messageQueue;
    private NetworkManager gameNetman;
    private NetworkManager controlNetman;

    private Broadcaster broadcaster;
    private Game localModel;
    private IView view;
    private ClientGameState currentState = ClientGameState.CHOOSE_ASSIST;
    private ClientGameState mainState;
    private int myPlayerID;
    private boolean noRequestCharacter = false;
    private int studentsMoved = 0;
    private final boolean fixupCurrentStateDiskFuse = false;

    /**
     * This creates a GameManager directly to a specific Game
     *
     * @param serverIP   The IP of the server
     * @param serverPort The port of the specific **GAME** server to connect to
     */
    public GameManager(@NotNull String serverIP, int serverPort) {
        this.gameNetman = new NetworkManager(serverIP, serverPort, this);
        this.messageQueue = new ArrayList<>();
    }

    /**
     * This creates a GameManager that initializes the control connection, the game must be selected and started separately
     *
     * @param serverIP The IP of the server
     */
    public GameManager(@NotNull String serverIP) {
        this.controlNetman = new NetworkManager(serverIP, Support.SERVER_CONTROL_PORT, this);
        this.messageQueue = new ArrayList<>();
    }

    public GameManager() {
        this.messageQueue = new ArrayList<>();
    }

    public void changeEndpoint(String serverIP) {
        if (gameNetman != null && gameNetman.isConnected() && !Objects.equals(gameNetman.getServerIP(), serverIP)) {
            gameNetman.disconnect();
            gameNetman = null;
        }

        if (controlNetman == null) {
            controlNetman = new NetworkManager(serverIP, Support.SERVER_CONTROL_PORT, this);
        }

        if (controlNetman.isConnected() && !Objects.equals(controlNetman.getServerIP(), serverIP)) {
            controlNetman.disconnect();
            controlNetman = new NetworkManager(serverIP, Support.SERVER_CONTROL_PORT, this);
        }
    }

    /**
     * Starts broadcaster
     *
     * @param consumer param
     */
    public void runBroadcaster(Consumer<String> consumer) {
        if (broadcaster == null) broadcaster = new Broadcaster(consumer);

        if (broadcaster.isRunning()) return;

        broadcaster.start();
    }

    /**
     * Stops broadcaster
     */
    public void stopBroadcaster() {
        if (broadcaster == null) return;

        broadcaster.stop();
    }

    private boolean viewReady() {
        return view != null;
    }

    /**
     * Starts a game on an already specified port (Constructor)
     *
     * @throws IOException Network IO Error
     * @deprecated
     */
    public void start() throws IOException {
        if (!gameNetman.isConnected()) gameNetman.connect();

        if (viewReady())
            view.beginRequestLoginInfo(null);
    }

    /**
     * This reconnects this game manager to a specific game
     *
     * @param username Player Username
     * @param mage     Player Mage
     * @param gamePort Game port to connect to
     */
    public void reconnectToGame(String username, Player.Magician mage, int gamePort) {
        if (gameNetman != null && gameNetman.isConnected()) gameNetman.disconnect();

        gameNetman = new NetworkManager(controlNetman.getServerIP(), gamePort, this);

        try {
            gameNetman.connect();
        } catch (IOException exc) {
            exc.printStackTrace();
            throw new RuntimeException("Could not connect to game");
        }

        Message response = gameNetman.sendMessage(new JoinRequestMessage(username, mage), 2000);

        if (response == null) throw new RuntimeException("Could not connect to game");

        if (response instanceof JoinResponseMessage jrm) {
            if (jrm.getRespCode() != JoinResponseMessage.JoinReponseCode.JOIN_OK)
                throw new InvalidLoginException("u dumb baka yarou", jrm.getRespCode());

            this.myPlayerID = jrm.getPlayerID();
            return;
        }

        throw new InvalidLoginException("message not right type", JoinResponseMessage.JoinReponseCode.JOIN_FAIL);
    }

    /**
     * Uses the control connection to retrieve the list of available games
     *
     * @return The list of available games
     */
    public List<GameSettings> getAvailableGames() {

        if (!controlNetman.isConnected()) {
            try {
                controlNetman.connect();
            } catch (IOException exc) {
                throw new RuntimeException("Could not connect to server");
            }
        }

        Message response = controlNetman.sendMessage(new ListGamesRequestMessage(), 2000);

        if (response instanceof ListGamesResponseMessage castedMessage) {
            return castedMessage.getGames();
        }

        return null;
    }

    /**
     * Requests the server to create a new game
     *
     * @param lobbyName  name of the lobby
     * @param lobbySize  lobby size
     * @param expertMode expert mode
     * @return the port of the created game
     * @deprecated
     */
    public int createNewGame(String lobbyName, int lobbySize, boolean expertMode, boolean saveOnDisk) {
        return this.createNewGame(new GameSettings(lobbyName, lobbySize, -1, expertMode, saveOnDisk));
    }

    /**
     * Requests the server to create a new game
     *
     * @param settings chosen game settings
     * @return the port of the created game
     */
    public int createNewGame(GameSettings settings) {

        Message response = controlNetman.sendMessage(new CreateNewGameRequestMessage(settings));

        if (response instanceof CreateNewGameResponseMessage castedMessage) {

            if (!castedMessage.isSuccess()) {
                throw new RuntimeException("Could not create game");
            }

            return castedMessage.getPortNumber();
        }

        throw new RuntimeException("invalid message received");
    }

    /**
     * Registers the view that must be controlled by this game manager
     *
     * @param view the f*cking view
     */
    public void registerView(@NotNull IView view) {
        this.view = view;

        synchronized (messageQueue) {
            for (Message message : messageQueue) {
                this.accept(message);
            }
        }
    }

    /**
     * I think this you can figure out baka
     */
    public void unregisterView() {
        this.view = null;
    }

    /**
     * Returns all opponents
     *
     * @return all opponents
     */
    public List<Player> getOpponentPlayers() {
        if (localModel == null) return new ArrayList<>();
        return localModel.getPlayers().stream().filter(p -> p.getId() != getMyPlayer().getId()).sorted(Comparator.comparingInt(Player::getId)).collect(Collectors.toList());
    }

    /**
     * Logs into the game
     *
     * @param nickname nickname of the login player
     * @param mage     selected mage
     * @throws IOException Network error
     */
    public void endRequestLoginInfo(String nickname, Player.Magician mage) throws IOException {

        Message response = gameNetman.sendMessage(new JoinRequestMessage(
                nickname, mage
        ));

        if (response instanceof JoinResponseMessage jrm) {
            if (jrm.getRespCode() == JoinResponseMessage.JoinReponseCode.JOIN_FAIL_USERNAME_TAKEN || jrm.getRespCode() == JoinResponseMessage.JoinReponseCode.JOIN_FAIL_MAGE_TAKEN) {
                if (viewReady()) view.beginRequestLoginInfo(jrm.getRespCode());
            } else if (jrm.getRespCode() == JoinResponseMessage.JoinReponseCode.JOIN_FAIL || jrm.getRespCode() == JoinResponseMessage.JoinReponseCode.JOIN_FAIL_UNEXPECTED_USERNAME) {
                if (viewReady()) view.exitGame("Boh");
                System.exit(-1);
            } else {
                myPlayerID = jrm.getPlayerID();
                if (viewReady()) view.notifyLoginOk();
            }
        } else {
            throw new RuntimeException("Received invalid message");
        }
    }

    /**
     * The user played the assistant card
     *
     * @param assistCard the assistant card
     */
    public void endRequestAssistCard(AssistCard assistCard) {
        localModel.getPlayerById(myPlayerID).getGraveyard().addCard(assistCard);
        gameNetman.sendMessageAsync(new PlayCardMessage(assistCard));
    }

    /**
     * The user moved the student
     *
     * @param student the student
     */
    public void endRequestMoveStudent(SPColor student, MoveStudentMessage.Destination destination, int nIsland) {

        gameNetman.sendMessageAsync(new MoveStudentMessage(student, destination, nIsland));

    }

    /**
     * The user moved mother nature
     *
     * @param moves number of moves
     */
    public void endRequestMotherNatureMoves(int moves) {

        gameNetman.sendMessageAsync(new MoveMotherNatureMessage(moves));

    }

    /**
     * The user chose the cloud
     *
     * @param cloud the id of the cloud
     */
    public void endRequestCloud(int cloud) {

        gameNetman.sendMessageAsync(new ChooseCloudMessage(cloud));

    }

    /**
     * The user requested to play a character
     *
     * @param characterEnum the character
     * @param object        eventual parameters for the move
     */
    public void endRequestCharacter(CharacterEnum characterEnum, CharacterParametersBase object) {
        endRequestCharacter(characterEnum, object, false);
    }

    /**
     * The user requested to play a character
     *
     * @param characterEnum the character
     * @param object        eventual parameters for the move
     */
    public void endRequestCharacter(CharacterEnum characterEnum, CharacterParametersBase object, boolean dontAskAgain) {
        gameNetman.sendMessageAsync(new ChooseCharacterMessage(characterEnum, object));
        this.noRequestCharacter = dontAskAgain;
    }

    /**
     * @return The Player controlled by this game manager
     */
    public Player getMyPlayer() {
        return localModel.getPlayerById(myPlayerID);
    }

    /**
     * @return The Model TODO should return a copy
     */
    public Game getModel() {
        return localModel; // questo va assolutamente copiato... prima o poi (più prima che poi)
    }

    /**
     * return current island
     *
     * @return current island
     */
    public int getCurrentIslandIndex() {
        for (int i = 0; i < localModel.getGameBoard().getIslands().size(); i++) {
            if (localModel.getGameBoard().getIslands().get(i).getMotherNature())
                return i;
        }

        return -1;
    }

    /**
     * Return available moves
     *
     * @return moves
     */
    public int getMoves() {
        int baseMoves = getMyPlayer().getGraveyard().getTopCard().getMoves();
        if (localModel.getGameBoard().getActiveCharacter() == null) return baseMoves;
        return localModel.getGameBoard().getActiveCharacter().getEnumType() == CharacterEnum.POSTMAN ? baseMoves + 2 : baseMoves;
    }

    /**
     * This is the main function that accepts async messages from the server
     *
     * @param message the input argument
     */
    @Override
    public synchronized void accept(Message message) {

        // qui ricevi tutti i vari messaggi asincroni

        if (message instanceof PingMessage) {
            gameNetman.sendMessageAsync(new PongMessage(message.getMessageId()));
            return;
        }

        if (!viewReady()) {
            synchronized (messageQueue) {
                messageQueue.add(message);
            }
            return;
        }

        int maxStudent;

        if (message instanceof GameStartedMessage gsm) {
            this.localModel = gsm.getModel();
            this.localModel.fixReferences();

            mainState = gsm.getClientGameState();
            currentState = gsm.getClientGameState();

            view.notifyGameStarted();
            view.modelUpdated(this.localModel);
        } else if (message instanceof ModelUpdateMessage mum) {
            this.localModel = mum.getModel();
            this.localModel.fixReferences();
            view.modelUpdated(this.localModel);
        } else if (message instanceof TurnChangedMessage tcm) {
            if (tcm.getCurrentTurnPlayerID() == this.myPlayerID) { // TODO

                this.localModel.setCurrentTurn(this.localModel.getPlayerById(tcm.getCurrentTurnPlayerID()));
                view.turnChanged(localModel.getPlayerById(myPlayerID));
                if (localModel.getMode() && currentState == ClientGameState.MOVE_STUDENTS && !noRequestCharacter) {
                    mainState = currentState;
                    currentState = ClientGameState.CHOOSE_CHARACTER;
                    studentsMoved = 0;
                    view.beginRequestCharacter(null);
                } else {
                    if (currentState == ClientGameState.CHOOSE_ASSIST) {
                        noRequestCharacter = false;
                        view.beginRequestAssistantCard(false);
                    } else {
                        studentsMoved = 0;
                        view.beginRequestStudentMove(null, null);
                    }
                }
            } else {
                view.turnChanged(localModel.getPlayerById(tcm.getCurrentTurnPlayerID())); // informiamo la view di chi è questo turno
            }
        } else if (message instanceof GameEndedMessage gem) {
            currentState = ClientGameState.GAME_ENDED;
            view.gameEnded(localModel.getPlayerById(gem.getWinnerID()));
        } else if (message instanceof ClientDisconnectMessage cdm) {
            if (currentState != ClientGameState.GAME_ENDED)
                view.exitGame("The server has closed the game because %s left"
                        .formatted(localModel.getPlayerById(cdm.getPlayerID()).getName()));
            currentState = ClientGameState.GAME_ENDED;
        } else if (message instanceof RequestDisconnectMessage) {
            if (gameNetman != null) gameNetman.disconnect();
            if (controlNetman != null) controlNetman.disconnect();

            if (currentState != ClientGameState.GAME_ENDED)
                view.exitGame("The server has closed the game");
            currentState = ClientGameState.GAME_ENDED;
        } else if (message instanceof EvaluateMoveMessage emm) {
            this.localModel = emm.getModel();
            this.localModel.fixReferences();
            maxStudent = 4;
            if (emm.getModel().getPlayers().size() == 2) maxStudent = 3;
            view.modelUpdated(this.localModel);
            switch (currentState) {
                case CHOOSE_ASSIST -> {
                    if (emm.getResponse() == EvaluateMoveMessage.MoveResponse.MOVE_KO) {
                        view.beginRequestAssistantCard(true);
                    } else {
                        currentState = ClientGameState.MOVE_STUDENTS;
                    }
                }
                case MOVE_STUDENTS -> {
                    if (emm.getResponse() == EvaluateMoveMessage.MoveResponse.MOVE_OK)
                        studentsMoved++;
                    if (studentsMoved < maxStudent && view != null) {
                        view.beginRequestStudentMove(emm.getResponse(), emm.getReason());
                    } else {
                        currentState = ClientGameState.MOVE_MN;
                        if (localModel.getMode() && !noRequestCharacter) {
                            mainState = currentState;
                            currentState = ClientGameState.CHOOSE_CHARACTER;
                            view.beginRequestCharacter(null);
                        } else view.beginRequestMotherNatureMoves(false);
                    }
                }
                case MOVE_MN -> {
                    if (emm.getResponse() == EvaluateMoveMessage.MoveResponse.MOVE_KO) {
                        view.beginRequestMotherNatureMoves(true);
                    } else {
                        currentState = ClientGameState.CHOOSE_CLOUD;
                        if (localModel.getMode() && !noRequestCharacter) {
                            mainState = currentState;
                            currentState = ClientGameState.CHOOSE_CHARACTER;
                            view.beginRequestCharacter(null);
                        } else view.beginRequestCloud(false);
                    }
                }
                case CHOOSE_CLOUD -> {
                    if (emm.getResponse() == EvaluateMoveMessage.MoveResponse.MOVE_KO) {
                        view.beginRequestCloud(true);
                    } else {
                        currentState = ClientGameState.CHOOSE_ASSIST;
                    }
                }
                case CHOOSE_CHARACTER -> {
                    if (emm.getResponse() == EvaluateMoveMessage.MoveResponse.MOVE_KO) {
                        noRequestCharacter = false;
                        view.beginRequestCharacter(emm.getReason());
                    } else {
                        if (!Objects.equals(emm.getReason(), "ok my friend")) noRequestCharacter = true;
                        currentState = mainState;
                        switch (currentState) {
                            case MOVE_STUDENTS -> view.beginRequestStudentMove(null, null);
                            case MOVE_MN -> view.beginRequestMotherNatureMoves(false);
                            case CHOOSE_CLOUD -> view.beginRequestCloud(false);
                        }
                    }
                }
            }
        }
    }

    /**
     * Disconnects the current gamemanager
     */
    public void disconnect() {
        if (gameNetman != null && gameNetman.isConnected()) {
            gameNetman.sendMessageAsync(new DisconnectMessage());
            gameNetman.disconnect();
        }

        if (controlNetman != null && controlNetman.isConnected()) controlNetman.disconnect();
    }

}
