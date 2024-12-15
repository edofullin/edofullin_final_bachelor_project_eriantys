package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.Support;
import it.polimi.ingsw.client.GameManager;
import it.polimi.ingsw.client.InvalidLoginException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.GameSettings;
import it.polimi.ingsw.server.Configuration;
import it.polimi.ingsw.server.Server;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Login view
 */
public class LoginScreenController implements Initializable {

    @FXML
    private TextField txtIPAddress;

    @FXML
    private TextField txtNickName;

    @FXML
    private ComboBox<Player.Magician> cmbMagicians;

    @FXML
    private CheckBox chkBroadcaster;

    private Thread serverThread;

    private GameManager gameManager = null;

    public void initialize(URL url, ResourceBundle bundle) {
        cmbMagicians.setItems(FXCollections.observableArrayList(Player.Magician.values()));
        cmbMagicians.setValue(Player.Magician.MARIN);

        if (Configuration.getConfiguration().isClientBroadcasterDefault()) {
            chkBroadcaster.setSelected(true);
            broadcasterChanged();
        }
    }

    /**
     * start server  button handler
     */
    public void startServerClicked() {
        new Thread(() -> {
            Server server = new Server();
            server.startLobbyManager();

            Platform.runLater(() -> {
                Support.JFXAlertShowWait(Alert.AlertType.INFORMATION, "Server started", "Server started");
            });

            server.waitLobbyManager();
        }).start();
    }

    public void broadcasterChanged() {

        if (chkBroadcaster.isSelected()) {

            if (gameManager == null) {
                gameManager = new GameManager();
            }

            gameManager.runBroadcaster((addr) -> {
                txtIPAddress.setText(addr);
                gameManager.stopBroadcaster();
            });


        } else {
            if (gameManager != null) gameManager.stopBroadcaster();
        }


    }

    /**
     * start game button event handler handler
     */
    @FXML
    private void startGame() {

        List<GameSettings> games;

        if (gameManager == null)
            gameManager = new GameManager(txtIPAddress.getText());

        gameManager.changeEndpoint(txtIPAddress.getText());

        try {
            games = gameManager.getAvailableGames();
        } catch (Exception exc) {
            Support.JFXAlertShowWait(Alert.AlertType.ERROR, "Connection Failed", "Could not connect to server");
            return;
        }

        if (games == null) {
            Support.JFXAlertShowWait(Alert.AlertType.ERROR, "Connection Failed", "There was an error retreving the list of games, try again later");
            return;
        }

        JoinCreateGameController jcgc = JoinCreateGameController.createAndDisplayAndWait(games);

        if (jcgc.isCanceled()) return;

        int joinPN = 0;
        GameSettings gameSettings;

        if (jcgc.isCreateGame() || jcgc.fromDisk()) {
            gameSettings = jcgc.getCreateGameSettings();
            try {
                joinPN = gameManager.createNewGame(gameSettings); // potrebbe andare a fuoco
            } catch (Exception exc) {
                Support.JFXAlertShowWait(Alert.AlertType.ERROR, "Game Create Failed", exc.getMessage());
                return;
            }
        } else {
            gameSettings = jcgc.getGameToJoin();
            joinPN = gameSettings.getPort();
        }

        try {
            gameManager.reconnectToGame(txtNickName.getText(), cmbMagicians.getValue(), joinPN);
        } catch (InvalidLoginException invalidLoginException) {
            Support.JFXAlertShowWait(Alert.AlertType.ERROR, "Join Failed", invalidLoginException.getErrorCode().toString());
            return;
        } catch (Exception exc) {
            Support.JFXAlertShowWait(Alert.AlertType.ERROR, "Join Failed", exc.getMessage());
            return;
        }

        gameManager.stopBroadcaster();

        String fxmlFile = gameSettings.getLobbySize() == 2 ? "/fxml/mainWindow.fxml" : "/fxml/mainWindow3Player.fxml";

        FXMLLoader loader = new FXMLLoader(JoinCreateGameController.class.getResource(fxmlFile));

        Parent parent;

        try {
            parent = loader.load();
        } catch (Exception e) {
            Support.JFXAlertShowWait(Alert.AlertType.ERROR, "Could not open main window", e.getMessage());
            return;
        }

        MainWindowController controller = loader.getController();
        controller.setGameManager(gameManager);

        Stage stage = new Stage();
        Scene scene = new Scene(parent, 1280, 720);

        stage.setOnShown((we) -> controller.shown());
        stage.setTitle("Eriantys - " + gameSettings.getLobbyName());
        stage.setScene(scene);
        stage.setOnCloseRequest(we -> {
            if (serverThread != null) serverThread.interrupt();
            controller.disconnect();
            Platform.exit();
            System.exit(0);
        });
        stage.show();

        ((Stage) txtIPAddress.getScene().getWindow()).close();
    }


}
