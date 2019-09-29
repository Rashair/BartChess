package model.pieces;

import model.Colour;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class KnightTest extends PieceTest {
    private Knight knight = new Knight(Colour.White);

    @Override
    Piece getTestedPiece() {
        return knight;
    }

    @Test
    @Override
    void allValidPositions() {
        assertThatResultMovesAreEqualExpected("f3",
                "e5", "g5", // top
                "h2", "h4", // right
                "e1", "g1", // bottom
                "d2", "d4"  // left
        );
    }

    @Test
    @DisplayName("Knight cannot move because of threat from enemy bishop")
    @Override
    void cannotEndangerKing() {
        setupKingForTests("a1", knight.colour);
        board.setPiece("h8", new Bishop(knight.colour.getOppositeColour()));

        assertThatResultMovesAreEqualExpected("b2");
    }
}
