package model.pieces;

import model.Colour;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class KnightTest extends PieceTest {
    private final Knight knight;

    KnightTest() {
        super(Colour.White);
        knight = new Knight(ally);
    }

    @Override
    Piece getTestedPiece() {
        return knight;
    }

    @Test
    @Override
    void allValidPositions() {
        setupKingForTests("a8", ally); // non-meaningful position
        assertThatResultMovesAreEqualExpected("f3",
                "e5", "g5", // Top
                "h2", "h4", // Right
                "e1", "g1", // Bottom
                "d2", "d4"  // Left
        );
    }

    @Test
    @DisplayName("Knight at b8")
    @Override
    void cannotCrossChessboardBorders() {
        setupKingForTests("a8", ally); // non-meaningful position
        assertThatResultMovesAreEqualExpected("b8",
                "d7",       // Right
                "a6", "c6"  // Bottom
        );
    }

    @Test
    @DisplayName("Knight cannot move because of threat from enemy bishop")
    @Override
    void cannotEndangerKing() {
        setupKingForTests("a1", ally);
        board.setPiece("h8", new Bishop(enemy));

        assertThatResultMovesAreEqualExpected("b2");
    }

    @Test
    @DisplayName("Knight can only move to kill enemy pawn")
    @Override
    void canOnlyKillThreatToKing() {
        setupKingForTests("b4", ally);
        board.setPiece("a5", new Pawn(enemy));   // Threat to king
        state.setCheck(ally, true);

        assertThatResultMovesAreEqualExpected("b3",
                "a5" // Kill the pawn
        );
    }

    @Test
    @DisplayName("Knight can only move to hide king from enemy rook attack range")
    @Override
    void canOnlyProtectKingFromThreat() {
        setupKingForTests("b4", ally);
        board.setPiece("e4", new Rook(enemy));   // Threat to king
        state.setCheck(ally, true);

        assertThatResultMovesAreEqualExpected("b3",
                "d4" // Hide king from enemy rook attack range
        );
    }
}
