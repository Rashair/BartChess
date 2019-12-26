package model.pieces;

import model.Colour;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PawnTest extends PieceTest {
    private final Pawn pawn;

    PawnTest() {
        super(Colour.Black);
        pawn = new Pawn(ally);
    }

    @Override
    Piece getTestedPiece() {
        return pawn;
    }

    @Test
    @Override
    void allValidPositions() {
        setupKingForTests("a8", ally); // non-meaningful position
        assertThatResultMovesAreEqualExpected("b7",
                "b6", "b5" // First move
        );
    }

    @Test
    @DisplayName("Black pawn at a1 (pretty rare situation)")
    @Override
    void cannotCrossChessboardBorders() {
        assertThatResultMovesAreEqualExpected("a1");
    }

    @Test
    @DisplayName("Pawn cannot move because of threat from enemy bishop")
    @Override
    void cannotEndangerKing() {
        setupKingForTests("b4", ally);
        board.setPiece("f8", new Bishop(enemy));

        assertThatResultMovesAreEqualExpected("d6");
    }

    @Test
    @DisplayName("Pawn can only move to kill enemy knight")
    @Override
    void canOnlyKillThreatToKing() {
        setupKingForTests("b6", ally);
        board.setPiece("c4", new Knight(enemy));   // Threat to king
        state.setCheck(ally, true);

        assertThatResultMovesAreEqualExpected("d5",
                "c4" // Kill the knight
        );
    }

    @Test
    @DisplayName("Pawn can only move to hide king from bishop attack range")
    @Override
    void canOnlyProtectKingFromThreat() {
        setupKingForTests("d5", ally);
        board.setPiece("b3", new Bishop(enemy));   // Threat to king
        state.setCheck(ally, true);

        assertThatResultMovesAreEqualExpected("c5",
                "c4" // Hide king from bishop attack range
        );
    }
}
