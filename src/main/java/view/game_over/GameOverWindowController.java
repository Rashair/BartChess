package view.game_over;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GameOverWindowController {
    @FXML
    private Label messageLabel;

    public void setMessageLabel(String text) {
        messageLabel.setText(text);
    }
}
