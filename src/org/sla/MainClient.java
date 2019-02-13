package org.sla;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainClient extends Application {
    private Controller myController;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample1.fxml"));
        Parent root = loader.load();

        Thread.currentThread().setName("MainServer GUI Thread");

        primaryStage.setTitle("GUI");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        myController = loader.getController();
        myController.setClientMode();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
