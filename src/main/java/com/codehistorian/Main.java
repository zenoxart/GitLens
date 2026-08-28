package com.codehistorian;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private static final int[] ICON_SIZES = {16, 32, 48, 64, 128, 256, 512};

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/main.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add(Main.class.getResource("/css/modern.css").toExternalForm());

        for (int size : ICON_SIZES) {
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/icons/gitlens-" + size + ".png")));
        }

        stage.setTitle("GitLens");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
