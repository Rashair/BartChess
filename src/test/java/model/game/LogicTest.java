package model.game;

import model.Colour;
import model.GameTest;
import model.grid.Board;
import model.grid.Move;
import model.pieces.Piece;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


class LogicTest extends GameTest {
    private Logic logic;

    LogicTest() {
        this.logic = model.getLogic();
    }

    @BeforeEach
    void setUp() {
        logic.initializeBoard();
    }

    @Test
    @DisplayName("En passant test")
    void enPassantTest() {
        // Arrange
        Piece movedPawn = makeMoveWithLogic("b2", "b4", Colour.White);
        makeMoveWithLogic("h7", "h5", Colour.Black);
        makeMoveWithLogic("b4", "b5", Colour.White);
        makeMoveWithLogic("c7", "c5", Colour.Black);

        // Act
        makeMoveWithLogic("b5", "c4", Colour.White);

        // Assert
        List<Move> validMovesForPawn = logic.getValidMoves(Board.parsePosition("c4"));

        assertThat(validMovesForPawn, contains(validMovesForPawn.stream().filter(move -> move.getMovedPiece() == movedPawn)));
    }

    private Piece makeMoveWithLogic(String from, String to, Colour colour) {
        var validMoves = logic.getValidMoves(Board.parsePosition(from));
        Move move = validMoves.stream().filter(m -> m.getDestination().equals(Board.parsePosition(to))).findFirst().get();
        logic.makeMove(move, colour);
        return move.getMovedPiece();
    }
}