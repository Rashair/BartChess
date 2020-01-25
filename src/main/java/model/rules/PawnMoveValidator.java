package model.rules;

import model.Colour;
import model.grid.Board;
import model.grid.Move;
import model.grid.Square;
import model.pieces.Pawn;

import java.util.HashSet;
import java.util.Set;

class PawnMoveValidator {
    private final Board board;
    private final Pawn pawn;
    private final Square pos;

    public PawnMoveValidator(Board board, Pawn pawn, Square pos) {
        this.board = board;
        this.pawn = pawn;
        this.pos = pos;
    }

    Set<Move> getValidMoves() {
        Set<Square> possiblePositions = new HashSet<>();
        int direction = (pawn.colour == Colour.White ? 1 : -1);

        Square forwardOne = new Square(pos.x + direction, pos.y);
        if (board.isEmptySquare(forwardOne)) {
            possiblePositions.add(forwardOne);

            Square forwardTwo = new Square(pos.x + 2 * direction, pos.y);
            int firstRow = pawn.colour == Colour.White ? 1 : Board.rowsNum - 2;
            if (pos.x == firstRow && board.isEmptySquare(forwardTwo)) {
                possiblePositions.add(forwardTwo);
            }
        }

        var attackPositions = getPossiblePawnAttackPositions(direction);
        possiblePositions.addAll(attackPositions);

        var result = Move.createMovesFromSource(pos, pawn, possiblePositions);
        result.forEach(move -> {
            var dest = move.getDestination();
            if (dest.x == 0 || dest.x == Board.rowsNum - 1) {
                move.setPromotionMove();
            }
            if (attackPositions.contains(dest) && board.getPiece(dest) == null) {
                move.setEnPassantMove();
            }
        });

        return result;
    }

    private Set<Square> getPossiblePawnAttackPositions(int direction) {
        var possibleAttackPositions = new HashSet<Square>();
        var lastMove = board.getLastMove();
        boolean isLastMoveTheFirstForEnemyPawn = isFirstPawnMove(lastMove);

        Square attackLeft = new Square(pos.x + direction, pos.y - 1);
        Square skipLeft = new Square(pos.x, pos.y - 1);
        var pieceOnLeft = board.getPiece(attackLeft);
        var pieceSkipLeft = board.getPiece(skipLeft);
        if ((pieceOnLeft != null && pieceOnLeft.colour != pawn.colour)
                || (isLastMoveTheFirstForEnemyPawn && pieceSkipLeft == lastMove.getMovedPiece())) {
            possibleAttackPositions.add(attackLeft);
        }

        Square attackRight = new Square(pos.x + direction, pos.y + 1);
        Square skipRight = new Square(pos.x, pos.y + 1);
        var pieceOnRight = board.getPiece(attackRight);
        var pieceSkipRight = board.getPiece(skipRight);
        if ((pieceOnRight != null && pieceOnRight.colour != pawn.colour)
                || (isLastMoveTheFirstForEnemyPawn && lastMove.getMovedPiece() == pieceSkipRight)) {
            possibleAttackPositions.add(attackRight);
        }

        return possibleAttackPositions;
    }


    private boolean isFirstPawnMove(Move move) {
        if (move == null) {
            return false;
        }

        var source = move.getSource();
        var dest = move.getDestination();
        return move.getMovedPiece() instanceof Pawn && Math.abs(source.x - dest.x) == 2;
    }
}
