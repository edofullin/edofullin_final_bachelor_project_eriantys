package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.network.GameSettings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 *
 */
public class JoinCreateGameController implements Initializable {

    boolean isCanceled = false;
    ObservableList<GameSettings> games;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private ComboBox<GameSettings> cmbGames;
    @FXML
    private ComboBox<GameSettings> cmbDiskGames;
    @FXML
    private RadioButton rbt2players;
    @FXML
    private RadioButton rbtCreate;
    @FXML
    private TextField txtLobbyName;
    @FXML
    private CheckBox chkExpert;
    @FXML
    private CheckBox chkSaveDisk;
    @FXML
    private RadioButton rbtDiskJoin;
    @FXML
    private RadioButton rbt3players;
    @FXML
    private RadioButton rbtJoin;

    /**
     * loads the join/ create game dialog
     *
     * @param games is a list of gameSettings wich represents the available lobbies
     * @return JoinCreateGameController
     */
    public static JoinCreateGameController createAndDisplayAndWait(List<GameSettings> games) {
        FXMLLoader loader = new FXMLLoader(JoinCreateGameController.class.getResource("/fxml/joincreate_game_dialog.fxml"));
        Parent parent;

        try {
            parent = loader.load();
        } catch (Exception exc) {
            // ignored
            throw new RuntimeException(exc);
        }

        JoinCreateGameController controller = loader.getController();
        controller.setGamesList(FXCollections.observableList(games));

        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setTitle("Select game to join");
        stage.showAndWait();

        return controller;
    }

    /**
     * button OK event handler
     */
    public void buttonOKClicked() {
        Stage stage = (Stage) cmbGames.getScene().getWindow();
        stage.close();
    }

    /**
     * button Cancel event handler
     */
    public void buttonCancelClicked() {
        buttonOKClicked();
        isCanceled = true;
    }

    public void setGamesList(ObservableList<GameSettings> games) {
        this.games = games;

        cmbGames.setItems(games.filtered(g -> !g.isFromDisk()));
        cmbDiskGames.setItems(games.filtered(g -> g.isFromDisk()));

        if (cmbGames.getItems().size() > 0) {
            cmbGames.getSelectionModel().select(0);
        } else {
            rbtJoin.setDisable(true);
            rbtDiskJoin.setSelected(true);
        }

        if (cmbDiskGames.getItems().size() > 0) {
            cmbDiskGames.getSelectionModel().select(0);
        } else {
            rbtDiskJoin.setDisable(true);

            if (cmbGames.getItems().size() == 0)
                rbtCreate.setSelected(true);
            else
                rbtJoin.setSelected(true);
        }
    }

    /**
     * true if user wants to create game
     */
    public boolean isCreateGame() {
        return rbtCreate.isSelected();
    }

    public GameSettings getCreateGameSettings() {
        if (fromDisk()) {
            return cmbDiskGames.getSelectionModel().getSelectedItem();
        }

        return new GameSettings(txtLobbyName.getText(), rbt2players.isSelected() ? 2 : 3, -1, chkExpert.isSelected(), chkSaveDisk.isSelected());
    }

    /**
     * true if  user wants to cancel
     */
    public boolean isCanceled() {
        return isCanceled;
    }

    public GameSettings getGameToJoin() {
        if (rbtDiskJoin.isSelected()) {
            return cmbDiskGames.getSelectionModel().getSelectedItem();
        } else {
            return cmbGames.getSelectionModel().getSelectedItem();
        }
    }

    /**
     * @return true if user wants to select game from disk
     */
    public boolean fromDisk() {
        return rbtDiskJoin.isSelected();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmbDiskGames.disableProperty().bind(rbtDiskJoin.selectedProperty().not());
        cmbGames.disableProperty().bind(rbtJoin.selectedProperty().not());

        txtLobbyName.disableProperty().bind(rbtCreate.selectedProperty().not());
        rbt2players.disableProperty().bind(rbtCreate.selectedProperty().not());
        rbt3players.disableProperty().bind(rbtCreate.selectedProperty().not());
        chkExpert.disableProperty().bind(rbtCreate.selectedProperty().not());
        chkSaveDisk.disableProperty().bind(rbtCreate.selectedProperty().not());
    }
}
