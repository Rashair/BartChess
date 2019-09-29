package model.pieces;

import model.Colour;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class KingTest extends PieceTest {
    private King king = new King(Colour.White);

    @Override
    Piece getTestedPiece() {
        return king;
    }

    @Test
    @Override
    void allValidPositions() {
        assertThatResultMovesAreEqualExpected("e5",
                "d6", "e6", "f6", // top
                "d5", "f5",       // left, right
                "d4", "e4", "f4"  // bottom
        );
    }

    @Test
    @Override
    void cannotEndangerKing() {
        // Implemented in cannotStepEndangeredFieldsX tests (X = 1,2,3)
    }


    @Test
    @DisplayName("King in corner")
    void cannotCrossChessboardBorders() {
        assertThatResultMovesAreEqualExpected("h8",
                "g8",       // left
                "g7", "h7"  // bottom
        );
    }

    @Test
    @DisplayName("King threatened by knight or rook on move")
    void cannotStepOnEndangeredFields1() {
        board.setPiece("e3", new Knight(king.colour.getOppositeColour())); // Endangered f5
        board.setPiece("d7", new Rook(king.colour.getOppositeColour()));   // Endangered d column

        assertThatResultMovesAreEqualExpected("e5",
                "e6", "f6", // top
                "e4", "f4"  // bottom
        );
    }

    @Test
    @DisplayName("King threatened by pawn on move")
    void cannotStepOnEndangeredFields2() {
        board.setPiece("e6", new Pawn(king.colour.getOppositeColour())); // Endangered d5 and f5

        assertThatResultMovesAreEqualExpected("e5",
                "d6", "e6", "f6",   // top
                "d4", "e4", "f4"    // bottom
        );
    }

    @Test
    @DisplayName("King cannot move because of threats")
    void cannotStepOnEndangeredFields3() {
        var oppositeColour = king.colour.getOppositeColour();
        board.setPiece("e3", new Knight(oppositeColour)); // Endangered f5
        board.setPiece("a4", new Rook(oppositeColour));   // Endangered 4-th row
        board.setPiece("g8", new Queen(oppositeColour));  // Endangered d5 and e6
        board.setPiece("h4", new Bishop(oppositeColour)); // Endangered f6
        board.setPiece("c7", new Pawn(oppositeColour));   // Endangered d6

        assertThatResultMovesAreEqualExpected("e5");
    }
}