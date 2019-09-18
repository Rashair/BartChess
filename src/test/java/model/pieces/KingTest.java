package model.pieces;

import model.Colour;
import model.grid.Move;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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
    public void cannotStepOnEndangeredFields1() {
        board.setPiece("e3", new Knight(king.colour.getOppositeColour())); // Endangered f5
        board.setPiece("d7", new Rook(king.colour.getOppositeColour()));   // Endangered d column

        var source = "e5"; // center of chessboard
        List<Move> result = getMovesFromPosition(source, king);

        List<Move> expected = Move.createMovesFromSource(source,
                "e6", "f6",
                "e4", "f4");
        assertResultListMatchesExpected(result, expected);
    }

    @Test
    public void cannotStepOnEndangeredFields2() {
        board.setPiece("e6", new Pawn(king.colour.getOppositeColour())); // Endangered d5 and f5

        var source = "e5"; // center of chessboard
        List<Move> result = getMovesFromPosition(source, king);

        List<Move> expected = Move.createMovesFromSource(source,
                "d6", "e6", "f6",
                "d4", "e4", "f4");
        assertResultListMatchesExpected(result, expected);
    }

    @Test
    public void cannotStepOnEndangeredFields3() {
        var oppositeColour = king.colour.getOppositeColour();
        board.setPiece("e3", new Knight(oppositeColour)); // Endangered f5
        board.setPiece("a4", new Rook(oppositeColour));   // Endangered 4-th row
        board.setPiece("g8", new Queen(oppositeColour));  // Endangered d5 and e6
        board.setPiece("h4", new Bishop(oppositeColour)); // Endangered f6
        board.setPiece("c7", new Pawn(oppositeColour));   // Endangered d6

        var source = "e5"; // center of chessboard
        List<Move> result = getMovesFromPosition(source, king);

        List<Move> expected = new ArrayList<Move>();
        assertResultListMatchesExpected(result, expected);
    }

}