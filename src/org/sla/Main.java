package org.sla;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private Controller myController;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        myController = loader.getController();
        myController.setStage(primaryStage);

        Thread.currentThread().setName("Main GUI Thread");

        primaryStage.setTitle("GUI");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

//cantcommitforsomereason
    public static void main(String[] args) {
        launch(args);
    }
}
