package model.grid;

import model.Colour;
import model.pieces.King;
import model.pieces.Pawn;
import model.pieces.Piece;
import model.pieces.PieceFactory;

public class Board {
    public static final int rowsNum = 8;
    public static final int columnsNum = 8;

    private final PieceFactory factory;
    private final Piece[][] pieces;

    private final Square[] kingPosition = new Square[Colour.getNumberOfColours()];

    public Board(PieceFactory factory) {
        this.factory = factory;
        pieces = new Piece[rowsNum][columnsNum];


        setPiece("e1", factory.create(King.class, Colour.White));
        kingPosition[Colour.White.getIntValue()] = parsePosition("e1");

        setPiece("e8", factory.create(King.class, Colour.Black));
        kingPosition[Colour.Black.getIntValue()] = parsePosition("e8");


        setupPawns();
    }

    private void setupPawns() {
        int secondRow = 1;
        int penultimateRow = rowsNum - 2;
        for (int colIt = 0; colIt < columnsNum; ++colIt) {
            setPiece(secondRow, colIt, factory.create(Pawn.class, Colour.White));
            setPiece(penultimateRow, colIt, factory.create(Pawn.class, Colour.Black));
        }
    }

    public void movePiece(Square from, Square to) {
        var piece = getPiece(from);
        setPiece(from, null);
        setPiece(to, piece);

        if (piece instanceof King) {
            kingPosition[piece.colour.getIntValue()] = to;
        }
    }

    public void movePiece(Move move) {
        movePiece(move.getSource(), move.getDestination());
    }

    public void movePiece(String source, String destination) {
        movePiece(parsePosition(source), parsePosition(destination));
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
        return pieces[p.x][p.y];
    }

    public Piece getPiece(int x, int y) {
        return pieces[x][y];
    }

    public Piece getPiece(String pos) {
        return getPiece(parsePosition(pos));
    }

    public boolean isNotAnEmptySquare(int x, int y) {
        return !isOutOfBoardPosition(x, y) && getPiece(x, y) != null;
    }

    public Square getKingPosition(Colour colour) {
        return kingPosition[colour.getIntValue()];
    }

    public void clearAllPieces() {
        for (int rowIt = 0; rowIt < rowsNum; ++rowIt) {
            for (int colIt = 0; colIt < columnsNum; ++colIt) {
                pieces[rowIt][colIt] = null;
            }
        }
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
