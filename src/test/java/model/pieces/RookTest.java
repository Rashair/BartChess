package model.pieces;

import model.Colour;
import model.grid.Move;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RookTest extends PieceTest {
    private Rook rook = new Rook(Colour.White);

    @Test
    @Override
    void allValidPositions() {
        var source = "e5"; // center of chessboard
        List<Move> result = getMovesFromPosition(source, rook);

        List<Move> expected = Move.createMovesFromSource(source,
                "e6", "e7", "e8",
                "a5", "b5", "c5", "d5",
                "f5", "g5", "h5",
                "e4", "e3", "e2", "e1");
        assertResultListMatchesExpected(result, expected);
    }
}
