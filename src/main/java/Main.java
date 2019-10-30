import controller.BoardController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.GameModel;
import view.BoardView;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        var model = new GameModel();
        var boardController = new BoardController(model);
        boardController.InitializeGame();

        var view = new BoardView(boardController);

        stage.setScene(new Scene(view.getBoardGrid(), 800, 800));
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
