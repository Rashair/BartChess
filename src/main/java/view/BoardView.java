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

    private final Font font;
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

        font = new Font("Tahoma", 48);
        for (int row = 0; row < Board.rowsNum; ++row) {
            for (int col = 0; col < Board.columnsNum; ++col) {
                var viewRow = Board.rowsNum - row - 1;

                StackPane squarePanel = new StackPane();
                panels[viewRow][col] = squarePanel;
                String styleClass = (row + col) % 2 == 0 ? "squareEven" : "squareOdd";
                squarePanel.getStyleClass().add(styleClass);

                if (!controller.isEmptySquare(viewRow, col)) {
                    Text text = getPieceDisplay(viewRow, col);
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
    }

    private Text getPieceDisplay(int row, int col) {
        var pieceView = controller.getSquareDisplay(row, col);
        Text text = new Text(pieceView);
        text.setFont(font);
        return text;
    }

    private void onSquareClicked(MouseEvent event, int row, int col) {
        System.out.println("Clicked " + row + " " + col);

        var clickedSquare = new Square(row, col);
        if (selectedPieceSquare != null && currentlyHighlighted.contains(clickedSquare)) {
            var moveTrace = controller.movePiece(selectedPieceSquare, clickedSquare);
            if (moveTrace.isValid()) {
                movePieceView(selectedPieceSquare, clickedSquare);
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
                    updatePieceView(dest);
                }
            }

            setHighlight(false);
        }
        else if (!controller.isEmptySquare(row, col)) {
            setHighlight(false);

            List<Square> validSquares = controller.getValidMoves(row, col);
            if (validSquares.size() > 0) {
                selectedPieceSquare = clickedSquare;
                currentlyHighlighted = validSquares;
                setHighlight(true);
            }
        }
        else {
            setHighlight(false);
        }
    }

    private void setHighlight(boolean val) {
        if (currentlyHighlighted == null) {
            return;
        }

        setHighlightForSquare(selectedPieceSquare, val);
        for (Square square : currentlyHighlighted) {
            setHighlightForSquare(square, val);
        }

        if (!val) {
            currentlyHighlighted = null;
            selectedPieceSquare = null;
        }
    }

    private void setHighlightForSquare(Square s, boolean val) {
        var squareView = panels[s.x][s.y];
        squareView.pseudoClassStateChanged(highlight, val);
    }

    private void movePieceView(Square from, Square to) {
        var prevView = panels[from.x][from.y];
        var currView = panels[to.x][to.y];
        currView.getChildren().clear();
        currView.getChildren().addAll(prevView.getChildren());
        prevView.getChildren().clear();
    }

    private void updatePieceView(Square s) {
        var currView = panels[s.x][s.y];
        currView.getChildren().clear();
        var display = getPieceDisplay(s.x, s.y);
        currView.getChildren().add(display);
    }

    public GridPane getBoardGrid() {
        return boardGrid;
    }
}
