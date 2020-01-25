package view.board;

import controller.BoardController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
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
import javafx.util.Duration;
import model.Colour;
import model.game.MoveTrace;
import model.grid.Board;
import model.grid.Square;
import model.pieces.King;
import model.pieces.Piece;
import model.pieces.Queen;
import model.players.GameMode;
import view.game_over.GameOverWindowController;
import view.promotion.PromotionWindowController;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class BoardView {
    private final BoardController controller;
    private final GameMode mode;
    private final Colour humanColour;

    private final PieceDisplayManager pieceDisplay;
    private final HighlightManager highlight;
    private final GridPane boardGrid;
    private Square selectedPieceSquare;

    private CompletableFuture<MoveTrace> currentPlayerMove;
    private Colour currentPlayerColour;

    public BoardView(BoardController controller, GameMode mode, Colour humanColour) {
        this.controller = controller;
        this.mode = mode;
        this.humanColour = humanColour;

        boardGrid = new GridPane();
        boardGrid.setId("boardGrid");
        boardGrid.getStylesheets().add(this.getClass().getResource("board.css").toExternalForm());
        StackPane[][] panels = new StackPane[Board.rowsNum][Board.columnsNum];
        highlight = new HighlightManager(panels);
        Font font = new Font("Tahoma", 48);
        pieceDisplay = new PieceDisplayManager(controller, panels, font);

        for (int row = 0; row < Board.rowsNum; ++row) {
            for (int col = 0; col < Board.columnsNum; ++col) {
                var viewRow = humanColour == Colour.Black ? row : Board.rowsNum - row - 1;
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
    }

    public void startGame() {
        currentPlayerColour = Colour.White;
        currentPlayerMove = CompletableFuture.supplyAsync(MoveTrace::new);
        if (mode == GameMode.COMPUTER_COMPUTER) {
            boardGrid.setOnMouseClicked((e) -> makeComputerMove());
        }
        else if (humanColour == Colour.Black) {
            runLater(() -> {
                currentPlayerMove = controller.makeComputerMove(currentPlayerColour);
            }, this::updateGUIWhenComputerMoveFinished, 1000);
        }
    }

    private void runLater(Runnable runnable1, Runnable runnable2, int timeoutMs) {
        final KeyFrame kf1 = new KeyFrame(Duration.millis(0), e -> runnable1.run());
        final KeyFrame kf2 = new KeyFrame(Duration.millis(timeoutMs), e -> runnable2.run());
        final Timeline timeline = new Timeline(kf1, kf2);
        Platform.runLater(timeline::play);
    }

    private void makeComputerMove() {
        if (controller.isGameOver()) {
            System.out.println("Game over");
            return;
        }

        if (currentPlayerMove.isDone() && currentPlayerColour == controller.getPlayerColourTurn()) {
            currentPlayerMove = controller.makeComputerMove(currentPlayerColour);
            updateGUIWhenComputerMoveFinished();
            Platform.runLater(() -> Platform.runLater(this::makeComputerMove));
        }
        else {
            runLater(() -> {}, this::makeComputerMove, 750);
        }
    }


    private void onSquareClicked(MouseEvent event, int row, int col) {
        System.out.println("Clicked " + row + " " + col);
        if (mode != GameMode.PLAYER_PLAYER && currentPlayerColour != humanColour) {
            return;
        }

        var clickedSquare = new Square(row, col);
        if (currentPlayerMove.isDone() && selectedPieceSquare != null && highlight.contains(clickedSquare)) {
            MoveTrace moveTrace = controller.movePiece(selectedPieceSquare, clickedSquare);
            if (moveTrace.isValid()) {
                onMove(moveTrace);
                if (!moveTrace.isGameOver()) {
                    if (mode == GameMode.PLAYER_COMPUTER) {
                        currentPlayerMove = controller.makeComputerMove(currentPlayerColour);
                        Platform.runLater(this::updateGUIWhenComputerMoveFinished);
                    }
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

    private void onMove(MoveTrace moveTrace, boolean isComputerMove) {
        var move = moveTrace.move;
        pieceDisplay.moveView(move.getSource(), move.getDestination());

        if (move.isPromotionMove()) {
            var promotionClass = getPromotionClass(isComputerMove);
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

        if (highlight.isInCheck()) {
            if (move.getMovedPiece() instanceof King) {
                highlight.setCheck(move.getSource(), false);
            }
            else {
                highlight.setCheck(controller.getKingPosition(currentPlayerColour), false);
            }
        }

        currentPlayerColour = currentPlayerColour.getOppositeColour();
        if (controller.isInCheck(currentPlayerColour)) {
            highlight.setCheck(controller.getKingPosition(currentPlayerColour), true);
        }
        if (moveTrace.isGameOver()) {
            handleGameOver(moveTrace);
        }
    }

    private void onMove(MoveTrace moveTrace) {
        onMove(moveTrace, false);
    }

    private void updateGUIWhenComputerMoveFinished() {
        if (currentPlayerMove.isDone()) {
            onComputerMoveFinished();
        }
        else {
            Platform.runLater(this::updateGUIWhenComputerMoveFinished);
        }
    }

    private void onComputerMoveFinished() {
        try {
            var trace = currentPlayerMove.get();
            if (trace.isValid()) {
                onMove(trace, true);
            }
            else {
                throw new Exception("Invalid move");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while doing computer move: " + e.getMessage());
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

    private Class<? extends Piece> getPromotionClass(boolean isComputerMove) {
        if (isComputerMove) {
            return Queen.class;
        }

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
