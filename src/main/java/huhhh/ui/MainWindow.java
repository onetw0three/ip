package huhhh.ui;

import java.util.Objects;

import huhhh.Huhhh;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Huhhh huhhh;

    private final Image userImage = new Image(
            Objects.requireNonNull(this.getClass().getResourceAsStream("/images/DaUser.png")));
    private final Image huhhhImage = new Image(
            Objects.requireNonNull(this.getClass().getResourceAsStream("/images/DaHuhhh.png")));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Huhhh instance */
    public void setHuhhh(Huhhh h) {
        huhhh = h;
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Huhh's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = huhhh.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getDukeDialog(response, huhhhImage)
        );
        userInput.clear();

        if (huhhh.isExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            Platform.exit();
        }
    }
}
