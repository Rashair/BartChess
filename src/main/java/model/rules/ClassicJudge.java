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
    
    private boolean isKingAttackedAfterOwnPieceMove(Move move) {
        var source = move.getSource();
        var destination = move.getDestination();
        var movedPiece = board.getPiece(source);
        var destinationPiece = board.getPiece(destination);

        board.movePiece(move);

        var kingColour = movedPiece.colour;
        var kingPosition = board.getKingPosition(kingColour);

        var isKingAttacked = false;
        if (kingPosition == destination) { // King has moved
            isKingAttacked = isKingAttackedFromTop(kingColour, kingPosition) ||
                    isKingAttackedFromBottom(kingColour, kingPosition) ||
                    isKingAttackedFromLeft(kingColour, kingPosition) ||
                    isKingAttackedFromRight(kingColour, kingPosition);
            if (!isKingAttacked) {
                isKingAttacked = isKingAttackedFromBottomLeft(kingColour, kingPosition) ||
                        isKingAttackedFromBottomRight(kingColour, kingPosition) ||
                        isKingAttackedFromUpperLeft(kingColour, kingPosition) ||
                        isKingAttackedFromUpperRight(kingColour, kingPosition);
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
                    return piece == null || !isKingAttackedFromPiece(kingColour, piece, Knight.class);
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
                                (piece != null && isKingAttackedFromPiece(kingColour, piece, Pawn.class));
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
                    return piece == null || !isKingAttackedFromPiece(kingColour, piece, King.class);
                });

                isKingAttacked = kingSquares.size() > 0;
            }
        } else if (source.x == kingPosition.x) {
            if (source.y < kingPosition.y) {
                isKingAttacked = isKingAttackedFromLeft(kingColour, kingPosition);
            } else { // source.y > kingPosition.y
                isKingAttacked = isKingAttackedFromRight(kingColour, kingPosition);
            }
        } else if (source.x < kingPosition.x) {
            if (source.y == kingPosition.y) {
                isKingAttacked = isKingAttackedFromBottom(kingColour, kingPosition);
            } else if (source.y < kingPosition.y) {
                isKingAttacked = isKingAttackedFromBottomLeft(kingColour, kingPosition);
            } else { // source.y > kingPosition.y
                isKingAttacked = isKingAttackedFromBottomRight(kingColour, kingPosition);
            }
        } else { // source.x > kingPosition.x
            if (source.y == kingPosition.y) {
                isKingAttacked = isKingAttackedFromTop(kingColour, kingPosition);
            } else if (source.y < kingPosition.y) {
                isKingAttacked = isKingAttackedFromUpperLeft(kingColour, kingPosition);
            } else { // source.y > kingPosition.y
                isKingAttacked = isKingAttackedFromUpperRight(kingColour, kingPosition);
            }
        }

        board.movePiece(destination, source);
        board.setPiece(destination, destinationPiece);
        return isKingAttacked;
    }


    private boolean isKingAttackedFromTop(Colour kingColour, Square kingPosition) {
        for (int i = kingPosition.x + 1; i < Board.rowsNum; ++i) {
            var currPiece = board.getPiece(i, kingPosition.y);
            if (currPiece != null) {
                return isKingAttackedFromPiece(kingColour, currPiece, Queen.class, Rook.class);
            }
        }

        return false;
    }

    private boolean isKingAttackedFromBottom(Colour kingColour, Square kingPosition) {
        for (int i = kingPosition.x - 1; i >= 0; --i) {
            var currPiece = board.getPiece(i, kingPosition.y);
            if (currPiece != null) {
                return isKingAttackedFromPiece(kingColour, currPiece, Queen.class, Rook.class);
            }
        }

        return false;
    }

    private boolean isKingAttackedFromLeft(Colour kingColour, Square kingPosition) {
        for (int i = kingPosition.y - 1; i >= 0; --i) {
            var currPiece = board.getPiece(kingPosition.x, i);
            if (currPiece != null) {
                return isKingAttackedFromPiece(kingColour, currPiece, Queen.class, Rook.class);
            }
        }

        return false;
    }

    private boolean isKingAttackedFromRight(Colour kingColour, Square kingPosition) {
        for (int i = kingPosition.y + 1; i < Board.columnsNum; ++i) {
            var currPiece = board.getPiece(kingPosition.x, i);
            if (currPiece != null) {
                return isKingAttackedFromPiece(kingColour, currPiece, Queen.class, Rook.class);
            }
        }

        return false;
    }

    private boolean isKingAttackedFromUpperRight(Colour kingColour, Square kingPosition) {
        int i = kingPosition.x + 1;
        int j = kingPosition.y + 1;
        for (; i < Board.rowsNum && j < Board.columnsNum; ++i, ++j) {
            var currPiece = board.getPiece(i, j);
            if (currPiece != null) {
                return isKingAttackedFromPiece(kingColour, currPiece, Queen.class, Bishop.class);
            }
        }

        return false;
    }

    private boolean isKingAttackedFromUpperLeft(Colour kingColour, Square kingPosition) {
        int i = kingPosition.x + 1;
        int j = kingPosition.y - 1;
        for (; i < Board.rowsNum && j >= 0; ++i, --j) {
            var currPiece = board.getPiece(i, j);
            if (currPiece != null) {
                return isKingAttackedFromPiece(kingColour, currPiece, Queen.class, Bishop.class);
            }
        }

        return false;
    }

    private boolean isKingAttackedFromBottomLeft(Colour kingColour, Square kingPosition) {
        int i = kingPosition.x - 1;
        int j = kingPosition.y - 1;
        for (; i >= 0 && j >= 0; --i, --j) {
            var currPiece = board.getPiece(i, j);
            if (currPiece != null) {
                return isKingAttackedFromPiece(kingColour, currPiece, Queen.class, Bishop.class);
            }
        }

        return false;
    }

    private boolean isKingAttackedFromBottomRight(Colour kingColour, Square kingPosition) {
        int i = kingPosition.x - 1;
        int j = kingPosition.y + 1;
        for (; i >= 0 && j < Board.columnsNum; --i, ++j) {
            var currPiece = board.getPiece(i, j);
            if (currPiece != null) {
                return isKingAttackedFromPiece(kingColour, currPiece, Queen.class, Bishop.class);
            }
        }

        return false;
    }

    private boolean isKingAttackedFromPiece(Colour kingColour, Piece piece, Class<?> enemy1, Class<?> enemy2) {
        return piece.colour != kingColour && (enemy1.isInstance(piece) || enemy2.isInstance(piece));
    }

    private boolean isKingAttackedFromPiece(Colour kingColour, Piece piece, Class<?> enemy1) {
        return isKingAttackedFromPiece(kingColour, piece, enemy1, Integer.class);
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

        var result = Move.createMovesFromSource(new Square(x, y), possiblePositions.toArray(Square[]::new));
        result.removeIf(this::isKingAttackedAfterOwnPieceMove);

        return result;
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

