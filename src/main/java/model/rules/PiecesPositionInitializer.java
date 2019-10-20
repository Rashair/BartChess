package model.rules;

import model.Colour;
import model.grid.Board;
import model.pieces.*;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;

class PiecesPositionInitializer {
    private static List<Triple<Class<? extends Piece>, Colour, String>> initialPositions;

    static List<Triple<Class<? extends Piece>, Colour, String>> getInitialPositions() {
        if (initialPositions != null) {
            return initialPositions;
        }

        initialPositions = new ArrayList<>(4 * Board.columnsNum);
        initializePawns();
        initializeKnights();
        initializeBishops();
        initializeRooks();
        initializeQueens();
        initializeKings();

        return initialPositions;
    }

    private static void initializePawns() {
        for (char y = 'a'; y <= 'h'; ++y) {
            initialPositions.add(new ImmutableTriple<>(Pawn.class, Colour.White, y + "2"));
            initialPositions.add(new ImmutableTriple<>(Pawn.class, Colour.Black, y + "7"));
        }
    }

    private static void initializeKnights() {
        initialPositions.add(new ImmutableTriple<>(Knight.class, Colour.White, "b1"));
        initialPositions.add(new ImmutableTriple<>(Knight.class, Colour.White, "g1"));

        initialPositions.add(new ImmutableTriple<>(Knight.class, Colour.Black, "b8"));
        initialPositions.add(new ImmutableTriple<>(Knight.class, Colour.Black, "g8"));
    }

    private static void initializeBishops() {
        initialPositions.add(new ImmutableTriple<>(Bishop.class, Colour.White, "c1"));
        initialPositions.add(new ImmutableTriple<>(Bishop.class, Colour.White, "f1"));

        initialPositions.add(new ImmutableTriple<>(Bishop.class, Colour.Black, "c8"));
        initialPositions.add(new ImmutableTriple<>(Bishop.class, Colour.Black, "f8"));
    }

    private static void initializeRooks() {
        initialPositions.add(new ImmutableTriple<>(Rook.class, Colour.White, "a1"));
        initialPositions.add(new ImmutableTriple<>(Rook.class, Colour.White, "h1"));

        initialPositions.add(new ImmutableTriple<>(Rook.class, Colour.Black, "a8"));
        initialPositions.add(new ImmutableTriple<>(Rook.class, Colour.Black, "h8"));
    }

    private static void initializeQueens() {
        initialPositions.add(new ImmutableTriple<>(Queen.class, Colour.White, "d1"));

        initialPositions.add(new ImmutableTriple<>(Queen.class, Colour.Black, "d8"));
    }

    private static void initializeKings() {
        initialPositions.add(new ImmutableTriple<>(King.class, Colour.White, "e1"));

        initialPositions.add(new ImmutableTriple<>(King.class, Colour.Black, "e8"));
    }
}
