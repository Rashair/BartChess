import controller.BoardController;
import javafx.application.Application;
import javafx.scene.ParallelCamera;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.GameModel;
import view.BoardView;

public class Main extends Application {
    private final int windowSize = 900;

    @Override
    public void start(Stage stage) throws Exception {
        var model = new GameModel();
        var boardController = new BoardController(model);
        boardController.InitializeGame();

        var view = new BoardView(boardController);

        var scene = new Scene(view.getBoardGrid(), windowSize, windowSize);
        var camera = new ParallelCamera();
        //camera.setTranslateX(5);
        camera.setRotate(-360);
        scene.setCamera(camera);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
