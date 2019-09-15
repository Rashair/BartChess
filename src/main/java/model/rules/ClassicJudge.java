package model.rules;

import model.Colour;
import model.GameState;
import model.grid.*;
import model.pieces.*;

import java.util.*;
import java.util.List;


public class ClassicJudge implements IJudge {
    private final Board board;
    private GameState state;

    public ClassicJudge(Board board, GameState state) {
        this.board = board;
        this.state = state;
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

        var resultMoves = Move.createMovesFromSource(new Square(x, y), possiblePositions.toArray(Square[]::new));
        resultMoves.removeIf(this::isKingAttackedAfterOwnPieceMove);

        return resultMoves;
    }


    /*       ?
     *       K
     *       x
     *       x
     *       R
     * R - Rook, K - King, x - squares attacked by Rook
     */
    private boolean isKingAttackedAfterOwnPieceMove(Move move) {
        var source = move.getSource();
        var destination = move.getDestination();
        var movedPiece = board.getPiece(source);

        board.movePiece(move);

        var kingColour = movedPiece.colour;
        var kingPosition = board.getKingPosition(kingColour);

        var result = false;
        if (kingPosition == destination) { // King has moved
            result = isKingAttackedFromTop(kingColour, kingPosition) ||
                    isKingAttackedFromBottom(kingColour, kingPosition) ||
                    isKingAttackedFromLeft(kingColour, kingPosition) ||
                    isKingAttackedFromRight(kingColour, kingPosition);
            result = result ||
                    isKingAttackedFromBottomLeft(kingColour, kingPosition) ||
                    isKingAttackedFromBottomRight(kingColour, kingPosition) ||
                    isKingAttackedFromUpperLeft(kingColour, kingPosition) ||
                    isKingAttackedFromUpperRight(kingColour, kingPosition);
        }
        else if (source.x == kingPosition.x) {
            if (source.y < kingPosition.y) {
                result = isKingAttackedFromLeft(kingColour, kingPosition);
            }
            else { // source.y > kingPosition.y
                result = isKingAttackedFromRight(kingColour, kingPosition);
            }
        }
        else if (source.x < kingPosition.x) {
            if (source.y == kingPosition.y) {
                result = isKingAttackedFromBottom(kingColour, kingPosition);
            }
            else if (source.y < kingPosition.y) {
                result = isKingAttackedFromBottomLeft(kingColour, kingPosition);
            }
            else { // source.y > kingPosition.y
                result = isKingAttackedFromBottomRight(kingColour, kingPosition);
            }
        }
        else { // source.x > kingPosition.x
            if (source.y == kingPosition.y) {
                result = isKingAttackedFromTop(kingColour, kingPosition);
            }
            else if (source.y < kingPosition.y) {
                result = isKingAttackedFromUpperLeft(kingColour, kingPosition);
            }
            else { // source.y > kingPosition.y
                result = isKingAttackedFromUpperRight(kingColour, kingPosition);
            }
        }

        board.movePiece(destination, source);
        return result;
    }


    private boolean isKingAttackedFromTop(Colour kingColour, Square kingPosition) {
        for (int i = kingPosition.x + 1; i < Board.rowsNum; ++i) {
            if (isKingAttackedFromQueenOrRook(kingColour, i, kingPosition.y)) {
                return true;
            }
        }

        return false;
    }

    private boolean isKingAttackedFromBottom(Colour kingColour, Square kingPosition) {
        for (int i = kingPosition.x - 1; i >= 0; --i) {
            if (isKingAttackedFromQueenOrRook(kingColour, i, kingPosition.y)) {
                return true;
            }
        }

        return false;
    }

    private boolean isKingAttackedFromLeft(Colour kingColour, Square kingPosition) {
        for (int i = kingPosition.y - 1; i >= 0; --i) {
            if (isKingAttackedFromQueenOrRook(kingColour, kingPosition.x, i)) {
                return true;
            }
        }

        return false;
    }

    private boolean isKingAttackedFromRight(Colour kingColour, Square kingPosition) {
        for (int i = kingPosition.y + 1; i < Board.columnsNum; ++i) {
            if (isKingAttackedFromQueenOrRook(kingColour, kingPosition.x, i)) {
                return true;
            }
        }

        return false;
    }

