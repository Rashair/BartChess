package model.pieces;

import model.Colour;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PawnTest extends PieceTest {
    private Pawn pawn = new Pawn(Colour.Black);

    @Override
    Piece getTestedPiece() {
        return pawn;
    }

    @Test
    @Override
    void allValidPositions() {
        assertThatResultMovesAreEqualExpected("b7",
                "b6", "b5" // first move
        );
    }

    @Override
    void cannotCrossChessboardBorders() {

    }

    @Test
    @DisplayName("Pawn cannot move because of threat from enemy bishop")
    @Override
    void cannotEndangerKing() {
        setupKingForTests("b4", ally);
        board.setPiece("f8", new Bishop(enemy));

        assertThatResultMovesAreEqualExpected("d6");
    }

    @Override
    void canOnlyKillThreatToKing() {

    }

    @Override
    void canOnlyProtectKingFromThreat() {

    }
}
