import controller.BoardController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.GameModel;
import view.board.BoardView;

public class Main extends Application {
    private final int windowSize = 900;

    @Override
    public void start(Stage stage) {
        var model = new GameModel();
        var boardController = new BoardController(model);
        boardController.InitializeGame();

        var view = new BoardView(boardController);

        var scene = new Scene(view.getBoardGrid(), windowSize, windowSize);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
