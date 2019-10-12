package model.pieces;

import model.Colour;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class QueenTest extends PieceTest {
    private Queen queen = new Queen(Colour.White);

    @Override
    Piece getTestedPiece() {
        return queen;
    }

    @Test
    @Override
    void allValidPositions() {
        assertThatResultMovesAreEqualExpected("e5",
                "e6", "e7", "e8",       // top
                "f6", "g7", "h8",       // diagonal top-right
                "f5", "g5", "h5",       // right
                "f4", "g3", "h2",       // diagonal bottom-right
                "d5", "c5", "b5", "a5", // bottom
                "d4", "c3", "b2", "a1", // diagonal bottom-left
                "e4", "e3", "e2", "e1", // left
                "d6", "c7", "b8"        // diagonal top-left
        );
    }

    @Override
    void cannotCrossChessboardBorders() {

    }

    @Test
    @DisplayName("Queen cannot move from anti-diagonal")
    @Override
    void cannotEndangerKing() {
        setupKingForTests("d4", queen.colour);
        board.setPiece("g7", new Bishop(queen.colour.getOppositeColour()));

        assertThatResultMovesAreEqualExpected("e5",
                "f6", "g7" // diagonal top-right
        );
    }

    @Override
    void canOnlyKillThreatToKing() {

    }

    @Override
    void canOnlyProtectKingFromThreat() {

    }
}
