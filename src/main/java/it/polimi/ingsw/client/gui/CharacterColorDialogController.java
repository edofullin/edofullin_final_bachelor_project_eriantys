package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.model.IStudentContainer;
import it.polimi.ingsw.model.SPColor;
import it.polimi.ingsw.model.characters.CharacterCard;
import it.polimi.ingsw.model.characters.HerbalistCharacter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

public class CharacterColorDialogController {


    @FXML
    Label lblTitle;
    @FXML
    AnchorPane mainAnchor;
    @FXML
    VBox vbColorSelect;
    @FXML
    ImageView imgCharacter;
    @FXML
    GridPane gpModifiers;
    @FXML
    GridPane gpMain;
    @FXML
    Label textDescription;
    @FXML
    Label lblEffectExplain;
    SPColor selectedColor;
    int characterModifiers = 0;
    private CharacterCard card;
    private List<SPColor> enabledColors;
    private String title;

    public static SPColor createAndDisplayAndWait(String title, List<SPColor> enabledColors, CharacterCard card) throws IOException {
        FXMLLoader loader = new FXMLLoader(CharacterColorDialogController.class.getResource("/fxml/colorSelectorDialog.fxml"));
        Parent parent = loader.load();

        CharacterColorDialogController controller = loader.getController();
        controller.setTitle(title);
        controller.setEnabledColors(enabledColors);
        controller.setCharacter(card);


        Scene scene = new Scene(parent);
        Stage stage = new Stage();

        stage.setOnShown(e -> controller.shown());

        stage.initModality(Modality.NONE);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.setResizable(false);
        stage.showAndWait();

        return controller.getSelectedColor();
    }

    public void setEnabledColors(List<SPColor> enabledColors) {
        this.enabledColors = enabledColors;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCharacter(CharacterCard card) {
        this.card = card;
    }

    /**
     * gets stage
     *
     * @return stage
     */
    public Stage getStage() {
        if (lblTitle.getScene() == null) return null;

        return (Stage) lblTitle.getScene().getWindow();
    }

    /**
     * loads students, description text and other card's properties
     */
    public void shown() {

        if (enabledColors != null) {
            for (SPColor enabledColor : enabledColors) {

                Image colorImage = GUIResourcesManager.getSingleton().getStudentImage(enabledColor);
                ImageView colorIV = new ImageView(colorImage);
                colorIV.setFitHeight(40);
                colorIV.setFitWidth(40);
                colorIV.setId(enabledColor.toString());
                colorIV.setPickOnBounds(true);
                colorIV.setOnMouseClicked(this::btnColorClicked);

                vbColorSelect.getChildren().add(colorIV);
            }
        }

        lblTitle.setText(title);

        if (card == null) return;
        textDescription.setText(card.getEnumType().getDescription());
        textDescription.setWrapText(true);
        lblEffectExplain.setText("%s effect".formatted(card.getEnumType().toString()));

        imgCharacter.setImage(GUIResourcesManager.getSingleton().getCharacterImage(card.getEnumType()));

        if (card instanceof HerbalistCharacter hc) {
            putImageOnCard(GUIResourcesManager.getSingleton().getBanImage(), hc.getBans());
        }

        if (card.isUsed()) {
            putImageOnCard(GUIResourcesManager.getSingleton().getCoinImage(), null);
        }

        // dopo mesi dalla definizione di questa interfaccia e dalla bestemmie che ha portato con se finalmente è stata utile a qualcosa!
        if (card instanceof IStudentContainer sc) {
            for (SPColor value : SPColor.values()) {
                Image colorImage = GUIResourcesManager.getSingleton().getStudentImage(value);
                putImageOnCard(colorImage, (int) sc.getStudents().stream().filter(c -> c == value).count());
            }
        }


    }

    /**
     * creates a stackpane,inserts an images and eventually put a label on top if necessary
     *
     * @param image  is the picture to add (e.g. student / ban ...)
     * @param number is the  number to display on top of the image
     */
    @SuppressWarnings("DuplicatedCode")
    private void putImageOnCard(@NotNull Image image, @Nullable Integer number) {
        StackPane stackPane = new StackPane();

        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(40);
        imageView.setFitWidth(40);

        stackPane.getChildren().add(imageView);

        if (number != null) {
            Label countLabel = new Label(String.valueOf(number));
            countLabel.toFront();
            countLabel.setTextFill(Color.WHITE);
            //countLabel.setPadding(new Insets(0, 0, 5, 3));
            countLabel.setFont(new Font(25));
            StackPane.setAlignment(countLabel, Pos.CENTER);

            stackPane.getChildren().add(countLabel);
        }

        gpModifiers.add(stackPane, characterModifiers % 5, characterModifiers / 5);
        characterModifiers++;

    }

    /**
     * event handler for click on a character's element
     *
     * @param e mouse event
     */
    public void btnColorClicked(MouseEvent e) {
        Node sender = (Node) e.getSource();
        selectedColor = SPColor.valueOf(sender.getId());
        getStage().close();
    }

    /**
     * closes character pop up window
     */
    public void close() {
        getStage().close();
    }

    public SPColor getSelectedColor() {
        return selectedColor;
    }


}
