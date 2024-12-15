package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.Support;
import it.polimi.ingsw.model.Archipelago;
import it.polimi.ingsw.model.SPColor;
import it.polimi.ingsw.model.TowerColor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class IslandController implements Initializable {

    int id;
    private Archipelago island;
    @FXML
    private AnchorPane anchorMain;
    @FXML
    private GridPane mainGP;

    /**
     * creates the new window and displays it
     *
     * @param island island of the window
     * @param islnum island id
     * @return islandController
     */
    public static IslandController createAndDisplay(Archipelago island, int islnum) {
        FXMLLoader loader = new FXMLLoader(IslandController.class.getResource("/fxml/island.fxml"));
        Parent parent;

        try {
            parent = loader.load();
        } catch (IOException exc) {
            throw new RuntimeException("cannot load window");
        }

        IslandController controller = loader.getController();
        controller.setIsland(island, islnum);

        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Island #%d".formatted(islnum));


        stage.show();

        return controller;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // cose da fare dopo che i campi @FXML sono stati popolati, ma PRIMA di avere ottenuto l'isola
        //anchorMain.setStyle("-fx-background-image: url('/img/Reame/PNG/Isola%d.png')".formatted(this.id % 3));
        mainGP.setGridLinesVisible(true);

    }

    /**
     * draws island window
     */
    private void draw() {
        // cose da fare dopo avere ottenuto l'isola (OCIO CHE NON HAI LE DIMENSIONI EFFETTIVE DELLE COSE, SOLO le pref)
        int count = 0;
        mainGP.getChildren().clear();

        BackgroundSize backgroundSize = new BackgroundSize(1, 1, true, true, false, false);
        BackgroundImage backgroundImage = new BackgroundImage(GUIResourcesManager.getSingleton()
                .getBigIslandImage(Math.abs(Support.simpleHash(GUIResourcesManager.getSingleton().getRuntimeRandomSeed(), id)) % 3 + 1),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, backgroundSize);

        anchorMain.setBackground(new Background(backgroundImage));

        for (SPColor value : SPColor.values()) {


            Image image = GUIResourcesManager.getSingleton().getStudentImage(value);
            ImageView studentColor = new ImageView(image);

            GridPane.setHalignment(studentColor, HPos.CENTER);
            GridPane.setValignment(studentColor, VPos.BOTTOM);

            studentColor.setFitHeight(40);
            studentColor.setFitWidth(40);
            studentColor.setEffect(new DropShadow(30, Color.WHITE));
            mainGP.add(studentColor, count, 0);

            Label countLabel = new Label(String.valueOf(island.getStudentsByColor(value)));
            countLabel.setMinWidth(20);
            countLabel.setMinHeight(20);
            countLabel.setPadding(new Insets(0, 0, 5, 3));
            countLabel.setFont(new Font(20));

            GridPane.setHalignment(countLabel, HPos.CENTER);
            GridPane.setValignment(countLabel, VPos.BOTTOM);

            mainGP.add(countLabel, count, 0);

            count++;
        }

        if (island.getOwner() != null) {
            Image towerImage = GUIResourcesManager.getSingleton().getTowerImage(island.getOwner());
            ImageView towerIV = new ImageView(towerImage);
            towerIV.setFitWidth(50);
            towerIV.setPreserveRatio(true);

            Label towerNumber = new Label(String.valueOf(island.getNTowers()));
            towerNumber.setMinWidth(20);
            towerNumber.setMinHeight(20);
            towerNumber.setPadding(new Insets(0, 0, 5, 3));
            towerNumber.setFont(new Font(20));
            if (island.getOwner() == TowerColor.BLACK) towerNumber.setTextFill(Color.WHITE);

            towerNumber.setPadding(new Insets(0, 0, 10, 0));

            GridPane.setHalignment(towerIV, HPos.CENTER);
            GridPane.setValignment(towerIV, VPos.BOTTOM);

            GridPane.setHalignment(towerNumber, HPos.CENTER);
            GridPane.setValignment(towerNumber, VPos.BOTTOM);
            towerIV.setEffect(new DropShadow(30, Color.WHITE));
            mainGP.add(towerIV, 1, 1);
            mainGP.add(towerNumber, 1, 1);


        }
        if (island.getMotherNature()) {
            Image motherNature = GUIResourcesManager.getSingleton().getMotherNatureImage();
            ImageView motherNatureIV = new ImageView(motherNature);
            motherNatureIV.setFitWidth(50);
            motherNatureIV.setPreserveRatio(true);

            mainGP.add(motherNatureIV, 3, 1);
        }
    }

    /**
     * gets stage
     *
     * @return stage
     */
    private Stage getStage() {
        if (anchorMain.getScene() == null) return null;

        return (Stage) anchorMain.getScene().getWindow();
    }

    public void setIsland(Archipelago newIsland, int id) {
        island = newIsland;
        this.id = id;
        draw();
    }
}
