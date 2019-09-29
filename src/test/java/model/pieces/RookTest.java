package model.pieces;

import model.Colour;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class RookTest extends PieceTest {
    private Rook rook = new Rook(Colour.White);

    @Override
    Piece getTestedPiece() {
        return rook;
    }

    @Test
    @Override
    void allValidPositions() {
        assertThatResultMovesAreEqualExpected("e5",
                "e6", "e7", "e8",       // top
                "f5", "g5", "h5",       // right
                "e4", "e3", "e2", "e1", // bottom
                "a5", "b5", "c5", "d5"  // left
        );
    }

    @Test
    @DisplayName("Enemy rook threatens king if ally rook moves vertically")
    @Override
    void cannotEndangerKing() {
        setupKingForTests("a5", rook.colour);

        board.setPiece("h5", new Rook(rook.colour.getOppositeColour()));

        assertThatResultMovesAreEqualExpected("e5",
                "b5", "c5", "d5", // left
                "f5", "g5", "h5"  // right
        );
    }
}
