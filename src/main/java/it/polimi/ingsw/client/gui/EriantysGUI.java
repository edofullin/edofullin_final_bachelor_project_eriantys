package it.polimi.ingsw.client.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@SuppressWarnings("ConstantConditions")
public class EriantysGUI extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));

        Scene scene = new Scene(root);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setOnCloseRequest((we) -> {
            System.exit(0);
        });
        stage.show();

    }

}