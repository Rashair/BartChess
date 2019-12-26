package model.pieces;

import model.Colour;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RookTest extends PieceTest {
    private final Rook rook;

    RookTest() {
        super(Colour.White);
        rook = new Rook(ally);
    }

    @Override
    Piece getTestedPiece() {
        return rook;
    }

    @Test
    @Override
    void allValidPositions() {
        setupKingForTests("a8", ally); // non-meaningful position
        assertThatResultMovesAreEqualExpected("e5",
                "e6", "e7", "e8",       // Top
                "f5", "g5", "h5",       // Right
                "e4", "e3", "e2", "e1", // Bottom
                "a5", "b5", "c5", "d5"  // Left
        );
    }

    @Test
    @DisplayName("Rook in left-upper corner")
    @Override
    void cannotCrossChessboardBorders() {
        setupKingForTests("b1", ally); // non-meaningful position
        assertThatResultMovesAreEqualExpected("a8",
                "b8", "c8", "d8", "e8", "f8", "g8", "h8",    // Right
                "a7", "a6", "a5", "a4", "a3", "a2", "a1"     // Bottom
        );
    }

    @Test
    @DisplayName("Enemy rook threatens king if ally rook moves vertically")
    @Override
    void cannotEndangerKing() {
        setupKingForTests("a5", ally);

        board.setPiece("h5", new Rook(enemy));

        assertThatResultMovesAreEqualExpected("e5",
                "b5", "c5", "d5", // Left
                "f5", "g5", "h5"  // Right
        );
    }

    @Test
    @DisplayName("Rook can only kill enemy pawn")
    @Override
    void canOnlyKillThreatToKing() {
        setupKingForTests("e4", ally);
        board.setPiece("f5", new Pawn(enemy)); // Threat to king
        state.setCheck(ally, true);

        assertThatResultMovesAreEqualExpected("f1",
                "f5" // Kill the pawn
        );
    }

    @Test
    @DisplayName("Rook can only move to hide king from queen attack range")
    @Override
    void canOnlyProtectKingFromThreat() {
        setupKingForTests("a5", ally);
        board.setPiece("d8", new Queen(enemy)); // Threat to king
        state.setCheck(ally, true);

        assertThatResultMovesAreEqualExpected("h6",
                "b6" // Hide king from queen attack range
        );
    }
}
