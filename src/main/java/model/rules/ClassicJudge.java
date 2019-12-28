package model.rules;

import model.Colour;
import model.game.State;
import model.grid.Board;
import model.grid.Move;
import model.grid.Square;
import model.pieces.*;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;


public class ClassicJudge implements IJudge {
    private final Board board;
    private final State state;
    private final CheckValidator checkValidator;

    // TODO : Add a cache storing current valid moves for every existing piece for performance improvement
    // TODO: Move validation when in check - there is a bug
    public ClassicJudge(Board board, State state) {
        this.board = board;
        this.state = state;
        this.checkValidator = new CheckValidator(board, state);
    }

    // TODO : Castling logic
    @Override
    public List<Move> getValidMoves(King king, int x, int y) {
        Set<Square> possiblePositions = new HashSet<>(Arrays.asList(
                new Square(x + 1, y - 1), new Square(x + 1, y), new Square(x + 1, y + 1),
                new Square(x, y - 1), new Square(x, y + 1),
                new Square(x - 1, y - 1), new Square(x - 1, y), new Square(x - 1, y + 1)
        ));

        removeStandardInvalidPositions(possiblePositions, king.colour);

        var result = Move.createMovesFromSource(new Square(x, y), king, possiblePositions);
        result.removeIf(checkValidator::isKingAttackedAfterAllyMove);

        return new ArrayList<>(result);
    }

    @Override
    public List<Move> getValidMoves(Queen queen, int x, int y) {
        var movesMaker = new MovesCreator(board, queen.colour, x, y);
        var squares = movesMaker.getVerticalAndHorizontalMoves();
        squares.addAll(movesMaker.getDiagonalMoves());

        var result = Move.createMovesFromSource(new Square(x, y), queen, squares);
        result.removeIf(checkValidator::isKingAttackedAfterAllyMove);

        return new ArrayList<>(result);
    }

    @Override
    public List<Move> getValidMoves(Rook rook, int x, int y) {
        var movesMaker = new MovesCreator(board, rook.colour, x, y);
        var squares = movesMaker.getVerticalAndHorizontalMoves();

        var result = Move.createMovesFromSource(new Square(x, y), rook, squares);
        result.removeIf(checkValidator::isKingAttackedAfterAllyMove);

        return new ArrayList<>(result);
    }

    // TODO : Castling logic
    @Override
    public List<Move> getValidMoves(Bishop bishop, int x, int y) {
        var movesMaker = new MovesCreator(board, bishop.colour, x, y);
        var squares = movesMaker.getDiagonalMoves();

        var result = Move.createMovesFromSource(new Square(x, y), bishop, squares);
        result.removeIf(checkValidator::isKingAttackedAfterAllyMove);

        return new ArrayList<>(result);
    }

    @Override
    public List<Move> getValidMoves(Knight knight, int x, int y) {
        Set<Square> possiblePositions = new HashSet<>(Arrays.asList(
                new Square(x + 2, y - 1), new Square(x + 2, y + 1), // top
                new Square(x + 1, y + 2), new Square(x - 1, y + 2), // right
                new Square(x - 2, y + 1), new Square(x - 2, y - 1), // bottom
                new Square(x - 1, y - 2), new Square(x + 1, y - 2)  // left
        ));

        removeStandardInvalidPositions(possiblePositions, knight.colour);

        var result = Move.createMovesFromSource(new Square(x, y), knight, possiblePositions);
        result.removeIf(checkValidator::isKingAttackedAfterAllyMove);

        return new ArrayList<>(result);
    }

    // TODO: En passant logic
    @Override
    public List<Move> getValidMoves(Pawn pawn, int x, int y) {
        Set<Square> possiblePositions = new HashSet<>();
        int sign = (pawn.colour == Colour.White ? 1 : -1);

        Square forwardOne = new Square(x + sign, y);
        if (board.isEmptySquare(forwardOne)) {
            possiblePositions.add(forwardOne);

            Square forwardTwo = new Square(x + 2 * sign, y);
            int firstRow = pawn.colour == Colour.White ? 1 : Board.rowsNum - 2;
            if (x == firstRow && board.isEmptySquare(forwardTwo))
                possiblePositions.add(forwardTwo);
        }

        var lastMove = board.getLastMove();
        boolean isLastMoveEnPassant = isEnPassant(lastMove);

        Square attackLeft = new Square(x + sign, y - 1);
        var pieceOnLeft = board.getPiece(attackLeft);
        // En passant
        var skipLeft = new Square(x, y - 1);
        var pieceSkipLeft = board.getPiece(skipLeft);
        if (pieceOnLeft != null && pieceOnLeft.colour != pawn.colour
                || (isLastMoveEnPassant && pieceSkipLeft == lastMove.getMovedPiece()))
            possiblePositions.add(attackLeft);

        Square attackRight = new Square(x + sign, y + 1);
        var pieceOnRight = board.getPiece(attackRight);
        // En passant
        var skipRight = new Square(x, y + 1);
        var pieceSkipRight = board.getPiece(skipRight);
        if (pieceOnRight != null && pieceOnRight.colour != pawn.colour
                || (isLastMoveEnPassant && lastMove.getMovedPiece() == pieceSkipRight))
            possiblePositions.add(attackRight);

        var result = Move.createMovesFromSource(new Square(x, y), pawn, possiblePositions);
        result.removeIf(checkValidator::isKingAttackedAfterAllyMove);

        result.forEach(move -> {
            var destX = move.getDestination().x;
            if (destX == 0 || destX == Board.rowsNum - 1)
                move.setPromotionMove();
        });

        return new ArrayList<>(result);
    }

    private boolean isEnPassant(Move move) {
        if (move == null)
            return false;

        var source = move.getSource();
        var dest = move.getDestination();
        return move.getMovedPiece() instanceof Pawn && Math.abs(source.x - dest.x) == 2;
    }

    private void removeStandardInvalidPositions(Set<Square> positions, Colour colour) {
        positions.removeIf(Board::isOutOfBoardPosition);
        positions.removeIf(position -> isSameColourPiece(colour, position));
    }

    private boolean isSameColourPiece(Colour colour, Square position) {
        var piece = board.getPiece(position);
        return piece != null && piece.colour == colour;
    }

    @Override
    public boolean isKingInCheck(Colour kingColour) {
        return checkValidator.isKingInCheck(kingColour);
    }

    @Override
    public boolean areAnyValidMovesForPlayer(Colour playerColour) {
        for (int i = 0; i < Board.rowsNum; ++i) {
            for (int j = 0; j < Board.columnsNum; ++j) {
                var piece = board.getPiece(i, j);
                if (piece != null && piece.colour == playerColour &&
                        !piece.getValidMoves(this, i, j).isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public List<Triple<Class<? extends Piece>, Colour, String>> getInitialPositionsForAllPieces() {
        return PiecesPositionInitializer.getInitialPositions();
    }
}

