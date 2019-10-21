package model.rules;

import model.Colour;
import model.GameTest;
import model.grid.Board;
import model.grid.Square;
import model.pieces.Knight;
import model.pieces.Pawn;
import model.pieces.Piece;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardInitializationTest extends GameTest {
    private Board board = model.getBoard();
    private IJudge judge = model.getJudge();

    private Square[][] knightsPositions;
    private final int[] pawnRows;
    private final int expectedMovesNumber = 2;

    BoardInitializationTest() {
        knightsPositions = new Square[Colour.getNumberOfColours()][];
        knightsPositions[Colour.White.getIntValue()] = new Square[]{Board.parsePosition("b1"), Board.parsePosition("g1")};
        knightsPositions[Colour.Black.getIntValue()] = new Square[]{Board.parsePosition("b8"), Board.parsePosition("g8")};

        pawnRows = new int[Colour.getNumberOfColours()];
        pawnRows[Colour.White.getIntValue()] = 1;
        pawnRows[Colour.Black.getIntValue()] = 6;
    }

    @Test
    void correctValidMovesOnBoardInitialization() {
        // Arrange

        // Act
        board.initializePieces(judge.getInitialPositionsForAllPieces());

        // Assert
        for (int x = 0; x < Board.rowsNum; ++x) {
            for (int y = 0; y < Board.columnsNum; ++y) {
                var piece = board.getPiece(x, y);
                if (piece != null) {
                    if (piece instanceof Knight) {
                        assertValidKnightMoves(x, y, piece);
                    }
                    else if (piece instanceof Pawn) {
                        assertValidPawnMoves(x, y, piece);
                    }
                    else {
                        var result = piece.getValidMoves(judge, x, y);
                        assertResultListMatchesExpected(result, new ArrayList<>(), board.toString());
                    }
                }
            }
        }
    }

    private void assertValidKnightMoves(int x, int y, Piece piece) {
        var currentSquare = new Square(x, y);
        assertTrue(ArrayUtils.contains(knightsPositions, currentSquare),
                "Knight position should be one of expected values");

        var currentMoves = piece.getValidMoves(judge, x, y);
        assertEquals(expectedMovesNumber, currentMoves.size(), "Wrong number of moves");

        for (int i = 0; i < expectedMovesNumber; ++i) {
            Square destination = currentMoves.get(i).getDestination();
            assertEquals(2, Math.abs(destination.x - currentSquare.x),
                    "Rows of positions should differ exactly by 2");
            assertEquals(1, Math.abs(destination.y - currentSquare.y),
                    "Columns of positions should differ exactly by 1");
        }
    }


    private void assertValidPawnMoves(int x, int y, Piece piece) {
        int pawnRow = pawnRows[piece.colour.getIntValue()];
        assertEquals(pawnRow, x, " Position row should be one of expected values");

        var currentMoves = piece.getValidMoves(judge, x, y);
        assertEquals(expectedMovesNumber, currentMoves.size(), "Wrong number of moves");

        var currentSquare = new Square(x, y);
        for (int i = 0; i < expectedMovesNumber; ++i) {
            Square destination = currentMoves.get(i).getDestination();

            var rowsDiff = Math.abs(destination.x - currentSquare.x);
            assertTrue(rowsDiff > 0 && rowsDiff <= 2, "Rows of positions should differ by 1 or 2");
            assertEquals(destination.y, currentSquare.y, "Columns should be equal");
        }
    }
}
