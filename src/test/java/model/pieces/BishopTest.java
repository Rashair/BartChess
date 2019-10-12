package model.pieces;

import model.Colour;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BishopTest extends PieceTest {
    private final Bishop bishop;

    BishopTest() {
        super(Colour.White);
        bishop = new Bishop(ally);
    }

    @Override
    Piece getTestedPiece() {
        return bishop;
    }

    @Test
    @Override
    void allValidPositions() {
        assertThatResultMovesAreEqualExpected("d4",
                "e5", "f6", "g7", "h8", // Diagonal top-right
                "e3", "f2", "g1",       // Diagonal bottom-right
                "c3", "b2", "a1",       // Diagonal bottom-left
                "c5", "b6", "a7"        // Diagonal top-left
        );
    }

    @Test
    @DisplayName("Bishop in left-lower corner")
    @Override
    void cannotCrossChessboardBorders() {
        assertThatResultMovesAreEqualExpected("a1",
                "b2", "c3", "d4", "e5", "f6", "g7", "h8" // Diagonal top-right
        );
    }

    @Test
    @DisplayName("Bishop cannot move on anti-diagonal because of threat from enemy queen")
    @Override
    void cannotEndangerKing() {
        setupKingForTests("g3", ally);
        board.setPiece("b8", new Queen(enemy));

        assertThatResultMovesAreEqualExpected("e5",
                "f4",                   // Diagonal bottom-left
                "d6", "c7", "b8"        // Diagonal top-left
        );
    }

    @Test
    @DisplayName("Bishop can only move to kill enemy knight")
    @Override
    void canOnlyKillThreatToKing() {
        setupKingForTests("b2", ally);
        board.setPiece("c4", new Knight(enemy));
        state.setCheck(ally, true);

        assertThatResultMovesAreEqualExpected("f1",
                "c4" // Kill the knight
        );
    }


    @Test
    @DisplayName("Queen can only move to hide king from bishop attack range")
    @Override
    void canOnlyProtectKingFromThreat() {
        setupKingForTests("b4", ally);
        board.setPiece("f8", new Bishop(enemy));
        state.setCheck(ally, true);

        assertThatResultMovesAreEqualExpected("e3",
                "c5"
        );
    }
}
