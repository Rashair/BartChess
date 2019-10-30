package view;


import controller.BoardController;
import javafx.css.PseudoClass;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Control;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import model.grid.Board;
import model.grid.Square;

import java.util.List;

public class BoardView {
    private final BoardController controller;

    private final GridPane boardGrid;
    private final StackPane[][] panels;
    private StackPane selectedPieceSquare;

    public BoardView(BoardController controller) {
        this.controller = controller;

        boardGrid = new GridPane();
        panels = new StackPane[Board.rowsNum][Board.columnsNum];

        Font font = new Font("Tahoma", 48);
        for (int row = 0; row < Board.rowsNum; row++) {
            for (int col = 0; col < Board.columnsNum; col++) {
                StackPane square = new StackPane();
                panels[row][col] = square;
                String color = (row + col) % 2 == 0 ? "white" : "grey";
                square.setStyle("-fx-background-color: " + color + ";");

                var pieceString = controller.getSquareDisplay(Board.rowsNum - row - 1, col);
                if (pieceString.length() > 0) {
                    Text text = new Text(pieceString);
                    text.setFont(font);
                    square.getChildren().add(text);
                }

                final int finalRow = row;
                final int finalCol = col;
                square.setOnMouseClicked((MouseEvent event) -> onSquareClicked(event, finalRow, finalCol));

                boardGrid.add(square, col, row);
            }
        }

        for (int i = 0; i < Board.rowsNum; i++) {
            boardGrid.getRowConstraints().add(new RowConstraints(5, Control.USE_COMPUTED_SIZE, Double.POSITIVE_INFINITY, Priority.ALWAYS, VPos.CENTER, true));
        }
        for (int i = 0; i < Board.columnsNum; ++i) {
            boardGrid.getColumnConstraints().add(new ColumnConstraints(5, Control.USE_COMPUTED_SIZE, Double.POSITIVE_INFINITY, Priority.ALWAYS, HPos.CENTER, true));
        }
    }

    void onSquareClicked(MouseEvent event, int row, int col) {
        System.out.println("Clicked " + row + " " + col);
        if (selectedPieceSquare != null) {
            // TODO: Validating move here
        }
        else {
            // TODO: If is not empty square
            if (controller.getSquareDisplay(row, col).length() > 0) {
                selectedPieceSquare = panels[row][col];
                List<Square> validSquares = controller.getValidMoves(row, col);
                for (Square square : validSquares) {
                    var panel = panels[square.x][square.y];
                    // panel.pseudoClassStateChanged(PseudoClass.getPseudoClass("highlighted"), true);
                    panel.setStyle("-fx-border-color: #384d96");
                }
            }
        }
    }

    public GridPane getBoardGrid() {
        return boardGrid;
    }
}
