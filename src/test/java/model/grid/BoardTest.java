package model.grid;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BoardTest {
    @Test
    void rightLowerCornerParsePosition() {
        String pos = "h1";
        Square result = Board.parsePosition(pos);

        assertEquals(result, new Square(0, 7));
    }

    @Test
    void middleParsePosition() {
        String pos = "e5";
        Square result = Board.parsePosition(pos);

        assertEquals(result, new Square(4, 4));
    }

    @Test
    void leftUpperCornerParsePosition() {
        String pos = "a8";

        Square result = Board.parsePosition(pos);

        assertEquals(result, new Square(7, 0));
    }
}