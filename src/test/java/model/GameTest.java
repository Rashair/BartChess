package model;

import model.grid.Board;
import model.grid.Move;
import model.pieces.Piece;
import model.pieces.PieceFactory;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class GameTest {
    protected GameModel model;

    public GameTest() {
        model = new GameModel();
    }


    protected static <T> void assertResultListMatchesExpected(List<T> result, List<T> expected) {
        assertThat(result, hasSize(expected.size()));
        assertTrue(result.containsAll(expected));
    }
}
