package model.players;

import model.Colour;
import model.game.Logic;
import model.game.MoveTrace;
import model.grid.Board;
import model.grid.Move;
import model.pieces.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ComputerLogic {
    private final Logic logic;
    private final Board board;
    private final HashMap<Class<? extends Piece>, Double> points;

    public ComputerLogic(Logic logic, Board board) {
        this.logic = logic;
        this.board = board;
        points = new HashMap<>();
        points.put(King.class, 90.0);
        points.put(Queen.class, 9.0);
        points.put(Rook.class, 5.0);
        points.put(Bishop.class, 3.0);
        points.put(Knight.class, 3.0);
        points.put(Pawn.class, 1.0);
    }

    public MoveTrace makeMove(Colour colour) {
        var move = chooseMove(colour);
        if (move != null) {
            return logic.makeMove(move);
        }

        return new MoveTrace();
    }

    private Move chooseMove(Colour colour) {
        List<Move> allMoves = new LinkedList<>();
        for (int i = 0; i < Board.rowsNum; ++i) {
            for (int j = 0; j < Board.columnsNum; ++j) {
                var piece = board.getPiece(i, j);
                if (piece != null && piece.colour == colour) {
                    var moves = logic.getValidMoves(i, j);
                    allMoves.addAll(moves);
                }
            }
        }

        Move bestMove = null;
        double maxPoints = -Double.MAX_VALUE;
        var random = new Random();
        for (Move move : allMoves) {
            var points = computePointsMove(move) + random.nextDouble();
            if (points > maxPoints) {
                bestMove = move;
                maxPoints = points;
            }
        }

        return bestMove;
    }

    private double computePointsMove(Move move) {
        double points = 0.0;
        if (move.isPromotionMove()) {
            points += 7.0;
        }
        if (move.isEnPassantMove()) {
            points += 1.5;
        }

        var movedPiece = move.getMovedPiece();
        var killedPiece = board.getPiece(move.getDestination());
        points += getPointsForKilledPiece(movedPiece, killedPiece);

        return points;
    }

    private double getPointsForKilledPiece(Piece hunter, Piece prey) {
        double hunterValue = points.get(hunter.getClass());
        if (prey == null) {
            return -hunterValue / 200.0;
        }

        double preyValue = points.get(prey.getClass());
        return 2.5 * preyValue - hunterValue;
    }
}
