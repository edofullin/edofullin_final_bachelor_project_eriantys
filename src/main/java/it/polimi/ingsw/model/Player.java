package it.polimi.ingsw.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Player {

    final private String name;
    final private Magician mage;
    final private TowerColor playerTowersColor;
    final private List<AssistCard> hand;
    private int id;
    private transient CardStack graveyard = null;
    private transient PlayerBoard board = null;
    private int coins;

    public Player(String name, int id, TowerColor tc, Magician mage) {
        this.name = name;
        this.id = id;
        this.mage = mage;
        this.playerTowersColor = tc;
        hand = new ArrayList<>();
        hand.addAll(Arrays.asList(AssistCard.values()));
        coins = 0;
    }

    public Player(@NotNull Player cp) {
        this.name = cp.name;
        this.id = cp.id;
        this.mage = cp.mage;
        this.playerTowersColor = cp.playerTowersColor;
        hand = new ArrayList<>(cp.getHand());
        coins = cp.coins;
    }

    /**
     * Get player's name
     *
     * @return player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Get player's id
     *
     * @return player's id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets player id
     *
     * @param id id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get player's mage
     *
     * @return player's mage
     */
    public Magician getMagician() {
        return this.mage;
    }

    /**
     * Get player's TowerColor
     *
     * @return player's TowerColor
     */
    public TowerColor getPlayerTowersColor() {
        return playerTowersColor;
    }

    /**
     * Get player's hand
     *
     * @return player's hand
     */
    public List<AssistCard> getHand() {
        return hand;
    }

    /**
     * Remove a card from hand
     *
     * @param assistCard the card to be removed
     */
    public void removeCard(AssistCard assistCard) {
        hand.remove(assistCard);
    }

    /**
     * Get player's graveyard
     *
     * @return player's graveyard
     */
    public CardStack getGraveyard() {
        return graveyard;
    }

    /**
     * Set the graveyard when created
     *
     * @param graveyard the graveyard
     */
    public void setGraveyard(CardStack graveyard) {
        this.graveyard = graveyard;
    }

    /**
     * Get player's board
     *
     * @return player's board
     */
    public PlayerBoard getBoard() {
        return board;
    }

    /**
     * Set the player's board when created
     *
     * @param pb the player's board
     */
    public void setBoard(PlayerBoard pb) {
        this.board = pb;
    }

    /**
     * Get player's coins
     *
     * @return player's coins
     */
    public int getCoins() {
        return coins;
    }

    /**
     * Modify coins' value
     *
     * @param value how much coins changes
     */
    public void modifyCoins(int value) {
        this.coins += value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public enum Magician {
        MARIN,
        XERXES,
        RIAS,
        ODIN
    }

}
