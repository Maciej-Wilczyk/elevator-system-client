

import controller.WindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.springframework.http.converter.json.GsonBuilderUtils;
import rest.Rest;
import rest.RestImpl;

import java.util.Optional;

public class Main extends Application {
    Stage window;
    private Rest rest;

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 900;

    @Override
    public void start(Stage stage) throws Exception {

        window = stage;
        window.setTitle("Elevator System");
        window.setTitle("Elevator System");
        window.setOnCloseRequest(e -> close());
        Parent root = FXMLLoader.load(getClass().getResource("fxml/window.fxml"));
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        scene.getStylesheets().add(getClass().getResource("css/window.css").toExternalForm());
        window.setScene(scene);
        window.show();
    }


    public void close() {
        WindowController.realTimeFlag = false;
        rest = new RestImpl();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("");
        alert.setHeaderText("Do you want to save the state of the elevators?");
        ButtonType yesB = new ButtonType("Yes");
        ButtonType noB = new ButtonType("No");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yesB, noB,buttonTypeCancel);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == yesB) {
            rest.save(true,(r) ->{});
            window.close();
        } else if (result.get() == noB) {
            rest.save(false,(r) ->{});
            window.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }


}
