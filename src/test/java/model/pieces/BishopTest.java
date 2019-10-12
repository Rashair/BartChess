package model.pieces;

import model.Colour;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BishopTest extends PieceTest {
   private Bishop bishop = new Bishop(Colour.White);

    @Override
    Piece getTestedPiece() {
        return bishop;
    }

    @Test
    @Override
    void allValidPositions() {
        assertThatResultMovesAreEqualExpected("d4",
                "e5", "f6", "g7", "h8", // diagonal top-right
                "e3", "f2", "g1",       // diagonal bottom-right
                "c3", "b2", "a1",       // diagonal bottom-left
                "c5", "b6", "a7"        // diagonal top-left
        );
    }

    @Override
    void cannotCrossChessboardBorders() {

    }

    @Test
    @DisplayName("Bishop cannot move on anti-diagonal because of threat from enemy queen")
    @Override
    void cannotEndangerKing() {
        setupKingForTests("g3", bishop.colour);
        board.setPiece("b8", new Queen(bishop.colour.getOppositeColour()));

        assertThatResultMovesAreEqualExpected("e5",
                "f4",                   // diagonal bottom-left
                "d6", "c7", "b8"        // diagonal top-left
        );
    }

    @Override
    void canOnlyKillThreatToKing() {

    }

    @Override
    void canOnlyProtectKingFromThreat() {

    }
}
