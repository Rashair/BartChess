package model.pieces;

import model.Colour;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueenTest extends PieceTest {
    private final Queen queen;

    QueenTest() {
        super(Colour.White);
        queen = new Queen(ally);
    }

    @Override
    Piece getTestedPiece() {
        return queen;
    }

    @Test
    @Override
    void allValidPositions() {
        assertThatResultMovesAreEqualExpected("e5",
                "e6", "e7", "e8",       // Top
                "f6", "g7", "h8",       // Diagonal top-right
                "f5", "g5", "h5",       // Right
                "f4", "g3", "h2",       // Diagonal bottom-right
                "d5", "c5", "b5", "a5", // Bottom
                "d4", "c3", "b2", "a1", // Diagonal bottom-left
                "e4", "e3", "e2", "e1", // Left
                "d6", "c7", "b8"        // Diagonal top-left
        );
    }

    @Test
    @DisplayName("Queen in right-lower corner")
    @Override
    void cannotCrossChessboardBorders() {
        assertThatResultMovesAreEqualExpected("h1",
                "h2", "h3", "h4", "h5", "h6", "h7", "h8",   // Top
                "g1", "f1", "e1", "d1", "c1", "b1", "a1",   // Left
                "g2", "f3", "e4", "d5", "c6", "b7", "a8"    // Diagonal upper-left
        );
    }

    @Test
    @DisplayName("Queen cannot move from anti-diagonal")
    @Override
    void cannotEndangerKing() {
        setupKingForTests("d4", ally);
        board.setPiece("g7", new Bishop(enemy));

        assertThatResultMovesAreEqualExpected("e5",
                "f6", "g7" // Diagonal top-right
        );
    }

    @Test
    @DisplayName("Queen can only move to kill enemy rook")
    @Override
    void canOnlyKillThreatToKing() {
        setupKingForTests("f3", ally);
        board.setPiece("f6", new Rook(enemy));   // Threat to king
        state.setCheck(ally, true);

        assertThatResultMovesAreEqualExpected("c3",
                "f6" // Kill the rook
        );
    }

    @Test
    @DisplayName("Queen can only move to hide king from rook attack range")
    @Override
    void canOnlyProtectKingFromThreat() {
        setupKingForTests("g3", ally);
        board.setPiece("c7", new Bishop(enemy));   // Threat to king
        state.setCheck(ally, true);

        assertThatResultMovesAreEqualExpected("b2",
                "e5" // Hide king from rook attack range
        );
    }
}
