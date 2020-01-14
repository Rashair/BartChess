package view.menu;

import controller.BoardController;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.GameModel;
import view.board.BoardView;

public class MenuViewController {
    @FXML
    public AnchorPane menuPane;

    public MenuViewController() {
    }

    @FXML
    public void initialize() {
    }

    public void onNewGameButtonClick(ActionEvent actionEvent) {
        var stage = getStage(actionEvent);
        var boardRoot = loadBoardRoot();
        stage.getScene().setRoot(boardRoot);
    }

    private Parent loadBoardRoot() {
        var model = new GameModel();
        var boardController = new BoardController(model);
        boardController.InitializeGame();
        var view = new BoardView(boardController);

        var pane = new StackPane();
        pane.setId("mainPane");
        pane.getStylesheets().add(this.getClass().getResource("/view/main.css").toExternalForm());
        pane.getChildren().add(view.getBoardGrid());

        return pane;
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
