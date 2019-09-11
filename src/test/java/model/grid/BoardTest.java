package model.grid;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    @Test
    void rightLowerCornerPosition() {
        String pos = "h1";
        Square result = Board.parsePosition(pos);

        assertEquals(result, new Square(0, 7));
    }

    @Test
    void middlePosition() {
        String pos = "e5";
        Square result = Board.parsePosition(pos);

        assertEquals(result, new Square(4, 4));
    }

    @Test
    void leftUpperCornerPosition() {
        String pos = "a8";

        Square result = Board.parsePosition(pos);

        assertEquals(result, new Square(7, 0));
    }
}