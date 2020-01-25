package controller;

import model.Colour;
import model.GameModel;
import model.game.Logic;
import model.game.MoveTrace;
import model.game.State;
import model.grid.Board;
import model.grid.Move;
import model.grid.Square;
import model.pieces.Piece;
import model.players.ComputerLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class BoardController {
    private final Logic logic;
    private final Board board;
    private final State state;
    private final ComputerLogic computerLogic;
    private List<Move> currentlyConsideredMoves;

    public BoardController(GameModel model) {
        this.board = model.getBoard();
        this.logic = model.getLogic();
        this.state = model.getState();
        this.computerLogic = new ComputerLogic(logic, board);
    }

    public void InitializeGame(Colour playerColour) {
        logic.initializeBoard();
        currentlyConsideredMoves = new ArrayList<>();
    }

    public boolean isEmptySquare(int row, int col) {
        return board.isEmptySquare(row, col);
    }

    public String getSquareDisplay(int row, int col) {
        var piece = board.getPiece(row, col);
        return piece != null ? piece.toString() : null;
    }

    public List<Square> getValidMoves(int row, int col) {
        var piece = board.getPiece(row, col);
        if (piece != null) {
            currentlyConsideredMoves = logic.getValidMoves(row, col);
            return currentlyConsideredMoves.stream().map(Move::getDestination).collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    public MoveTrace movePiece(Square from, Square to) {
        if (currentlyConsideredMoves.size() > 0) {
            var validMove = currentlyConsideredMoves.stream().
                    filter((Move move) -> move.getSource().equals(from) && move.getDestination().equals(to)).
                    findFirst();
            if (validMove.isPresent()) {
                var move = validMove.get();
                return logic.makeMove(move);
            }
        }

        return new MoveTrace();
    }

    public MoveTrace promotePiece(Square square, Class<? extends Piece> promoted) {
        return logic.promotePiece(square, promoted);
    }

    public CompletableFuture<MoveTrace> makeComputerMove(Colour colour) {
        return CompletableFuture.supplyAsync(() -> computerLogic.makeMove(colour));
    }

    public boolean isGameOver() {
        return logic.isGameOver();
    }

    public Colour getPlayerColourTurn() {
        return logic.getPlayerTurnColour();
    }

    public boolean isInCheck(Colour playerColour) {
        return state.isInCheck(playerColour);
    }

    public Square getKingPosition(Colour colour) {
        return board.getKingPosition(colour);
    }
}
