package model;

import model.grid.Board;
import model.grid.Move;
import model.pieces.Piece;
import model.pieces.PieceFactory;

import javax.management.modelmbean.ModelMBean;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class GameTest {
    protected final static GameModel model = new GameModel();

    public GameTest() { }


    protected static <T> void assertResultListMatchesExpected(List<T> result, List<T> expected) {
        assertThat(result, hasSize(expected.size()));
        assertTrue(result.containsAll(expected));
    }
}
