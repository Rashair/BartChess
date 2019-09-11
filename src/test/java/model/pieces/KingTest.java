package model.pieces;

import model.Colour;
import model.grid.Move;
import org.junit.jupiter.api.Test;

import java.util.List;


class KingTest extends PieceTest {
    private King king = new King(Colour.White);

    @Test
    @Override
    void allValidPositions() {
        var source = "e5"; // center of chessboard
        List<Move> result = getMovesFromPosition(source, king);

        List<Move> expected = Move.createMovesFromSource(source,
                "d6", "e6", "f6",
                "d5", "f5",
                "d4", "e4", "f4");
        assertResultListMatchesExpected(result, expected);
    }


    @Test
    public void cannotCrossChessboardBorders() {
        var source = "h8"; // corner of chessboard
        List<Move> result = getMovesFromPosition(source, king);

        List<Move> expected = Move.createMovesFromSource(source,
                "g8",
                "g7", "h7");
        assertResultListMatchesExpected(result, expected);
    }

    @Test
    public void cannotStepOnEndangeredFields() {
        board.setPiece("f3", new Knight(king.colour.getOppositeColour())); // Endangered f5
        board.setPiece("d7", new Rook(king.colour.getOppositeColour()));   // Endangered d column

        var source = "e5"; // center of chessboard
        List<Move> result = getMovesFromPosition(source, king);

        List<Move> expected = Move.createMovesFromSource(source,
                "e6", "f6",
                "f5",
                "e4", "f4");
        assertResultListMatchesExpected(result, expected);
    }

}