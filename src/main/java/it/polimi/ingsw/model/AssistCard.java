package it.polimi.ingsw.model;

public enum AssistCard {
    ASSISTANT_0("Lion", 1, 1),
    ASSISTANT_1("Goose", 2, 1),
    ASSISTANT_2("Cat", 3, 2),
    ASSISTANT_3("Eagle", 4, 2),
    ASSISTANT_4("Fox", 5, 3),
    ASSISTANT_5("Snake", 6, 3),
    ASSISTANT_6("Hippopotamus", 7, 4),
    ASSISTANT_7("Dog", 8, 4),
    ASSISTANT_8("Elephant", 9, 5),
    ASSISTANT_9("Turtle", 10, 5);

    final private String animal;
    final private int power;
    final private int moves;

    AssistCard(String animal, int power, int moves) {
        this.animal = animal;
        this.power = power;
        this.moves = moves;
    }

    /**
     * Get the animal
     *
     * @return the animal
     */
    public String getAnimal() {
        return animal;
    }

    /**
     * Get the power
     *
     * @return the power
     */
    public int getPower() {
        return power;
    }

    /**
     * Get the moves
     *
     * @return the moves
     */
    public int getMoves() {
        return moves;
    }
}
