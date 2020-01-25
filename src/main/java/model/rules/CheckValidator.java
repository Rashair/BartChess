package model.rules;

import model.Colour;
import model.game.State;
import model.grid.Board;
import model.grid.Move;
import model.grid.Square;
import model.pieces.*;

import java.util.HashSet;
import java.util.Set;

class CheckValidator {
    private final Board board;
    private final State state;
    private final MoveSimulator moveSimulator;
    private Colour kingColour;

    CheckValidator(Board board, State state) {
        this.board = board;
        this.state = state;
        this.moveSimulator = new MoveSimulator(board);
    }

    private void setKingColour(Move move) {
        kingColour = move.getMovedPiece().colour;
    }

    private void setKingColour(Colour colour) {
        this.kingColour = colour;
    }

    boolean isKingAttackedAfterAllyMove(Move move) {
        setKingColour(move);
        moveSimulator.setMove(move);
        moveSimulator.makeMove();

        var source = move.getSource();
        var kingPosition = board.getKingPosition(kingColour);
        if (kingPosition == move.getDestination() || state.isInCheck(kingColour)) {
            if (isKingInCheck(kingColour)) {
                return reverseMoveAndReturnTrue();
            }
        }
        else if (source.x == kingPosition.x) {
            if (source.y < kingPosition.y && isKingAttackedFromDirection(kingPosition, Direction.Left)) {
                return reverseMoveAndReturnTrue();
            }
            else if (isKingAttackedFromDirection(kingPosition, Direction.Right)) {
                return reverseMoveAndReturnTrue();
            }
        }
        else if (source.x < kingPosition.x) {
            if (source.y == kingPosition.y && isKingAttackedFromDirection(kingPosition, Direction.Bottom)) {
                return reverseMoveAndReturnTrue();
            }
            else if (source.y < kingPosition.y && isKingAttackedFromDirection(kingPosition, Direction.LowerLeft)) {
                return reverseMoveAndReturnTrue();
            }
            else if (isKingAttackedFromDirection(kingPosition, Direction.LowerRight)) // source.y > kingPosition.y
            {
                return reverseMoveAndReturnTrue();
            }
        }
        else { // source.x > kingPosition.x
            if (source.y == kingPosition.y && isKingAttackedFromDirection(kingPosition, Direction.Top)) {
                return reverseMoveAndReturnTrue();
            }

            else if (source.y < kingPosition.y && isKingAttackedFromDirection(kingPosition, Direction.UpperLeft)) {
                return reverseMoveAndReturnTrue();
            }

            else if (isKingAttackedFromDirection(kingPosition, Direction.UpperRight))  // source.y > kingPosition.y
            {
                return reverseMoveAndReturnTrue();
            }
        }

        moveSimulator.reverseMove();
        return false;
    }

    boolean isKingInCheck(Colour kingColour) {
        setKingColour(kingColour);

        var kingPosition = board.getKingPosition(kingColour);
        for (Direction dir : Direction.values()) {
            if (isKingAttackedFromDirection(kingPosition, dir)) {
                return true;
            }
        }

        Set<Square> knightSquares = new HashSet<>() {{
            add(new Square(kingPosition.x + 2, kingPosition.y - 1));
            add(new Square(kingPosition.x + 2, kingPosition.y + 1));
            add(new Square(kingPosition.x - 2, kingPosition.y - 1));
            add(new Square(kingPosition.x - 2, kingPosition.y + 1));
            add(new Square(kingPosition.x + 1, kingPosition.y - 2));
            add(new Square(kingPosition.x - 1, kingPosition.y - 2));
            add(new Square(kingPosition.x + 1, kingPosition.y + 2));
            add(new Square(kingPosition.x - 1, kingPosition.y + 2));
        }};
        knightSquares.removeIf(Board::isOutOfBoardPosition);
        knightSquares.removeIf(square -> {
            var piece = board.getPiece(square);
            return piece == null || !isKingAttackedFromPiece(piece, Knight.class);
        });
        if (knightSquares.size() > 0) {
            return true;
        }

        var rowToAdd = kingColour == Colour.White ? 1 : -1;
        Square[] pawnSquares = {new Square(kingPosition.x + rowToAdd, kingPosition.y - 1),
                new Square(kingPosition.x + rowToAdd, kingPosition.y + 1)};

        for (int i = 0; i < 2; ++i) {
            if (!Board.isOutOfBoardPosition(pawnSquares[i])) {
                var piece = board.getPiece(pawnSquares[i]);
                if ((piece != null && isKingAttackedFromPiece(piece, Pawn.class))) {
                    return true;
                }
            }
        }

        Set<Square> kingSquares = new HashSet<>() {{
            add(new Square(kingPosition.x + 1, kingPosition.y - 1));
            add(new Square(kingPosition.x + 1, kingPosition.y));
            add(new Square(kingPosition.x + 1, kingPosition.y + 1));
            add(new Square(kingPosition.x, kingPosition.y + 1));
            add(new Square(kingPosition.x, kingPosition.y - 1));
            add(new Square(kingPosition.x - 1, kingPosition.y + 1));
            add(new Square(kingPosition.x - 1, kingPosition.y));
            add(new Square(kingPosition.x - 1, kingPosition.y - 1));
        }};

        kingSquares.removeIf(Board::isOutOfBoardPosition);
        kingSquares.removeIf(square -> {
            var piece = board.getPiece(square);
            return piece == null || !isKingAttackedFromPiece(piece, King.class);
        });

        return kingSquares.size() > 0;
    }

