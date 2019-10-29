package view;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.GameModel;

public class View extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        var model = new GameModel();
        model.NewGame();
        var board = model.getBoard();
        GridPane root = new GridPane();
        Font font = new Font("Tahoma", 48);
        final int size = 8;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                StackPane square = new StackPane();
                String color;
                if ((row + col) % 2 == 0) {
                    color = "white";
                }
                else {
                    color = "grey";
                }
                square.setStyle("-fx-background-color: " + color + ";");
                var piece = board.getPiece(size - row - 1, col);
                var pieceString = piece == null ? "" : piece.toString();
                Text text = new Text(pieceString);
                text.setFont(font);
                square.getChildren().add(text);
                root.add(square, col, row);
            }
        }
        for (int i = 0; i < size; i++) {
            root.getColumnConstraints().add(new ColumnConstraints(5, Control.USE_COMPUTED_SIZE, Double.POSITIVE_INFINITY, Priority.ALWAYS, HPos.CENTER, true));
            root.getRowConstraints().add(new RowConstraints(5, Control.USE_COMPUTED_SIZE, Double.POSITIVE_INFINITY, Priority.ALWAYS, VPos.CENTER, true));
        }

        stage.setScene(new Scene(root, 800, 800));
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
