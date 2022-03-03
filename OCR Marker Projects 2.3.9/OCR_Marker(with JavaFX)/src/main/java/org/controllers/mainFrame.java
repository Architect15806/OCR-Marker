package org.controllers;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class mainFrame extends Application {

    public static Stage mainStage;
    public static Scene primaryScene;
    public static Scene markScene;

    public static final String primaryScenePath = "/FXController/PrimarySceneController.fxml";
    public static final String MarkScenePath = "/FXController/MarkSceneController.fxml";

    public static final String avilableAccount = "Architect";
    public static final String password = "123456";

    public static void main( String[] args ){
        Application.launch(args);// 启动软件
    }

    @Override
    public void start(Stage stage) throws Exception {
        mainFrame.mainStage = stage;
        mainFrame.mainStage.setTitle("Marker 01");

        Parent root = FXMLLoader.load(getClass().getResource(primaryScenePath));
        Scene scene = new Scene(root);
        mainFrame.primaryScene = scene;

        root = FXMLLoader.load(getClass().getResource(MarkScenePath));
        scene = new Scene(root);
        mainFrame.markScene = scene;
        mainFrame.mainStage.setScene(mainFrame.primaryScene);
        mainFrame.mainStage.setMaximized(false);
        mainFrame.mainStage.setResizable(false);
        mainFrame.mainStage.show();
    }
}

