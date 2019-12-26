package view;

import controller.BoardController;
import javafx.css.PseudoClass;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import model.grid.Board;
import model.grid.Square;
import model.pieces.Queen;

import java.util.List;

public class BoardView {
    private final BoardController controller;

    private final PieceDisplayManager pieceDisplay;
    private final HighlightManager highlight;

    private final GridPane boardGrid;
    private final StackPane[][] panels;
    private Square selectedPieceSquare;

    public BoardView(BoardController controller) {
        this.controller = controller;
        boardGrid = new GridPane();
        boardGrid.getStylesheets().add(this.getClass().getResource("board.css").toExternalForm());
        panels = new StackPane[Board.rowsNum][Board.columnsNum];

        var font = new Font("Tahoma", 48);
        pieceDisplay = new PieceDisplayManager(controller, panels, font);
        for (int row = 0; row < Board.rowsNum; ++row) {
            for (int col = 0; col < Board.columnsNum; ++col) {
                var viewRow = Board.rowsNum - row - 1;

                StackPane squarePanel = new StackPane();
                panels[viewRow][col] = squarePanel;
                String styleClass = (row + col) % 2 == 0 ? "squareEven" : "squareOdd";
                squarePanel.getStyleClass().add(styleClass);

                if (!controller.isEmptySquare(viewRow, col)) {
                    Text text = pieceDisplay.getDisplay(viewRow, col);
                    squarePanel.getChildren().add(text);
                }

                final int finalRow = viewRow;
                final int finalCol = col;
                squarePanel.setOnMouseClicked((MouseEvent event) -> onSquareClicked(event, finalRow, finalCol));

                boardGrid.add(squarePanel, col, row);
            }
        }

        double percentRowSize = 100.0 / Board.rowsNum;
        for (int i = 0; i < Board.rowsNum; i++) {
            var constraint = new RowConstraints();
            constraint.setPercentHeight(percentRowSize);
            constraint.setValignment(VPos.CENTER);
            constraint.setFillHeight(true);
            boardGrid.getRowConstraints().add(constraint);
        }

        double percentColumnSize = 100.0 / Board.columnsNum;
        for (int i = 0; i < Board.columnsNum; ++i) {
            var constraint = new ColumnConstraints();
            constraint.setPercentWidth(percentColumnSize);
            constraint.setHalignment(HPos.CENTER);
            constraint.setFillWidth(true);
            boardGrid.getColumnConstraints().add(constraint);
        }

        highlight = new HighlightManager(panels);
    }

    private void onSquareClicked(MouseEvent event, int row, int col) {
        System.out.println("Clicked " + row + " " + col);

        var clickedSquare = new Square(row, col);
        if (selectedPieceSquare != null && highlight.contains(clickedSquare)) {
            var moveTrace = controller.movePiece(selectedPieceSquare, clickedSquare);
            if (moveTrace.isValid()) {
                pieceDisplay.moveView(selectedPieceSquare, clickedSquare);
                if (moveTrace.isGameOver()) {
                    // TODO: Show window with winner
                    if (moveTrace.isDraw()) {

                    }
                    else {
                        var winner = moveTrace.getWinner();
                    }
                }
                if (moveTrace.move.isPromotionMove()) {
                    // TODO : Ask for piece to choose for promotion
                    var dest = moveTrace.move.getDestination();
                    controller.promotePiece(dest, Queen.class);
                    pieceDisplay.updateView(dest);
                }
            }

            removeHighlight();
        }
        else if (!controller.isEmptySquare(row, col)) {
            removeHighlight();

            List<Square> validSquares = controller.getValidMoves(row, col);
            if (validSquares.size() > 0) {
                selectedPieceSquare = clickedSquare;
                highlight.set(validSquares, selectedPieceSquare);
                highlight.set(true);
            }
        }
        else {
            removeHighlight();
        }
    }

    private void removeHighlight() {
        selectedPieceSquare = null;
        highlight.set(false);
    }

    public GridPane getBoardGrid() {
        return boardGrid;
    }
}
