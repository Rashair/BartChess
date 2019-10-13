package model.rules;

import model.Colour;
import model.GameState;
import model.grid.*;
import model.pieces.*;

import java.util.HashSet;
import java.util.Set;

class CheckValidator {
    private final Board board;
    private final GameState state;
    private Colour kingColour;

    CheckValidator(Board board, GameState state) {
        this.board = board;
        this.state = state;
    }

    private enum Direction {Left, UpperLeft, Top, UpperRight, Right, LowerRight, Bottom, LowerLeft}

    private void setKingColour(Move move) {
        kingColour = board.getPiece(move.getSource()).colour;
    }

    boolean isKingAttackedAfterMove(Move move) {
        setKingColour(move);

        var source = move.getSource();
        var destination = move.getDestination();
        var destinationPiece = board.getPiece(destination);
        board.movePiece(move);

        var kingPosition = board.getKingPosition(kingColour);

        var isKingAttacked = false;
        if (kingPosition == destination || state.isInCheck(kingColour)) {
            for (Direction dir : Direction.values()) {
                if (isKingAttackedFromDirection(kingPosition, dir)) {
                    isKingAttacked = true;
                    break;
                }
            }

            if (!isKingAttacked) {
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

                isKingAttacked = knightSquares.size() > 0;
            }
            if (!isKingAttacked) {
                var rowToAdd = kingColour == Colour.White ? 1 : -1;
                Square[] pawnSquares = {new Square(kingPosition.x + rowToAdd, kingPosition.y - 1),
                        new Square(kingPosition.x + rowToAdd, kingPosition.y + 1)};

                for (int i = 0; i < 2; ++i) {
                    if (!Board.isOutOfBoardPosition(pawnSquares[i])) {
                        var piece = board.getPiece(pawnSquares[i]);
                        isKingAttacked = isKingAttacked ||
                                (piece != null && isKingAttackedFromPiece(piece, Pawn.class));
                    }
                }
            }
            if (!isKingAttacked) {
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

                isKingAttacked = kingSquares.size() > 0;
            }
        } else if (source.x == kingPosition.x) {
            if (source.y < kingPosition.y) {
                isKingAttacked = isKingAttackedFromDirection(kingPosition, Direction.Left);
            } else { // source.y > kingPosition.y
                isKingAttacked = isKingAttackedFromDirection(kingPosition, Direction.Right);
            }
        } else if (source.x < kingPosition.x) {
            if (source.y == kingPosition.y) {
                isKingAttacked = isKingAttackedFromDirection(kingPosition, Direction.Bottom);
            } else if (source.y < kingPosition.y) {
                isKingAttacked = isKingAttackedFromDirection(kingPosition, Direction.LowerLeft);
            } else { // source.y > kingPosition.y
                isKingAttacked = isKingAttackedFromDirection(kingPosition, Direction.LowerRight);
            }
        } else { // source.x > kingPosition.x
            if (source.y == kingPosition.y) {
                isKingAttacked = isKingAttackedFromDirection(kingPosition, Direction.Top);
            } else if (source.y < kingPosition.y) {
                isKingAttacked = isKingAttackedFromDirection(kingPosition, Direction.UpperLeft);
            } else { // source.y > kingPosition.y
                isKingAttacked = isKingAttackedFromDirection(kingPosition, Direction.UpperRight);
            }
        }

        board.movePiece(destination, source);
        board.setPiece(destination, destinationPiece);
        return isKingAttacked;
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

    private boolean isKingAttackedFromPiece(Piece piece, Class<?> enemy1) {
        return isKingAttackedFromPiece(piece, enemy1, Integer.class);
    }

}
