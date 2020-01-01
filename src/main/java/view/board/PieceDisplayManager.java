package view.board;

import controller.BoardController;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import model.grid.Square;

public class PieceDisplayManager {
    private final BoardController controller;
    private final StackPane[][] panels;
    private final Font font;

    public PieceDisplayManager(BoardController controller, StackPane[][] panels, Font font) {
        this.controller = controller;
        this.panels = panels;
        this.font = font;
    }

    public void moveView(Square from, Square to) {
        var prevView = panels[from.x][from.y];
        var currView = panels[to.x][to.y];
        currView.getChildren().clear();
        currView.getChildren().addAll(prevView.getChildren());
        prevView.getChildren().clear();
    }

    public void updateView(Square s) {
        var currView = panels[s.x][s.y];
        currView.getChildren().clear();
        var display = getDisplay(s.x, s.y);
        if (display != null) {
            currView.getChildren().add(display);
        }
    }

    public Text getDisplay(int row, int col) {
        var pieceView = controller.getSquareDisplay(row, col);
        if (pieceView == null) {
            return null;
        }

        Text text = new Text(pieceView);
        text.setFont(font);
        return text;
    }
}
