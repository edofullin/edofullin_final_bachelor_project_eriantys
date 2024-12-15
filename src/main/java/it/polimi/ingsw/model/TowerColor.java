package it.polimi.ingsw.model;


import javafx.scene.paint.Color;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum TowerColor {
    WHITE,
    BLACK,
    GRAY;

    @Contract(pure = true)
    public @NotNull String toHexColor() {
        switch (this) {
            case BLACK -> {
                return "#2d2d2e";
            }
            case GRAY -> {
                return "#757575";
            }
            case WHITE -> {
                return "#ffffff";
            }
        }

        throw new RuntimeException();
    }

    public Color toColor() {
        switch (this) {
            case BLACK -> {
                return Color.BLACK;
            }
            case GRAY -> {
                return Color.GRAY;
            }
            case WHITE -> {
                return Color.WHITE;
            }
        }

        throw new RuntimeException();
    }
}
