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
    private List<Square> currentlyHighlighted;
    private final PseudoClass highlight;

    public BoardView(BoardController controller) {
        this.controller = controller;

        highlight = PseudoClass.getPseudoClass("highlighted");
        boardGrid = new GridPane();
        var resource = this.getClass().getResource("board.css").toExternalForm();
        boardGrid.getStylesheets().add(resource);
        panels = new StackPane[Board.rowsNum][Board.columnsNum];

        Font font = new Font("Tahoma", 48);
        for (int row = 0; row < Board.rowsNum; ++row) {
            for (int col = 0; col < Board.columnsNum; ++col) {
                StackPane squarePanel = new StackPane();
                panels[row][col] = squarePanel;
                String styleClass = (row + col) % 2 == 0 ? "squareEven" : "squareOdd";
                squarePanel.getStylesheets().add(resource);
                squarePanel.getStyleClass().add(styleClass);

                var styles = squarePanel.getStylesheets();
                var classes = squarePanel.getStyleClass();
                // Different orientation - chessboard starts from 0 and goes up
                int boardRow = Board.rowsNum - row - 1;
                if (!controller.isEmptySquare(boardRow, col)) {
                    var pieceView = controller.getSquareDisplay(boardRow, col);
                    Text text = new Text(pieceView);
                    text.setFont(font);
                    squarePanel.getChildren().add(text);
                }

                final int finalRow = row;
                final int finalCol = col;
                squarePanel.setOnMouseClicked((MouseEvent event) -> onSquareClicked(event, finalRow, finalCol));

                boardGrid.add(squarePanel, col, row);
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
        boolean clickedEmptySquare = controller.isEmptySquare(row, col);
        if (selectedPieceSquare != null && clickedEmptySquare) {
            selectedPieceSquare = null;
        }
        else if (!clickedEmptySquare) {
            selectedPieceSquare = panels[row][col];
            List<Square> validSquares = controller.getValidMoves(row, col);

            setHighlighting(false);
            currentlyHighlighted = validSquares;
            setHighlighting(true);
        }
        else if (currentlyHighlighted != null) {
            setHighlighting(false);
            currentlyHighlighted = null;
        }
    }

    private void setHighlighting(boolean val) {
        if (currentlyHighlighted == null) {
            return;
        }

        for (Square square : currentlyHighlighted) {
            var squareView = panels[square.x][square.y];
            squareView.pseudoClassStateChanged(highlight, val);
        }
    }

    public GridPane getBoardGrid() {
        return boardGrid;
    }
}
