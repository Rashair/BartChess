package view.menu;

import controller.BoardController;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.Colour;
import model.GameModel;
import model.players.GameMode;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import view.board.BoardView;
import view.game_mode.GameSettingsWindowController;

import java.io.IOException;
import java.util.Objects;

public class MenuViewController {
    public void onNewGameButtonClick(ActionEvent actionEvent) {
        var stage = getStage(actionEvent);
        var settings = getGameSettings();
        if (settings != null) {
            var model = new GameModel();
            var boardController = new BoardController(model);
            boardController.InitializeGame(settings.getRight());
            var view = new BoardView(boardController, settings.getLeft(), settings.getRight());

            var boardRoot = loadBoardRoot(view.getBoardGrid());
            stage.getScene().setRoot(boardRoot);
            view.startGame();
        }
    }

    private Parent loadBoardRoot(GridPane grid) {
        var pane = new StackPane();
        pane.setId("mainPane");
        pane.getStylesheets().add(this.getClass().getResource("/view/main.css").toExternalForm());
        pane.getChildren().add(grid);

        return pane;
    }

    private Pair<GameMode, Colour> getGameSettings() {
        FXMLLoader loader;
        Parent root;
        try {
            loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("../game_mode/gameModeView.fxml")));
            root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Game mode");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        GameSettingsWindowController controller = loader.getController();
        return new ImmutablePair<>(controller.getGameMode(), controller.getPlayerColour());
    }

    public void onLoadGameButtonClick(ActionEvent actionEvent) {
    }

    public void onExitGameButtonClick(ActionEvent actionEvent) {
        closeWindow(actionEvent);
    }

    private Stage getStage(Event e) {
        Node source = (Node) e.getSource();
        return (Stage) source.getScene().getWindow();
    }

    private void closeWindow(Event e) {
        var stage = getStage(e);
        stage.close();
    }
}
