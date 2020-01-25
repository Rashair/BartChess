package model.grid;

import model.pieces.Piece;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Move implements Comparable<Move> {
    private final Square source;
    private final Square destination;
    private final Piece movedPiece;

    private boolean isPromotionMove;
    private boolean isEnPassantMove;
    private boolean isCastlingMove;

    public Move(Square source, Square destination, Piece movedPiece) {
        this.source = source;
        this.destination = destination;
        this.movedPiece = movedPiece;

        isPromotionMove = false;
        isEnPassantMove = false;
        isCastlingMove = false;
    }

    public static HashSet<Move> createMovesFromSource(String source, Piece movedPiece, String... destinations) {
        Square sourceSquare = Board.parsePosition(source);
        HashSet<Move> moves = new HashSet<>(destinations.length);
        for (String dest : destinations) {
            moves.add(new Move(sourceSquare, Board.parsePosition(dest), movedPiece));
        }

        return moves;
    }

    public static HashSet<Move> createMovesFromSource(Square source, Piece movedPiece, Set<Square> destinations) {
        HashSet<Move> moves = new HashSet<>(destinations.size());
        for (Square dest : destinations) {
            moves.add(new Move(source, dest, movedPiece));
        }

        return moves;
    }

    public Square getSource() {
        return source;
    }

    public Square getDestination() {
        return destination;
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }

    public boolean isPromotionMove() {
        return isPromotionMove;
    }

    public void setPromotionMove() {
        isPromotionMove = true;
    }

    public boolean isEnPassantMove() {
        return isEnPassantMove;
    }

    public void setEnPassantMove() {
        isEnPassantMove = true;
    }

    public boolean isCastlingMove() {
        return isCastlingMove;
    }

    public void setCastlingMove() {
        isCastlingMove = true;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Move)) {
            return false;
        }

        Move move = (Move) obj;
        return source.equals(move.source) && destination.equals(move.destination) && movedPiece.equals(move.getMovedPiece());
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, destination);
    }

    @Override
    public int compareTo(Move o) {
        return source.equals(o.source) ?
                destination.compareTo(o.destination) :
                source.compareTo(o.source);
    }

    @Override
    public String toString() {
        return "[" + source.toString() + ", " + destination.toString() + "]";
    }
}
