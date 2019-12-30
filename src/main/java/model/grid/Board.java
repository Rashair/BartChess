package model.grid;

import model.Colour;
import model.pieces.King;
import model.pieces.Piece;
import model.pieces.PieceFactory;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

public class Board {
    public static final int rowsNum = 8;
    public static final int columnsNum = 8;

    private final PieceFactory pieceFactory;
    private final Piece[][] pieces;
    private final Square[] kingPositions = new Square[Colour.getNumberOfColours()];
    private Move lastMove;

    public Board(PieceFactory pieceFactory) {
        pieces = new Piece[rowsNum][columnsNum];
        this.pieceFactory = pieceFactory;
    }

    public void initializePieces(List<Triple<Class<? extends Piece>, Colour, String>> piecesToPositions) {
        for (var entry : piecesToPositions) {
            Square position = parsePosition(entry.getRight());
            Piece piece = pieceFactory.create(entry.getLeft(), entry.getMiddle());

            pieces[position.x][position.y] = piece;
            if (piece instanceof King) {
                kingPositions[piece.colour.getIntValue()] = position;
            }
        }
    }

    public void movePiece(Move move) {
        var piece = move.getMovedPiece();
        var from = move.getSource();
        var to = move.getDestination();

        if (piece == null)
            throw new IllegalArgumentException("You cannot move empty field");

        setPiece(from, null);
        setPiece(to, piece);
        if (move.isEnPassantMove()) {
            setPiece(from.x, to.y, null);
        }

        if (piece instanceof King)
            kingPositions[piece.colour.getIntValue()] = to;

        lastMove = move;
    }

    public void movePiece(String from, String to) {
        movePiece(parsePosition(from), parsePosition(to));
    }

    private void movePiece(Square from, Square to) {
        var move = new Move(from, to, getPiece(from));
        movePiece(move);
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
        return kingPositions[colour.getIntValue()];
    }

    public Move getLastMove() {
        return lastMove;
    }

    public void setLastMove(Move move) {
        this.lastMove = move;
    }

    public void clear() {
        for (int rowIt = 0; rowIt < rowsNum; ++rowIt) {
            for (int colIt = 0; colIt < columnsNum; ++colIt) {
                pieces[rowIt][colIt] = null;
            }
        }
        lastMove = null;
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

    /**
     * @param pos - must be in lowercase
     * @return Square representing position on chessboard
     */
    public static Square parsePosition(String pos) {
        int x = pos.charAt(1) - '1';
        int y = pos.charAt(0) - 'a';

        if (isOutOfBoardPosition(x, y))
            throw new IllegalArgumentException("Provided string is invalid");

        return new Square(x, y);
    }

    public static boolean isOutOfBoardPosition(int x, int y) {
        return x < 0 || x >= rowsNum ||
                y < 0 || y >= columnsNum;
    }

    public static boolean isOutOfBoardPosition(Square square) {
        return isOutOfBoardPosition(square.x, square.y);
    }
}
