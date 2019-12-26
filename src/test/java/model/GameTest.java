package model;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class GameTest {
    protected final GameModel model;

    protected GameTest() {
        model = new GameModel();
    }


    protected static <T> void assertResultListMatchesExpected(List<T> result, List<T> expected) {
        assertResultListMatchesExpected(result, expected, "");
    }

    protected static <T> void assertResultListMatchesExpected(List<T> result, List<T> expected, String message) {
        assertThat("Different size than expected - " + message, result, hasSize(expected.size()));
        assertTrue(result.containsAll(expected), "Does not contain all expected objects - \n" + message);
    }
}