    @SuppressWarnings("SameReturnValue")
    private boolean reverseMoveAndReturnTrue() {
        moveSimulator.reverseMove();
        return true;
    }

    private boolean isKingAttackedFromDirection(Square kingPosition, Direction dir) {
        switch (dir) {
            case Left:
                for (int i = kingPosition.y - 1; i >= 0; --i) {
                    var currPiece = board.getPiece(kingPosition.x, i);
                    if (currPiece != null) {
                        return isKingAttackedFromPiece(currPiece, Queen.class, Rook.class);
                    }
                }

                return false;
            case UpperLeft: {
                int i = kingPosition.x + 1;
                int j = kingPosition.y - 1;
                for (; i < Board.rowsNum && j >= 0; ++i, --j) {
                    var currPiece = board.getPiece(i, j);
                    if (currPiece != null) {
                        return isKingAttackedFromPiece(currPiece, Queen.class, Bishop.class);
                    }
                }

                return false;
            }
            case Top:
                for (int i = kingPosition.x + 1; i < Board.rowsNum; ++i) {
                    var currPiece = board.getPiece(i, kingPosition.y);
                    if (currPiece != null) {
                        return isKingAttackedFromPiece(currPiece, Queen.class, Rook.class);
                    }
                }

                return false;
            case UpperRight: {
                int i = kingPosition.x + 1;
                int j = kingPosition.y + 1;
                for (; i < Board.rowsNum && j < Board.columnsNum; ++i, ++j) {
                    var currPiece = board.getPiece(i, j);
                    if (currPiece != null) {
                        return isKingAttackedFromPiece(currPiece, Queen.class, Bishop.class);
                    }
                }

                return false;
            }
            case Right:
                for (int i = kingPosition.y + 1; i < Board.columnsNum; ++i) {
                    var currPiece = board.getPiece(kingPosition.x, i);
                    if (currPiece != null) {
                        return isKingAttackedFromPiece(currPiece, Queen.class, Rook.class);
                    }
                }

                return false;
            case LowerRight: {
                int i = kingPosition.x - 1;
                int j = kingPosition.y + 1;
                for (; i >= 0 && j < Board.columnsNum; --i, ++j) {
                    var currPiece = board.getPiece(i, j);
                    if (currPiece != null) {
                        return isKingAttackedFromPiece(currPiece, Queen.class, Bishop.class);
                    }
                }

                return false;
            }
            case Bottom:
                for (int i = kingPosition.x - 1; i >= 0; --i) {
                    var currPiece = board.getPiece(i, kingPosition.y);
                    if (currPiece != null) {
                        return isKingAttackedFromPiece(currPiece, Queen.class, Rook.class);
                    }
                }

                return false;
            case LowerLeft: {
                int i = kingPosition.x - 1;
                int j = kingPosition.y - 1;
                for (; i >= 0 && j >= 0; --i, --j) {
                    var currPiece = board.getPiece(i, j);
                    if (currPiece != null) {
                        return isKingAttackedFromPiece(currPiece, Queen.class, Bishop.class);
                    }
                }

                return false;
            }
        }

        return false;
    }

    private boolean isKingAttackedFromPiece(Piece piece, Class<?> enemy1, Class<?> enemy2) {
        return piece.colour != kingColour && (enemy1.isInstance(piece) || enemy2.isInstance(piece));
    }

    private boolean isKingAttackedFromPiece(Piece piece, Class<?> enemy) {
        return isKingAttackedFromPiece(piece, enemy, Integer.class);
    }

    private enum Direction {Left, UpperLeft, Top, UpperRight, Right, LowerRight, Bottom, LowerLeft}

}
