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
    private Square selectedPieceSquare;
    private List<Square> currentlyHighlighted;
    private final PseudoClass highlight;

    public BoardView(BoardController controller) {
        this.controller = controller;

        highlight = PseudoClass.getPseudoClass("highlighted");
        boardGrid = new GridPane();
        boardGrid.getStylesheets().add(this.getClass().getResource("board.css").toExternalForm());
        panels = new StackPane[Board.rowsNum][Board.columnsNum];

        Font font = new Font("Tahoma", 48);
        for (int row = 0; row < Board.rowsNum; ++row) {
            for (int col = 0; col < Board.columnsNum; ++col) {
                StackPane squarePanel = new StackPane();
                panels[row][col] = squarePanel;
                String styleClass = (row + col) % 2 == 0 ? "squareEven" : "squareOdd";
                squarePanel.getStyleClass().add(styleClass);

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
            boardGrid.getRowConstraints().add(new RowConstraints(-5, Control.USE_COMPUTED_SIZE, Double.POSITIVE_INFINITY, Priority.ALWAYS, VPos.CENTER, true));
        }
        for (int i = 0; i < Board.columnsNum; ++i) {
            boardGrid.getColumnConstraints().add(new ColumnConstraints(5, Control.USE_COMPUTED_SIZE, Double.POSITIVE_INFINITY, Priority.ALWAYS, HPos.CENTER, true));
        }
    }

    void onSquareClicked(MouseEvent event, int row, int col) {
        System.out.println("Clicked " + row + " " + col);

        var clickedSquare = new Square(row, col);
        if (selectedPieceSquare != null && currentlyHighlighted.contains(clickedSquare)) {
            if (controller.movePiece(selectedPieceSquare, clickedSquare)) {
                movePieceView(selectedPieceSquare, clickedSquare);
            }

            selectedPieceSquare = null;
        }
        else if (!controller.isEmptySquare(row, col)) {
            setHighlighting(false);

            List<Square> validSquares = controller.getValidMoves(row, col);
            if (validSquares.size() > 0) {
                selectedPieceSquare = clickedSquare;
                currentlyHighlighted = validSquares;
                setHighlighting(true);
            }
            return;
        }

        setHighlighting(false);
    }

    private void setHighlighting(boolean val) {
        if (currentlyHighlighted == null) {
            return;
        }

        for (Square square : currentlyHighlighted) {
            var squareView = panels[square.x][square.y];
            squareView.pseudoClassStateChanged(highlight, val);
        }

        if (!val) {
            currentlyHighlighted = null;
        }
    }

    private void movePieceView(Square from, Square to) {
        var prevView = panels[from.x][from.y];
        var currView = panels[to.x][to.y];
        currView.getChildren().clear();
        currView.getChildren().addAll(prevView.getChildren());
        prevView.getChildren().clear();
    }

    public GridPane getBoardGrid() {
        return boardGrid;
    }
}
