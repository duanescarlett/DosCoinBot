package core;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private static FXMLLoader root;
    public static Stage window;
    private static Parent pane;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.TheFXMLLoader("ui/gui.fxml"); // Load FXML file
        Controller controller = root.getController(); // Give control to the controller
        controller.setMain(this); // Initialize the main class var with Main obj

        this.window = primaryStage;
        this.window.setTitle("DosCoinBot");
        this.window.setOnCloseRequest(e -> {
            e.consume();
            //this.closeProgram();
        });

        Scene s = new Scene(this.pane);

        this.window.setScene(s);
        this.window.show();
    }

    private void TheFXMLLoader(String s) {
        this.root = new FXMLLoader(Main.class.getResource(s));
        try {
            pane = root.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
