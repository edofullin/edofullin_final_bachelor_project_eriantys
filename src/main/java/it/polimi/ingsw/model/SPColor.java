package it.polimi.ingsw.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum SPColor {
    GREEN,
    RED,
    YELLOW,
    PINK,
    BLUE;

    @Contract(pure = true)
    public @NotNull String toHexColor() {
        switch (this) {
            case RED -> {
                return "#db5365";
            }
            case GREEN -> {
                return "#97db53";
            }
            case YELLOW -> {
                return "#f0e15b";
            }
            case PINK -> {
                return "#faa7ee";
            }
            case BLUE -> {
                return "#7496ed";
            }
        }

        throw new RuntimeException();
    }
}
