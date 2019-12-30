package model.game;

import model.Colour;
import model.GameTest;
import model.grid.Board;
import model.grid.Move;
import model.pieces.Piece;
import model.pieces.Queen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


class LogicTest extends GameTest {
    private Logic logic;
    private Board board;

    LogicTest() {
        this.logic = model.getLogic();
        this.board = model.getBoard();
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


        // Act
        var finalDestination = "c6";
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

    @Test
    @DisplayName("Promotion + check test")
    void promotionTest() {
        // Arrange
        makeMoveWithLogic("b2", "b4", Colour.White);
        makeMoveWithLogic("h7", "h5", Colour.Black); // non-meaningful
        makeMoveWithLogic("b4", "b5", Colour.White);
        makeMoveWithLogic("h5", "h4", Colour.Black); // non-meaningful
        makeMoveWithLogic("b5", "b6", Colour.White);
        makeMoveWithLogic("h4", "h3", Colour.Black); // non-meaningful
        makeMoveWithLogic("b6", "c7", Colour.White);
        makeMoveWithLogic("h3", "g2", Colour.Black); // non-meaningful

        // Act
        var finalDestination = "d8";
        assertDoesNotThrow((Executable) () -> makeMoveWithLogic("c7", finalDestination, Colour.White));
        assertDoesNotThrow((Executable) () -> logic.promotePiece(Board.parsePosition(finalDestination), Queen.class));

        // Assert
        List<Move> validMovesForQueen = logic.getValidMoves(Board.parsePosition(finalDestination));
        assertThat("Queen should be on correct position", validMovesForQueen,
                contains(validMovesForQueen.stream().filter(move -> move.getMovedPiece() instanceof Queen).toArray()));
        assertOnlyKingCanMove(finalDestination);
    }

    private void assertOnlyKingCanMove(String finalDestination) {
        var blackKingPos = board.getKingPosition(Colour.Black);
        var blackKing = board.getPiece(blackKingPos);
        var validKingMoves = logic.getValidMoves(blackKingPos);
        assertResultListMatchesExpected(validKingMoves, Collections.singletonList(new Move(blackKingPos, Board.parsePosition(finalDestination), blackKing)));

        for (int x = 0; x < Board.rowsNum; ++x) {
            for (int y = 0; y < Board.columnsNum; ++y) {
                var piece = board.getPiece(x, y);
                if (piece != null && piece.colour == Colour.Black && piece != blackKing) {
                    var validBlackPieceMoves = logic.getValidMoves(x, y);
                    assertThat("No valid moves except for the king", validBlackPieceMoves, is(empty()));
                }
            }
        }
    }

    private Piece makeMoveWithLogic(String from, String to, Colour colour) {
        var validMoves = logic.getValidMoves(Board.parsePosition(from));
        Move move = validMoves.stream().filter(m -> m.getDestination().equals(Board.parsePosition(to))).findFirst().get();
        logic.makeMove(move, colour);
        return move.getMovedPiece();
    }
}