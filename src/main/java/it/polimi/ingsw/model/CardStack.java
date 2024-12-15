package it.polimi.ingsw.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CardStack {

    final private List<AssistCard> cards;
    private Player owner;

    public CardStack(@NotNull Player p) {
        this.cards = new ArrayList<>();
        this.owner = p;
        p.setGraveyard(this);
    }

    public CardStack(@NotNull CardStack ccs) {
        this.cards = new ArrayList<>(ccs.cards);
        this.owner = new Player(ccs.owner);
        owner.setGraveyard(this);
    }

    /**
     * Get all the cards
     *
     * @return all the cards
     */
    public List<AssistCard> getCards() {
        return cards;
    }

    /**
     * Get top card
     *
     * @return the top card
     */
    public AssistCard getTopCard() {
        return cards.size() == 0 ? null : cards.get(cards.size() - 1);
    }

    /**
     * Adds a card
     *
     * @param card the card to be added
     */
    public void addCard(AssistCard card) {
        this.cards.add(card);
    }

    /**
     * Check if a card is present
     *
     * @param c the card to check
     * @return boolean representing if it is present or not
     */
    public boolean isPresent(AssistCard c) {
        return cards.contains(c);
    }

    /**
     * Get the "owner"
     *
     * @return the "owner"
     */
    public Player getOwner() {
        return this.owner;
    }

    /**
     * Set the owner
     *
     * @param owner the owner
     */
    public void setOwner(Player owner) {
        this.owner = owner;
    }
}