    private boolean isKingAttackedFromQueenOrRook(Colour kingColour, int x, int y) {
        Piece currentPiece = board.getPiece(x, y);
        if (currentPiece != null) {
            return isKingAttackedFromPiece(kingColour, currentPiece, Queen.class, Rook.class);
        }

        return false;
    }

    private boolean isKingAttackedFromUpperRight(Colour kingColour, Square kingPosition) {
        int i = kingPosition.x + 1;
        int j = kingPosition.y + 1;
        for (; i < Board.rowsNum && j < Board.columnsNum; ++i, ++j) {
            if (isKingAttackedFromQueenOrBishop(kingColour, i, j)) {
                return true;
            }
        }

        return false;
    }

    private boolean isKingAttackedFromUpperLeft(Colour kingColour, Square kingPosition) {
        int i = kingPosition.x + 1;
        int j = kingPosition.y - 1;
        for (; i < Board.rowsNum && j >= 0; ++i, --j) {
            if (isKingAttackedFromQueenOrBishop(kingColour, i, j)) {
                return true;
            }
        }

        return false;
    }

    private boolean isKingAttackedFromBottomLeft(Colour kingColour, Square kingPosition) {
        int i = kingPosition.x - 1;
        int j = kingPosition.y - 1;
        for (; i >= 0 && j >= 0; --i, --j) {
            if (isKingAttackedFromQueenOrBishop(kingColour, i, j)) {
                return true;
            }
        }

        return false;
    }

    private boolean isKingAttackedFromBottomRight(Colour kingColour, Square kingPosition) {
        int i = kingPosition.x - 1;
        int j = kingPosition.y + 1;
        for (; i >= 0 && j < Board.columnsNum; --i, ++j) {
            if (isKingAttackedFromQueenOrBishop(kingColour, i, j)) {
                return true;
            }
        }

        return false;
    }

    private boolean isKingAttackedFromQueenOrBishop(Colour kingColour, int x, int y) {
        Piece currentPiece = board.getPiece(x, y);
        if (currentPiece != null) {
            return isKingAttackedFromPiece(kingColour, currentPiece, Queen.class, Bishop.class);
        }

        return false;
    }


    private boolean isKingAttackedFromPiece(Colour kingColour, Piece piece, Class<?> enemy1, Class<?> enemy2) {
        return piece.colour != kingColour && (enemy1.isInstance(piece) || enemy2.isInstance(piece));
    }


    @Override
    public List<Move> getValidMoves(Queen queen, int x, int y) {
        var movesMaker = new MovesMaker(board, queen.colour, x, y);
        var result = movesMaker.getVerticalAndHorizontalMoves();
        result.addAll(movesMaker.getDiagonalMoves());

        result.removeIf(this::isKingAttackedAfterOwnPieceMove);

        return result;
    }

    @Override
    public List<Move> getValidMoves(Rook rook, int x, int y) {
        var movesMaker = new MovesMaker(board, rook.colour, x, y);
        var result = movesMaker.getVerticalAndHorizontalMoves();

        result.removeIf(this::isKingAttackedAfterOwnPieceMove);

        return result;
    }

    // TODO : Castling logic
    @Override
    public List<Move> getValidMoves(Bishop bishop, int x, int y) {
        var movesMaker = new MovesMaker(board, bishop.colour, x, y);
        var result = movesMaker.getDiagonalMoves();

        result.removeIf(this::isKingAttackedAfterOwnPieceMove);

        return result;
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

        return Move.createMovesFromSource(new Square(x, y), possiblePositions.toArray(Square[]::new));
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
        }
        else {
            possiblePositions.add(new Square(x - 1, y));
            if (x == 6)  // Not moved yet
                possiblePositions.add(new Square(x - 2, y));

            if (board.isNotAnEmptySquare(x - 1, y - 1))
                possiblePositions.add(new Square(x - 1, y - 1));

            if (board.isNotAnEmptySquare(x - 1, y + 1))
                possiblePositions.add(new Square(x - 1, y + 1));
        }

        removeStandardInvalidPositions(possiblePositions, pawn.colour);

        var result = Move.createMovesFromSource(new Square(x, y), possiblePositions.toArray(Square[]::new));
        result.removeIf(this::isKingAttackedAfterOwnPieceMove);

        return result;
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

