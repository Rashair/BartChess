package model.rules;

import model.Colour;
import model.pieces.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

class PiecesPositionInitializer {
    private static Map<Pair<Class<? extends Piece>, Colour>, String> initialPositions;

    static Map<Pair<Class<? extends Piece>, Colour>, String> getInitialPositions() {
        if (initialPositions != null) {
            return initialPositions;
        }

        initialPositions = new HashMap<>();
        initializePawns();
        initializeKnights();
        initializeBishops();
        initializeRooks();
        initializeQueens();
        initializeKings();

        return initialPositions;
    }

    private static void initializePawns() {
        Pair<Class<? extends Piece>, Colour> whitePawn = new ImmutablePair<>(Pawn.class, Colour.White);
        Pair<Class<? extends Piece>, Colour> blackPawn = new ImmutablePair<>(Pawn.class, Colour.Black);
        for (char y = 'a'; y <= 'h'; ++y) {
            initialPositions.put(whitePawn, y + "2");
            initialPositions.put(blackPawn, y + "7");
        }
    }

    private static void initializeKnights() {
        Pair<Class<? extends Piece>, Colour> whiteKnight = new ImmutablePair<>(Knight.class, Colour.White);
        initialPositions.put(whiteKnight, "b1");
        initialPositions.put(whiteKnight, "g1");

        Pair<Class<? extends Piece>, Colour> blackKnight = new ImmutablePair<>(Knight.class, Colour.Black);
        initialPositions.put(blackKnight, "b8");
        initialPositions.put(blackKnight, "g8");
    }

    private static void initializeBishops() {
        Pair<Class<? extends Piece>, Colour> whiteBishop = new ImmutablePair<>(Bishop.class, Colour.White);
        initialPositions.put(whiteBishop, "c1");
        initialPositions.put(whiteBishop, "f1");

        Pair<Class<? extends Piece>, Colour> blackBishop = new ImmutablePair<>(Bishop.class, Colour.Black);
        initialPositions.put(blackBishop, "c8");
        initialPositions.put(blackBishop, "f8");
    }

    private static void initializeRooks() {
        Pair<Class<? extends Piece>, Colour> whiteRook = new ImmutablePair<>(Rook.class, Colour.White);
        initialPositions.put(whiteRook, "a1");
        initialPositions.put(whiteRook, "h1");

        Pair<Class<? extends Piece>, Colour> blackRook = new ImmutablePair<>(Rook.class, Colour.Black);
        initialPositions.put(blackRook, "a8");
        initialPositions.put(blackRook, "h8");
    }

    private static void initializeQueens() {
        Pair<Class<? extends Piece>, Colour> whiteQueen = new ImmutablePair<>(Queen.class, Colour.White);
        initialPositions.put(whiteQueen, "d1");

        Pair<Class<? extends Piece>, Colour> blackQueen = new ImmutablePair<>(Queen.class, Colour.Black);
        initialPositions.put(blackQueen, "d8");
    }

    private static void initializeKings() {
        Pair<Class<? extends Piece>, Colour> whiteKing = new ImmutablePair<>(King.class, Colour.White);
        initialPositions.put(whiteKing, "e1");

        Pair<Class<? extends Piece>, Colour> blackKing = new ImmutablePair<>(King.class, Colour.Black);
        initialPositions.put(blackKing, "e8");
    }
}
