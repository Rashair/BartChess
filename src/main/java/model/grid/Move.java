package model.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Move implements Comparable<Move> {
    private final Square source;
    private final Square destination;

    public Move(Square source, Square destination) {
        this.source = source;
        this.destination = destination;
    }

    public Square getSource() {
        return source;
    }

    public Square getDestination() {
        return destination;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Move))
            return false;

        Move move = (Move) obj;
        return source.equals(move.source) && destination.equals(move.destination);
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

    public static List<Move> createMovesFromSource(String source, String... destinations) {
        Square sourceSquare = Board.parsePosition(source);
        List<Move> moves = new ArrayList<>(destinations.length);
        for (String dest : destinations) {
            moves.add(new Move(sourceSquare, Board.parsePosition(dest)));
        }

        return moves;
    }

    public static List<Move> createMovesFromSource(Square source, Square[] destinations) {
        List<Move> moves = new ArrayList<>(destinations.length);
        for (Square dest : destinations) {
            moves.add(new Move(source, dest));
        }

        return moves;
    }
}
