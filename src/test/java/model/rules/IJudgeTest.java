package model.rules;

import model.Colour;
import model.GameTest;
import model.grid.Board;
import model.grid.Square;
import model.pieces.Knight;
import model.pieces.Pawn;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IJudgeTest extends GameTest {
    Board board = model.getBoard();
    IJudge judge = model.getJudge();


    @BeforeEach
    void setUp() {
        board.initializePieces(judge.getInitialPositionsForAllPieces());
    }

    @Test
    void areAnyValidMovesForPlayer() {

    }

    @Test
    void getInitialPositionsForAllPieces() {
        var knightsPositions = new Square[Colour.getNumberOfColours()][];
        knightsPositions[Colour.White.getIntValue()] = new Square[]{Board.parsePosition("b1"), Board.parsePosition("g1")};
        knightsPositions[Colour.Black.getIntValue()] = new Square[]{Board.parsePosition("b8"), Board.parsePosition("g8")};

        var pawnRows = new int[Colour.getNumberOfColours()];
        pawnRows[Colour.White.getIntValue()] = 1;
        pawnRows[Colour.Black.getIntValue()] = 6;

        final int expectedMovesNumber = 2;
        for (int x = 0; x < Board.rowsNum; ++x) {
            for (int y = 0; y < Board.columnsNum; ++y) {
                var piece = board.getPiece(x, y);
                if (piece != null) {
                    if (piece instanceof Knight) {
                        var currentSquare = new Square(x, y);
                        assertTrue(ArrayUtils.contains(knightsPositions[piece.colour.getIntValue()], currentSquare),
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
                    else if (piece instanceof Pawn) {
                        assertEquals(pawnRows[piece.colour.getIntValue()], x,
                                "Pawn position row should be one of expected values");

                        var currentMoves = piece.getValidMoves(judge, x, y);
                        assertEquals(expectedMovesNumber, currentMoves.size(), "Wrong number of moves");

                        var currentSquare = new Square(x, y);
                        for (int i = 0; i < expectedMovesNumber; ++i) {
                            Square destination = currentMoves.get(i).getDestination();

                            var rowsDiff = Math.abs(destination.x - currentSquare.x);
                            assertTrue(rowsDiff > 0 && rowsDiff <= 2,
                                    "Rows of positions should differ by 1 or 2");
                            assertEquals(destination.y, currentSquare.y, "Columns should be equal");
                        }
                    }
                    else {
                        var result = piece.getValidMoves(judge, x, y);
                        assertResultListMatchesExpected(result, new ArrayList<>(), "\n" + board.toString());
                    }
                }
            }
        }
    }

    @AfterEach
    void cleanUp() {
        board.clearAllPieces();
    }
}