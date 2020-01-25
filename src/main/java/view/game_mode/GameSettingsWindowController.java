package view.game_mode;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import model.Colour;
import model.players.GameMode;

import java.io.IOException;
import java.util.Objects;

public class GameSettingsWindowController {
    private GameMode gameMode = GameMode.PLAYER_COMPUTER;
    private Colour playerColour = Colour.White;

    public GameMode getGameMode() {
        return gameMode;
    }

    public Colour getPlayerColour() {
        return playerColour;
    }

    public void onPlayerPlayerButtonClick(ActionEvent actionEvent) {
        gameMode = GameMode.PLAYER_PLAYER;
        closeWindow(actionEvent);
    }

    public void onPlayerComputerButtonClick(ActionEvent event) {
        gameMode = GameMode.PLAYER_COMPUTER;
        chooseColour(event);
    }

    private void chooseColour(ActionEvent ev) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("./playerColourView.fxml")));
            loader.setController(this);
            Parent root = loader.load();

            Node source = (Node) ev.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.setTitle("Player colour");
            stage.getScene().setRoot(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void onPlayerWhiteButtonClick(ActionEvent actionEvent) {
        playerColour = Colour.White;
        closeWindow(actionEvent);
    }

    public void onPlayerBlackButtonClick(ActionEvent actionEvent) {
        playerColour = Colour.Black;
        closeWindow(actionEvent);
    }

    public void onComputerComputerButtonClick(ActionEvent actionEvent) {
        gameMode = GameMode.COMPUTER_COMPUTER;
        closeWindow(actionEvent);
    }

    private void closeWindow(Event e) {
        Node source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
