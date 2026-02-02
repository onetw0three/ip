package huhhh.ui;

import java.io.IOException;
import java.util.Objects;

import huhhh.Huhhh;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The GUI layer for the Huhhh JavaFX application.
 */
public class GraphicUi extends Application {
    private final Huhhh huhhh = new Huhhh();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Huhhh.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            scene.getStylesheets().add(Objects.requireNonNull(Huhhh.class.getResource("/css/main.css")).toExternalForm());
            stage.setTitle("Huhhh");
            stage.setScene(scene);
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            fxmlLoader.<MainWindow>getController().setHuhhh(huhhh);
            stage.show();
        } catch (IOException e) {
            Logger.showError(e.getMessage());
        }
    }
}
