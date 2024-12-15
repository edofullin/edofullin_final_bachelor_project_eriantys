package it.polimi.ingsw.model;

import it.polimi.ingsw.model.characters.CharacterCard;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class Game {

    final private boolean expertMode;
    final private List<Player> players;
    private Board gameBoard;
    private Player currentTurn;
    private List<Player> playersQueue;

    public Game(boolean expertMode, List<Player> players) {
        this.expertMode = expertMode;
        this.players = players;
        this.gameBoard = new Board(players);
        this.playersQueue = new ArrayList<>(players);

        int students = 7;
        if (players.size() == 3) students += 2;

        for (Player player : this.players) {
            for (int i = 0; i < students; i++) {

                player.getBoard().addStudent(gameBoard.extractStudentFromPouch());
            }
        }

        if (expertMode) {
            gameBoard.initCharacters();

            for (CharacterCard characterCard : this.gameBoard.getCharacterCards()) {
                characterCard.initializeGame(this.gameBoard);
            }

            for (Player player : getPlayers()) {
                player.modifyCoins(1);
            }
            getGameBoard().modifyCoins(21 - players.size());
        }
    }

    /**
     * Copy constructor
     *
     * @param copy game to copy
     */
    public Game(@NotNull Game copy) {
        this.expertMode = copy.expertMode;
        this.gameBoard = new Board(copy.gameBoard);
        this.players = copy.getPlayers().stream().map(Player::new).collect(Collectors.toList());
        this.currentTurn = copy.getCurrentTurn();
        this.playersQueue = copy.playersQueue;
    }

    /**
     * Get the weakest card
     *
     * @param playedCards cards played
     * @return the weakest card
     */
    private static int minCard(@NotNull List<AssistCard> playedCards) {
        int pos = -1;
        int min = 11;

        for (int i = 0; i < playedCards.size(); i++) {
            AssistCard card = playedCards.get(i);
            if (card.getPower() < min) {
                min = card.getPower();
                pos = i;
            }
        }

        return pos;
    }

    /**
     * Fixes references to send the game
     *
     * @param game the game to be sent
     * @deprecated
     */
    @Deprecated
    public static void fixReferences(@NotNull Game game) {
        game.fixReferences();
    }

    /**
     * Calculates the queue
     *
     * @param playedCards all the cards played
     */
    public void calculateQueue(List<AssistCard> playedCards) {
        List<AssistCard> played = new ArrayList<>(playedCards); // avoid problems with immutable lists
        ArrayList<Player> newQueue = new ArrayList<>();

        for (int i = 0; i < players.size(); i++) {
            int pos = minCard(played);
            newQueue.add(this.playersQueue.get(pos));
            played.remove(pos);
            this.playersQueue.remove(pos);
        }
        this.playersQueue = newQueue;
    }

    /**
     * Get the queue
     *
     * @return the queue
     */
    public List<Player> getQueue() {
        return this.playersQueue;
    }

    /**
     * Get a player by his ID
     *
     * @param id the ID
     * @return the player
     */
    public Player getPlayerById(int id) {
        return players
                .stream().filter(player -> player.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Give all the students on a cloud to a player
     *
     * @param c the cloud
     * @param p the player
     */
    public void giveCloudToPlayer(@NotNull Cloud c, Player p) {
        c.getStudents().forEach(st -> p.getBoard().addStudent(st));

        c.getStudents().clear();
    }


    /**
     * Calls the wright method
     *
     * @param cid cloud ID
     * @param p   player
     */
    public void giveCloudToPlayer(int cid, Player p) {
        this.giveCloudToPlayer(this.getGameBoard().getCloud(cid), p);
    }

    /**
     * get all the players in the game
     *
     * @return all the players
     */
    public List<Player> getPlayers() {
        return this.players;
    }

    /**
     * Get the game's mode
     *
     * @return the game's mode
     */
    public boolean getMode() {
        return this.expertMode;
    }

    /**
     * Get the game's board
     *
     * @return the game's board
     */
    public Board getGameBoard() {
        return this.gameBoard;
    }

    /**
     * Only for tests
     *
     * @param board the board
     */
    public void setGameBoard(Board board) {
        this.gameBoard = board;
    }

    /**
     * Get current player's board
     *
     * @return current player's board
     */
    public PlayerBoard getCurrentPlayerBoard() {
        return this.currentTurn.getBoard();
    }

    /**
     * Get current player
     *
     * @return current player
     */
    public Player getCurrentTurn() {
        return this.currentTurn;
    }

    /**
     * Set current player
     */
    public void setCurrentTurn(Player player) {
        this.currentTurn = player;
    }

    /**
     * Get the current island
     *
     * @return the current island
     */
    public Archipelago getCurrentIsland() {
        return gameBoard.getMotherNatureIsland();
    }

    /**
     * Fixes references
     */
    public void fixReferences() {
        // fix players in playerBoards
        for (PlayerBoard playerBoard : this.gameBoard.getPlayerBoards()) {
            //noinspection OptionalGetWithoutIsPresent
            Player assPlayer = this.getPlayers().stream().filter(pl -> pl.getId() == playerBoard.getOwner().getId()).findFirst().get();
            playerBoard.setPlayerOwner(assPlayer);
            assPlayer.setBoard(playerBoard);
        }

        // fix CardStacks in board
        for (CardStack cs : this.gameBoard.getGraveyards()) {
            //noinspection OptionalGetWithoutIsPresent
            Player assPlayer = this.getPlayers().stream().filter(pl -> pl.getId() == cs.getOwner().getId()).findFirst().get();
            cs.setOwner(assPlayer);
            assPlayer.setGraveyard(cs);
        }

        // fix queue
        if (playersQueue != null)
            playersQueue.replaceAll(player -> getPlayerById(player.getId()));

        if (currentTurn != null) {
            int turnID = currentTurn.getId();
            this.currentTurn = getPlayerById(turnID);
        }
    }

    /**
     * Check the and of the game
     *
     * @return the reason
     */
    public WinNotification checkGameOver() {

        for (PlayerBoard board : getGameBoard().getPlayerBoards()) {
            if (board.getTowers().isEmpty()) {
                return new WinNotification(board.getOwner(), WinningConditions.NO_TOWERS);
            }
        }

        if (getGameBoard().getIslands().size() <= 3) {
            return new WinNotification(getMostInfluentialPlayer(), WinningConditions.TOO_FEW_ISLANDS);
        }

        if (getGameBoard().getPouch().isEmpty()) {
            return new WinNotification(getMostInfluentialPlayer(), WinningConditions.EMPTY_POUCH);
        }

        for (Player player : getPlayers()) {
            if (player.getHand().isEmpty())
                return new WinNotification(getMostInfluentialPlayer(), WinningConditions.EMPTY_HAND);
        }

        return null;
    }

    public boolean getExpertMode() {
        return expertMode;
    }

    /**
     * @return the player with more towers on the board
     */
    private @NotNull Player getMostInfluentialPlayer() {
        return players.stream().min(Comparator.comparingInt(p -> p.getBoard().getTowers().size())).get();
    }

    public enum WinningConditions {
        TOO_FEW_ISLANDS,
        EMPTY_POUCH,
        EMPTY_HAND,
        NO_TOWERS
    }

    public record WinNotification(Player winner, WinningConditions condition) {
    }
}
