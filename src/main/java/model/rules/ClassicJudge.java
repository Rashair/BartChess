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
        resultMoves.removeIf(this::isKingAttackedAfterMove);

        return resultMoves;
    }


    /*       ?
     *       K
     *       x
     *       x
     *       R
     * R - Rook, K - King, x - squares attacked by Rook
     */
    private boolean isKingAttackedAfterMove(Move move) {
        var source = move.getSource();
        var destination = move.getDestination();
        var movedPiece = board.getPiece(source);

        board.movePiece(move);

        var kingColour = movedPiece.colour;
        var kingPosition = board.getKingPosition(kingColour);

        var result = false;
        if (kingPosition == destination) { // King has moved

        }
        else if (source.x == kingPosition.x) {
            if (source.y < kingPosition.y) {
                // Attacks from Queen or Rook
                for (int j = kingPosition.y - 1; j >= 0; --j) {
                    var currentPiece = board.getPiece(kingPosition.x, j);
                    if (currentPiece != null) {
                        if (currentPiece.colour != kingColour && (currentPiece instanceof Queen || currentPiece instanceof Rook))
                            result = true;
                        break;
                    }
                }
            }
            else { // source.y > kingPosition.y
                // Attacks from Queen or Rook
                for (int j = kingPosition.y + 1; j < Board.columnsNum; ++j) {
                    var currentPiece = board.getPiece(kingPosition.x, j);
                    if (currentPiece != null) {
                        if (currentPiece.colour != kingColour && (currentPiece instanceof Queen || currentPiece instanceof Rook))
                            result = true;
                        break;
                    }
                }
            }
        }
        else if (source.x < kingPosition.x) {
            if (source.y == kingPosition.y) {
                // Attacks from Queen or Rook
                for (int i = kingPosition.x - 1; i >= 0; --i) {
                    var currentPiece = board.getPiece(i, kingPosition.y);
                    if (currentPiece != null) {
                        if (currentPiece.colour != kingColour && (currentPiece instanceof Queen || currentPiece instanceof Rook))
                            result = true;
                        break;
                    }
                }
            }
            else if (source.y < kingPosition.y) {

            }
            else { // source.y > kingPosition.y

            }
        }
        else { // source.x > kingPosition.x
            if (source.y == kingPosition.y) {
                // Attacks from Queen or Rook
                for (int i = kingPosition.x + 1; i < Board.rowsNum; ++i) {
                    var currentPiece = board.getPiece(i, kingPosition.y);
                    if (currentPiece != null) {
                        if (currentPiece.colour != kingColour && (currentPiece instanceof Queen || currentPiece instanceof Rook))
                            result = true;
                        break;
                    }
                }
            }
            else if (source.y < kingPosition.y) {

            }
            else { // source.y > kingPosition.y

            }
        }


        board.movePiece(destination, source);
        return result;
    }

    @Override
    public List<Move> getValidMoves(Queen queen, int x, int y) {
        var movesMaker = new MovesMaker(board, queen.colour, x, y);
        var result = movesMaker.getVerticalAndHorizontalMoves();
        result.addAll(movesMaker.getDiagonalMoves());

        result.removeIf(this::isKingAttackedAfterMove);

        return result;
    }

    @Override
    public List<Move> getValidMoves(Rook rook, int x, int y) {
        var movesMaker = new MovesMaker(board, rook.colour, x, y);
        var result = movesMaker.getVerticalAndHorizontalMoves();

        result.removeIf(this::isKingAttackedAfterMove);

        return result;
    }

    // TODO : Castling logic
    @Override
    public List<Move> getValidMoves(Bishop bishop, int x, int y) {
        var movesMaker = new MovesMaker(board, bishop.colour, x, y);
        var result = movesMaker.getDiagonalMoves();

        result.removeIf(this::isKingAttackedAfterMove);

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
        result.removeIf(this::isKingAttackedAfterMove);

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

