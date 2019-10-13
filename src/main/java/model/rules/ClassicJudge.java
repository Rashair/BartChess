package model.rules;

import model.Colour;
import model.GameState;
import model.grid.*;
import model.pieces.*;

import java.util.*;
import java.util.List;


public class ClassicJudge implements IJudge {
    private final Board board;
    private final GameState state;
    private final CheckValidator checkValidator;

    public ClassicJudge(Board board, GameState state) {
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

        var result = Move.createMovesFromSource(new Square(x, y), possiblePositions);
        result.removeIf(checkValidator::isKingAttackedAfterMove);

        return new ArrayList<>(result);
    }

    @Override
    public List<Move> getValidMoves(Queen queen, int x, int y) {
        var movesMaker = new MovesMaker(board, queen.colour, x, y);
        var result = movesMaker.getVerticalAndHorizontalMoves();
        result.addAll(movesMaker.getDiagonalMoves());

        result.removeIf(checkValidator::isKingAttackedAfterMove);

        return new ArrayList<>(result);
    }

    @Override
    public List<Move> getValidMoves(Rook rook, int x, int y) {
        var movesMaker = new MovesMaker(board, rook.colour, x, y);
        var result = movesMaker.getVerticalAndHorizontalMoves();

        result.removeIf(checkValidator::isKingAttackedAfterMove);

        return new ArrayList<>(result);
    }

    // TODO : Castling logic
    @Override
    public List<Move> getValidMoves(Bishop bishop, int x, int y) {
        var movesMaker = new MovesMaker(board, bishop.colour, x, y);
        var result = movesMaker.getDiagonalMoves();

        result.removeIf(checkValidator::isKingAttackedAfterMove);

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

        var result = Move.createMovesFromSource(new Square(x, y), possiblePositions);
        result.removeIf(checkValidator::isKingAttackedAfterMove);

        return new ArrayList<>(result);
    }

    // TODO: En passant logic
    @Override
    public List<Move> getValidMoves(Pawn pawn, int x, int y) {
        Set<Square> possiblePositions = new HashSet<>();
        if (pawn.colour == Colour.White) {
            possiblePositions.add(new Square(x + 1, y));
            if (x == 1)  // Not moved yet
                possiblePositions.add(new Square(x + 2, y));

            if (board.isNotAnEmptySquare(x + 1, y - 1))
                possiblePositions.add(new Square(x + 1, y - 1));

            if (board.isNotAnEmptySquare(x + 1, y + 1))
                possiblePositions.add(new Square(x + 1, y + 1));
        } else {
            possiblePositions.add(new Square(x - 1, y));
            if (x == 6)  // Not moved yet
                possiblePositions.add(new Square(x - 2, y));

            if (board.isNotAnEmptySquare(x - 1, y - 1))
                possiblePositions.add(new Square(x - 1, y - 1));

            if (board.isNotAnEmptySquare(x - 1, y + 1))
                possiblePositions.add(new Square(x - 1, y + 1));
        }

        removeStandardInvalidPositions(possiblePositions, pawn.colour);

        var result = Move.createMovesFromSource(new Square(x, y), possiblePositions);
        result.removeIf(checkValidator::isKingAttackedAfterMove);

        return new ArrayList<>(result);
    }

    private void removeStandardInvalidPositions(Set<Square> positions, Colour colour) {
        positions.removeIf(Board::isOutOfBoardPosition);
        positions.removeIf(position -> isSameColourPiece(colour, position));
    }

    private boolean isSameColourPiece(Colour colour, Square position) {
        var piece = board.getPiece(position);
        return piece != null && piece.colour == colour;
    }
}

