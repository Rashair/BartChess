package model.pieces;

import model.Colour;
import model.GameTest;
import model.grid.Board;
import model.grid.Move;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

abstract class PieceTest extends GameTest {
    Board board = model.getBoard();

    @Test
    abstract void allValidPositions();

    @Test
    abstract void cannotEndangerKing();

    @BeforeEach
    void setUp() {
        board.clearAllPieces();
    }

    abstract Piece getTestedPiece();

    List<Move> getMovesFromPosition(String pos, Piece piece) {
        return piece.getValidMoves(model.getJudge(), pos);
    }

    void assertThatResultMovesAreEqualExpected(String source, String... expectedPossibleMoves) {
        var piece = getTestedPiece();
        board.setPiece(source, piece);
        List<Move> result = getMovesFromPosition(source, piece);

        List<Move> expected = Move.createMovesFromSource(source, expectedPossibleMoves);
        assertResultListMatchesExpected(result, expected);
    }

    void setupKingForTests(String pos, Colour kingColour){
        board.setPiece(pos, new King(kingColour));
        board.movePiece(pos, pos); // For board kingPosition tracking
    }
}
