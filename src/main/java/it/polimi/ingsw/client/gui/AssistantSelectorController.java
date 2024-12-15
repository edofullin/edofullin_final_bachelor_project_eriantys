package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.model.AssistCard;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AssistantSelectorController implements Initializable {

    private ObservableList<AssistCard> cards;

    @FXML
    private HBox cardsBox;

    private AssistCard selectedCard;

    private boolean clickEnabled = true;

    /**
     * creates and displays new windows then waits for new input
     *
     * @param assistants   list of assistant cards
     * @param clickEnabled true == enabled , false == disabled
     * @param title        window header
     * @return controller of  assistant selector window
     */
    public static AssistantSelectorController createAndDisplayAndWait(List<AssistCard> assistants, boolean clickEnabled, String title) {
        FXMLLoader loader = new FXMLLoader(AssistantSelectorController.class.getResource("/fxml/assistantSelectorDialog.fxml"));
        Parent parent;

        try {
            parent = loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        AssistantSelectorController controller = loader.getController();
        controller.setCards(FXCollections.observableList(assistants));
        controller.setClickEnabled(clickEnabled);

        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.initModality(Modality.NONE);
        stage.setScene(scene);
        stage.setOnShown(e -> controller.shown());
        stage.setResizable(false);
        stage.showAndWait();

        return controller;
    }

    /**
     * @param games
     * @return
     * @throws IOException
     * @deprecated
     */
    public static AssistantSelectorController createAndDisplayAndWait(List<AssistCard> games, String title) {
        return AssistantSelectorController.createAndDisplayAndWait(games, true, title);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


    }

    public void setCards(ObservableList<AssistCard> cards) {
        this.cards = cards;

        drawImages();
    }

    public void setClickEnabled(boolean ce) {
        clickEnabled = ce;
    }

    public AssistCard getSelectedCard() {
        return selectedCard;
    }

    /**
     * draws  cards images
     */
    private void drawImages() {

        cardsBox.getChildren().clear();

        for (AssistCard card : cards) {

            ImageView imageView = new ImageView(GUIResourcesManager.getSingleton().getAssistCardImage(card));

            imageView.setOnMouseClicked((e) -> {
                if (!clickEnabled) return;
                selectedCard = AssistCard.valueOf(((ImageView) e.getSource()).getId());
                getStage().close();
            });

            imageView.setId(card.toString());
            imageView.setFitHeight(230);
            imageView.setPreserveRatio(true);


            cardsBox.getChildren().add(imageView);
        }

    }

    public void shown() {
        Scene scene = cardsBox.getScene();

    }

    private Stage getStage() {
        return (Stage) cardsBox.getScene().getWindow();
    }
}
