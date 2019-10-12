package model.pieces;

import model.Colour;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// There is no need to set state to check
// or setup king for board tracking because
// all we care about is next position of king
// which is checked for those cases
class KingTest extends PieceTest {
    private final King king;

    KingTest(){
        super(Colour.White);
        king = new King(ally);
    }

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
    @DisplayName("King in right-upper corner")
    @Override
    void cannotCrossChessboardBorders() {
        assertThatResultMovesAreEqualExpected("h8",
                "g8",       // left
                "g7", "h7"  // bottom
        );
    }

    @Test
    @DisplayName("King threatened by enemy king or queen on move")
    @Override
    void cannotEndangerKing() {
        board.setPiece("d8", new Queen(enemy));  // Endangered a5
        board.setPiece("d4", new King(enemy));   // Endangered c5, c4, c3 fields

        assertThatResultMovesAreEqualExpected("b4",
                "b5",       // top
                "a4",       // left, right
                "a3", "b3"  // bottom
        );
    }

    @Test
    @DisplayName("King threatened by pawn, can only move to pawn position")
    @Override
    void canOnlyKillThreatToKing() {
        board.setPiece("b5", new Pawn(enemy));   // Endangered a4
        board.setPiece("a7", new Rook(enemy));   // Endangered a column

        board.setPiece("a3", new Rook(ally));    // Blocks king on a3
        board.setPiece("b3", new Knight(ally));  // Blocks king on b3
        board.setPiece("b4", new Knight(ally));  // Blocks king on b4

        assertThatResultMovesAreEqualExpected("a4",
                "b5" // Kill the pawn
        );
    }

    @Test
    @DisplayName("King threatened by queen, can only hide behind own colour pawn")
    @Override
    void canOnlyProtectKingFromThreat() {
        board.setPiece("d7", new Queen(enemy));   // Endangered d column
        board.setPiece("e6", new Rook(enemy));    // Endangered e column
        board.setPiece("a5", new Bishop(enemy));  // Endangered c3 and d2 fields

        board.setPiece("c2", new Pawn(ally));     // Blocks king on c2
        board.setPiece("c4", new Knight(ally));   // Blocks king on c4

        board.setPiece("e4", new Pawn(ally));     // Protects king from check on bottom of e column

        assertThatResultMovesAreEqualExpected("d3",
                "e3", "e2" // Hide from queen
        );
    }

    @Test
    @DisplayName("King threatened by knight or rook on move")
    void cannotStepOnEndangeredFields1() {
        board.setPiece("e3", new Knight(enemy)); // Endangered f5
        board.setPiece("d7", new Rook(enemy));   // Endangered d column

        assertThatResultMovesAreEqualExpected("e5",
                "e6", "f6", // Top
                "e4", "f4"  // Bottom
        );
    }

    @Test
    @DisplayName("King threatened by pawn on move")
    void cannotStepOnEndangeredFields2() {
        board.setPiece("e6", new Pawn(enemy)); // Endangered d5 and f5

        assertThatResultMovesAreEqualExpected("e5",
                "d6", "e6", "f6",   // top
                "d4", "e4", "f4"    // bottom
        );
    }

    @Test
    @DisplayName("King cannot move because of threats")
    void cannotStepOnEndangeredFields3() {
        board.setPiece("e3", new Knight(enemy)); // Endangered f5
        board.setPiece("a4", new Rook(enemy));   // Endangered 4-th row
        board.setPiece("g8", new Queen(enemy));  // Endangered d5 and e6
        board.setPiece("h4", new Bishop(enemy)); // Endangered f6
        board.setPiece("c7", new Pawn(enemy));   // Endangered d6

        assertThatResultMovesAreEqualExpected("e5");
    }
}