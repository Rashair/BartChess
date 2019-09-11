package model.pieces;

import model.GameTest;
import model.grid.Board;
import model.grid.Move;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

abstract class PieceTest extends GameTest {
    Board board = model.getBoard();

    @Test
    abstract void allValidPositions();

    @BeforeEach
    void setUp() {
        board.clearAllPieces();
    }

    List<Move> getMovesFromPosition(String pos, Piece piece) {
        board.setPiece(pos, piece);
        return piece.getValidMoves(model.getJudge(), pos);
    }
}
