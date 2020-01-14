package view.board;

import controller.BoardController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Colour;
import model.game.MoveTrace;
import model.grid.Board;
import model.grid.Square;
import model.pieces.Piece;
import view.game_over.GameOverWindowController;
import view.promotion.PromotionWindowController;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

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
        boardGrid.setId("boardGrid");
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
            MoveTrace moveTrace = controller.movePiece(selectedPieceSquare, clickedSquare);
            if (moveTrace.isValid()) {
                pieceDisplay.moveView(selectedPieceSquare, clickedSquare);

                var move = moveTrace.move;
                if (move.isPromotionMove()) {
                    var promotionClass = getPromotionClass();
                    var dest = move.getDestination();
                    MoveTrace promotionMoveTrace = controller.promotePiece(dest, promotionClass);
                    pieceDisplay.updateView(dest);
                    if (promotionMoveTrace.isGameOver()) {
                        handleGameOver(promotionMoveTrace);
                    }
                }
                else if (move.isEnPassantMove()) {
                    var source = move.getSource();
                    var dest = move.getDestination();
                    pieceDisplay.updateView(new Square(source.x, dest.y));
                }
                else if (move.isCastlingMove()) {
                    var source = move.getSource();
                    var dest = move.getDestination();
                    if (dest.y > source.y) {
                        pieceDisplay.updateView(new Square(source.x, dest.y - 1));
                        pieceDisplay.updateView(new Square(source.x, Board.columnsNum - 1));
                    }
                    else {
                        pieceDisplay.updateView(new Square(source.x, dest.y + 1));
                        pieceDisplay.updateView(new Square(source.x, 0));
                    }
                }

                if (moveTrace.isGameOver()) {
                    handleGameOver(moveTrace);
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
            }
        }
        else {
            removeHighlight();
        }
    }

    private void handleGameOver(MoveTrace trace) {
        if (trace.isDraw()) {
            openGameOverWindow("Draw!");
        }
        else {
            Colour winner = trace.getWinner();
            openGameOverWindow(winner.toString() + " player won!");
        }
    }

    private void openGameOverWindow(String message) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("../game_over/gameOverWindow.fxml")));
            Parent root = loader.load();
            GameOverWindowController controller = loader.getController();
            controller.setMessageLabel(message);

            Stage stage = new Stage();
            stage.setTitle("Game over");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Class<? extends Piece> getPromotionClass() {
        FXMLLoader loader;
        Parent root;
        try {
            loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("../promotion/promotionWindow.fxml")));
            root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Promotion");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        PromotionWindowController controller = loader.getController();
        return controller.getPromotionChoice();
    }

    private void removeHighlight() {
        selectedPieceSquare = null;
        highlight.setActive(false);
    }

    public GridPane getBoardGrid() {
        return boardGrid;
    }
}
