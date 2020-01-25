package model.grid;

import javafx.util.Pair;
import model.Colour;
import model.pieces.*;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
    public static final int rowsNum = 8;
    public static final int columnsNum = 8;

    private final PieceFactory pieceFactory;
    private final Piece[][] pieces;

    private final Square[] kingPosition;
    private final Map<Piece, Move> firstPieceMove;
    private MoveData lastMoveData;

    public Board(PieceFactory pieceFactory) {
        this.pieceFactory = pieceFactory;

        pieces = new Piece[rowsNum][columnsNum];
        kingPosition = new Square[Colour.getNumberOfColours()];
        firstPieceMove = new HashMap<>();
    }

    /**
     * @param pos - must be in lowercase
     * @return Square representing position on chessboard
     */
    public static Square parsePosition(String pos) {
        int x = pos.charAt(1) - '1';
        int y = pos.charAt(0) - 'a';

        if (isOutOfBoardPosition(x, y)) {
            throw new IllegalArgumentException("Provided string is invalid");
        }

        return new Square(x, y);
    }

    public static boolean isOutOfBoardPosition(int x, int y) {
        return x < 0 || x >= rowsNum ||
                y < 0 || y >= columnsNum;
    }

    public static boolean isOutOfBoardPosition(Square square) {
        return isOutOfBoardPosition(square.x, square.y);
    }

    public void initializePieces(List<Triple<Class<? extends Piece>, Colour, String>> piecesToPositions) {
        for (var entry : piecesToPositions) {
            Square position = parsePosition(entry.getRight());
            Piece piece = pieceFactory.create(entry.getLeft(), entry.getMiddle());

            pieces[position.x][position.y] = piece;
            if (piece instanceof King) {
                kingPosition[piece.colour.getIntValue()] = position;
                firstPieceMove.put(piece, null);
            }
            else if (piece instanceof Rook) {
                firstPieceMove.put(piece, null);
            }
        }
    }

    public void movePiece(Move move) {
        var piece = move.getMovedPiece();
        var from = move.getSource();
        var to = move.getDestination();

        if (piece == null) {
            throw new IllegalArgumentException("You cannot move empty field");
        }

        lastMoveData = new MoveData(move, getPiece(to));
        atomicMove(from, to, piece);

        if (move.isEnPassantMove()) {
            setPiece(from.x, to.y, null);
            lastMoveData.setKilledPiecePosition(new Square(from.x, to.y));
        }
        else if (move.isCastlingMove()) { // Only possible in king move
            Rook rook;
            Square source;
            if (to.y > from.y) {  // Move to the right
                source = new Square(from.x, columnsNum - 1);
                rook = (Rook) getPiece(source);
                setPiece(source, null);
                setPiece(from.x, to.y - 1, rook);
            }
            else { // Move to the left
                source = new Square(from.x, 0);
                rook = (Rook) getPiece(source);
                setPiece(source, null);
                setPiece(from.x, to.y + 1, rook);
            }

            firstPieceMove.replace(rook, move);
            lastMoveData.setMovedRookDataOnCastling(new Pair<>(rook, source));
        }

        if (piece instanceof King) {
            kingPosition[piece.colour.getIntValue()] = to;
            if (firstPieceMove.get(piece) == null) {
                firstPieceMove.replace(piece, move);
                lastMoveData.setFirstPieceMove();
            }
        }
        else if (piece instanceof Rook) {
            var rook = (Rook) piece;
            // If pawn was promoted to rook then condition can be false
            if (firstPieceMove.getOrDefault(rook, move) == null) {
                firstPieceMove.replace(rook, move);
                lastMoveData.setFirstPieceMove();
            }
        }
    }

    public void movePiece(String from, String to) {
        movePiece(parsePosition(from), parsePosition(to));
    }

    private void movePiece(Square from, Square to) {
        var move = new Move(from, to, getPiece(from));
        movePiece(move);
    }

    private void atomicMove(Square from, Square to, Piece piece) {
        setPiece(from, null);
        setPiece(to, piece);
    }

    private void setPiece(int x, int y, Piece piece) {
        pieces[x][y] = piece;
    }

    public void setPiece(Square pos, Piece piece) {
        setPiece(pos.x, pos.y, piece);
    }

    public void setPiece(String pos, Piece piece) {
        setPiece(parsePosition(pos), piece);
    }

    public Piece getPiece(Square p) {
        return getPiece(p.x, p.y);
    }

    public Piece getPiece(int x, int y) {
        return isOutOfBoardPosition(x, y) ? null : pieces[x][y];
    }

    public Piece getPiece(String pos) {
        return getPiece(parsePosition(pos));
    }

    public void promotePiece(Square pos, Class<? extends Piece> updatedClass) {
        var oldPiece = getPiece(pos);
        if (!(oldPiece instanceof Pawn)) {
            throw new IllegalArgumentException("You cannot promote piece other than pawn");
        }

        setPiece(pos, pieceFactory.create(updatedClass, oldPiece.colour));
    }

    /**
     * @param s - square
     * @return If is position inside board and if this position is empty
     */
    public boolean isEmptySquare(Square s) {
        return isEmptySquare(s.x, s.y);
    }

    public boolean isEmptySquare(int x, int y) {
        return !isOutOfBoardPosition(x, y) && getPiece(x, y) == null;
    }

    public Square getKingPosition(Colour colour) {
        return kingPosition[colour.getIntValue()];
    }

    public boolean wasKingMoved(King king) {
        return firstPieceMove.get(king) != null;
    }

    public boolean wasRookMoved(Rook rook) {
        // Possible if pawn was promoted to rook
        if (!firstPieceMove.containsKey(rook)) {
            return true;
        }

        return firstPieceMove.get(rook) != null;
    }

    public boolean areFieldsBetweenEmpty(int row, int colSmaller, int colGreater) {
        for (int y = colSmaller + 1; y < colGreater; ++y) {
            if (getPiece(row, y) != null) {
                return false;
            }
        }

        return true;
    }

    public MoveData getLastMoveData() {
        return lastMoveData;
    }

    public Move getLastMove() {
        return lastMoveData != null ? lastMoveData.getMove() : null;
    }

    public void reverseLastMove(MoveData previousMoveData) {
        var move = lastMoveData.getMove();
        if (move.isPromotionMove()) {
            setPiece(move.getDestination(), move.getMovedPiece());
        }

        var piece = move.getMovedPiece();
        atomicMove(move.getDestination(), move.getSource(), piece);
        Pair<Piece, Square> killedPieceData = lastMoveData.getKilledPieceData();
        setPiece(killedPieceData.getValue(), killedPieceData.getKey());

        if (piece instanceof King) {
            kingPosition[piece.colour.getIntValue()] = move.getSource();
        }

        if (move.isCastlingMove()) {
            Pair<Rook, Square> data = lastMoveData.getMovedRookDataOnCastling();
            setPiece(data.getValue(), data.getKey());
            firstPieceMove.replace(data.getKey(), null);
        }

        if (lastMoveData.wasFirstPieceMove()) {
            firstPieceMove.replace(piece, null);
        }

        lastMoveData = previousMoveData;
    }

    public void clear() {
        for (int rowIt = 0; rowIt < rowsNum; ++rowIt) {
            for (int colIt = 0; colIt < columnsNum; ++colIt) {
                pieces[rowIt][colIt] = null;
            }
        }

        lastMoveData = null;
        firstPieceMove.clear();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = rowsNum - 1; i >= 0; --i) {
            for (int j = 0; j < columnsNum; ++j) {
                var piece = pieces[i][j];
                if (piece != null) {
                    builder.append(piece.toString());
                }
                else {
                    builder.append('\u2003');
                }
            }
            builder.append('\n');
        }

        return builder.toString();
    }
}
