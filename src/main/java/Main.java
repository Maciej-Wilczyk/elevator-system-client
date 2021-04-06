

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    Stage window;

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 900;

    @Override
        public void start(Stage stage) throws Exception {
            window = stage;
            window.setTitle("Elevator System");
            window.setTitle("Elevator System");
            Parent root = FXMLLoader.load(getClass().getResource("fxml/window.fxml"));
            Scene scene = new Scene(root,WIDTH,HEIGHT);
            scene.getStylesheets().add(getClass().getResource("css/window.css").toExternalForm());
            window.setScene(scene);
            window.show();
    }

    public static void main(String[] args) {
        launch(args);
    }


}
