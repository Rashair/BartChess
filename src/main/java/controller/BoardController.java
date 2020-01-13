package controller;

import model.Colour;
import model.GameModel;
import model.game.Logic;
import model.game.MoveTrace;
import model.grid.Board;
import model.grid.Move;
import model.grid.Square;
import model.pieces.Piece;
import model.players.Computer;
import model.players.Human;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BoardController {
    private final Logic logic;
    private final Board board;

    private List<Move> currentlyConsideredMoves;


    public BoardController(GameModel model) {
        this.board = model.getBoard();
        this.logic = model.getLogic();
    }

    public void InitializeGame() {
        logic.initializeBoard();
        currentlyConsideredMoves = new ArrayList<>();
        Colour randomColour = Colour.getRandomColour();
        var player1 = new Human(randomColour);
        var player2 = new Computer(randomColour.getOppositeColour());
    }

    public void NewGame() {
        //var interaction = new Interaction(logic, player1, player2);
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
}
