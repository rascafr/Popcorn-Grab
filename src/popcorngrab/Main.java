package popcorngrab;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by Rascafr, 06/08/2016
 * JavaFX main entry class, use with precaution
 *
 * Build :
 * - Build
 * - Build Artifacts
 * - Popcorn-Grab:jar
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("grabui.fxml"));
        primaryStage.setTitle("Popcorn-Grab");
        primaryStage.setScene(new Scene(root, 570, 315));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
