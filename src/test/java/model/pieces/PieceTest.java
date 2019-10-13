package model.pieces;

import model.Colour;
import model.GameState;
import model.GameTest;
import model.grid.Board;
import model.grid.Move;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

abstract class PieceTest extends GameTest {
    final Board board;
    final GameState state;
    final Colour ally;
    final Colour enemy;

    public PieceTest(Colour ally) {
        this.board = model.getBoard();
        this.state = model.getState();
        this.ally = ally;
        this.enemy = ally.getOppositeColour();
    }

    abstract Piece getTestedPiece();

    @Test
    abstract void allValidPositions();

    @Test
    abstract void cannotCrossChessboardBorders();

    @Test
    abstract void cannotEndangerKing();

    @Test
    abstract void canOnlyKillThreatToKing();

    @Test
    abstract void canOnlyProtectKingFromThreat();

    @BeforeEach
    void setUp() {
        board.clearAllPieces();
    }

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

    void setupKingForTests(String pos, Colour kingColour) {
        board.setPiece(pos, new King(kingColour));
        board.movePiece(pos, pos); // For board kingPosition tracking
    }
}
