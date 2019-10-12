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

    @Test
    @DisplayName("Queen in right-lower corner")
    @Override
    void cannotCrossChessboardBorders() {
        assertThatResultMovesAreEqualExpected("h1",
                "h2", "h3", "h4", "h5", "h6", "h7", "h8",   // top
                "g1", "f1", "e1", "d1", "c1", "b1", "a1",   // left
                "g2", "f3", "e4", "d5", "c6", "b7", "a8"    // diagonal upper-left
        );
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

    @Test
    @DisplayName("Queen can only move to kill enemy rook")
    @Override
    void canOnlyKillThreatToKing() {
        setupKingForTests("f3", queen.colour);
        board.setPiece("f6", new Rook(queen.colour.getOppositeColour()));   // Threat to king
        state.setCheck(queen.colour, true);

        assertThatResultMovesAreEqualExpected("c3",
                "f6" // Kill the rook
        );
    }

    @Test
    @DisplayName("Queen can only move to protect king from check")
    @Override
    void canOnlyProtectKingFromThreat() {
        setupKingForTests("g3", queen.colour);
        board.setPiece("c7", new Bishop(queen.colour.getOppositeColour()));   // Threat to king
        state.setCheck(queen.colour, true);

        assertThatResultMovesAreEqualExpected("b2",
                "e5" // Hide king from rook path
        );
    }
}
