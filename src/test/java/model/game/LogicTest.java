package model.game;

import model.Colour;
import model.GameTest;
import model.grid.Board;
import model.grid.Move;
import model.pieces.Piece;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


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
        makeMoveWithLogic("h7", "h5", Colour.Black); // non-meaningful
        makeMoveWithLogic("b4", "b5", Colour.White);
        makeMoveWithLogic("c7", "c5", Colour.Black);

        var finalDestination = "c6";
        // Act
        assertDoesNotThrow((Executable) () -> makeMoveWithLogic("b5", finalDestination, Colour.White));

        // Assert
        List<Move> validMovesForPawn = logic.getValidMoves(Board.parsePosition(finalDestination));
        assertThat("Pawn should be on correct position", validMovesForPawn,
                contains(validMovesForPawn.stream().filter(move -> move.getMovedPiece() == movedPawn).toArray()));

        var expectedSquares = Arrays.asList(Board.parsePosition("b7"), Board.parsePosition("c7"), Board.parsePosition("d7"));
        assertResultListMatchesExpected(validMovesForPawn.stream().map(Move::getDestination).collect(Collectors.toList()), expectedSquares,
                "Pawn should be only able to move to those positions");

        assertThat("Skipped pawn should be killed", logic.getValidMoves(Board.parsePosition("c5")), is(empty()));
    }

    private Piece makeMoveWithLogic(String from, String to, Colour colour) {
        var validMoves = logic.getValidMoves(Board.parsePosition(from));
        Move move = validMoves.stream().filter(m -> m.getDestination().equals(Board.parsePosition(to))).findFirst().get();
        logic.makeMove(move, colour);
        return move.getMovedPiece();
    }
}