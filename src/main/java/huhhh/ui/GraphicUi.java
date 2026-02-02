package huhhh.ui;

import java.io.IOException;

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
            stage.setTitle("Huhhh");
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setHuhhh(huhhh);
            stage.show();
        } catch (IOException e) {
            Logger.showError(e.getMessage());
        }
    }
}
