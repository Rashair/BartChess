package model;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class GameTest {
    protected final static GameModel model = new GameModel();

    protected GameTest() { }


    protected static <T> void assertResultListMatchesExpected(List<T> result, List<T> expected) {
        assertThat("Different size than expected", result, hasSize(expected.size()));
        assertTrue(result.containsAll(expected), "Does not contain all expected objects");
    }
}
