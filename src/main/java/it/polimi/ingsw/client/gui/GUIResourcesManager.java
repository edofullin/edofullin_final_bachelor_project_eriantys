package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.model.AssistCard;
import it.polimi.ingsw.model.SPColor;
import it.polimi.ingsw.model.TowerColor;
import it.polimi.ingsw.model.characters.CharacterEnum;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GUIResourcesManager {

    private static GUIResourcesManager singleton;
    private int runtimeRandomSeed = 0;

    public static GUIResourcesManager getSingleton() {
        if (singleton == null) singleton = new GUIResourcesManager();
        return singleton;
    }

    public int getRuntimeRandomSeed() {
        if (runtimeRandomSeed == 0) runtimeRandomSeed = (int) (Math.random() * 100);
        return runtimeRandomSeed;
    }

    /**
     * extract an assistant card's image path given an assistant card instance
     *
     * @param card the assistant card instance
     * @return assistant card image
     */
    public Image getAssistCardImage(@NotNull AssistCard card) {
        String basePath = "/img/Assistenti/2x/";

        switch (card) {
            case ASSISTANT_0 -> {
                basePath += "Assistente (1).png";
            }
            case ASSISTANT_1 -> {
                basePath += "Assistente (2).png";
            }
            case ASSISTANT_2 -> {
                basePath += "Assistente (3).png";
            }
            case ASSISTANT_3 -> {
                basePath += "Assistente (4).png";
            }
            case ASSISTANT_4 -> {
                basePath += "Assistente (5).png";
            }
            case ASSISTANT_5 -> {
                basePath += "Assistente (6).png";
            }
            case ASSISTANT_6 -> {
                basePath += "Assistente (7).png";
            }
            case ASSISTANT_7 -> {
                basePath += "Assistente (8).png";
            }
            case ASSISTANT_8 -> {
                basePath += "Assistente (9).png";
            }
            case ASSISTANT_9 -> {
                basePath += "Assistente (10).png";
            }
        }

        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(basePath)));
    }

    /**
     * returns the image of a student given an SPColor instance
     *
     * @param color SPColor instance
     * @return image
     */
    public Image getStudentImage(SPColor color) {
        StringBuilder sb = new StringBuilder("/img/pedine/studente_");

        switch (color) {
            case RED:
                sb.append("rosso");
                break;
            case BLUE:
                sb.append("blu");
                break;
            case GREEN:
                sb.append("verde");
                break;
            case PINK:
                sb.append("magenta");
                break;
            case YELLOW:
                sb.append("giallo");
                break;
            default:
                throw new RuntimeException("invalid student color");
        }

        sb.append(".png");

        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(sb.toString())));

        return image;
    }

    /**
     * returns the image of a professor given an SPColor instance
     *
     * @param color SPColor instance
     * @return image of the professor
     */

    public Image getProfessorImage(SPColor color) {
        StringBuilder sb = new StringBuilder("/img/pedine/professore_");

        switch (color) {
            case RED:
                sb.append("rosso");
                break;
            case BLUE:
                sb.append("blu");
                break;
            case GREEN:
                sb.append("verde");
                break;
            case PINK:
                sb.append("magenta");
                break;
            case YELLOW:
                sb.append("giallo");
                break;
            default:
                throw new RuntimeException("invalid student color");
        }

        sb.append(".png");

        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(sb.toString())));

        return image;
    }

    /**
     * returns the image of a tower given a TowerColor instance
     *
     * @param color TowerColor instance
     * @return image
     */
    public Image getTowerImage(TowerColor color) {
        // ....
        Image Tower;
        StringBuilder colore = new StringBuilder("/img/pedine/torre_");
        switch (color) {
            case WHITE:
                colore.append("bianca");
                break;
            case BLACK:
                colore.append("nera");
                break;
            case GRAY:
                colore.append("grigia");
                break;
            default:
                throw new RuntimeException("invalid tower color");

        }
        colore.append(".png");
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(colore.toString())));
        return image;
    }

    public Image getCoinImage() {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/coin.png")));
    }

    public Image getBanImage() {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/ban_small.png")));
    }

    public Image getCharacterImage(CharacterEnum card) {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/Personaggi/%s.jpg".formatted(card.toString()))));
    }

    public Image getIslandImage() {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/isoletta.png")));
    }

    public Image getBigIslandImage(int num) {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/Reame/Isola%d.png".formatted(num))));
    }

    public Image getMotherNatureImage() {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/pedine/madre_natura.png")));
    }
}
