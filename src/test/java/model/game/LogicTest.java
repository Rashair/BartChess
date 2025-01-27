package model.game;

import model.Colour;
import model.GameTest;
import model.grid.Board;
import model.grid.Move;
import model.pieces.Piece;
import model.pieces.Queen;
import org.junit.jupiter.api.AfterEach;
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
import static org.junit.jupiter.api.Assertions.fail;


class LogicTest extends GameTest {
    private final Logic logic;
    private final Board board;

    LogicTest() {
        this.logic = model.getLogic();
        this.board = model.getBoard();
    }

    @BeforeEach
    void setUp() {
        logic.initializeBoard();
    }

    @AfterEach
    void cleanUp() {
        board.clear();
        clearState();
    }

    @Test
    @DisplayName("En passant test")
    void enPassantTest() {
        // Arrange
        Piece movedPawn = makeMoveWithLogic("b2", "b4");
        makeMoveWithLogic("h7", "h5"); // non-meaningful
        makeMoveWithLogic("b4", "b5");
        makeMoveWithLogic("c7", "c5");

        // Act
        var finalDestination = "c6";
        assertDoesNotThrow((Executable) () -> makeMoveWithLogic("b5", finalDestination));

        // Assert
        makeMoveWithLogic("h5", "h4"); // non-meaningful
        List<Move> validMovesForPawn = logic.getValidMoves(Board.parsePosition(finalDestination));
        assertThat("Pawn should be on correct position", validMovesForPawn,
                contains(validMovesForPawn.stream().filter(move -> move.getMovedPiece() == movedPawn).toArray()));

        var expectedSquares = Arrays.asList(Board.parsePosition("b7"), Board.parsePosition("c7"), Board.parsePosition("d7"));
        assertResultListMatchesExpected(validMovesForPawn.stream().map(Move::getDestination).collect(Collectors.toList()), expectedSquares,
                "Pawn should be only able to move to those positions");

        assertThat("Skipped pawn should be killed", board.getPiece("c5"), is(nullValue()));
    }

    @Test
    @DisplayName("Promotion + check test")
    void promotionTest() {
        // Arrange
        makeMoveWithLogic("b2", "b4");
        makeMoveWithLogic("h7", "h5"); // non-meaningful
        makeMoveWithLogic("b4", "b5");
        makeMoveWithLogic("h5", "h4"); // non-meaningful
        makeMoveWithLogic("b5", "b6");
        makeMoveWithLogic("h4", "h3"); // non-meaningful
        makeMoveWithLogic("b6", "c7");
        makeMoveWithLogic("h3", "g2"); // non-meaningful

        // Act
        var finalDestination = "d8";
        assertDoesNotThrow((Executable) () -> makeMoveWithLogic("c7", finalDestination));
        assertDoesNotThrow((Executable) () -> logic.promotePiece(Board.parsePosition(finalDestination), Queen.class));

        // Assert
        var piece = board.getPiece(finalDestination);
        assertThat("Promoted queen should be on correct position", piece, is(instanceOf(Queen.class)));
        assertThat("Promoted queen should be same colour as pawn before", piece.colour, is(equalTo(Colour.White)));
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

    @Test
    @DisplayName("Right valid castling")
    void rightValidCastlingTest() {
        // Arrange
        makeMoveWithLogic("b1", "a3"); // non-meaningful
        makeMoveWithLogic("g7", "g5"); // pawn

        makeMoveWithLogic("a3", "b1"); // non-meaningful
        makeMoveWithLogic("f8", "h6"); // bishop

        makeMoveWithLogic("b1", "a3"); // non-meaningful
        makeMoveWithLogic("g8", "f6"); // knight

        makeMoveWithLogic("a3", "b1"); // non-meaningful

        var blackRook = board.getPiece("h8");
        // Act
        try {
            makeMoveWithLogic("e8", "g8"); // castling to the right
        } catch (IllegalArgumentException e) {
            fail("Move should be valid");
        }

        // Assert
        assertThat("Black rook should be on correct position", board.getPiece("f8"), equalTo(blackRook));
    }

    @Test
    @DisplayName("Left valid castling")
    void leftValidCastlingTest() {
        // Arrange
        makeMoveWithLogic("b2", "b4"); // pawn
        makeMoveWithLogic("g8", "h6"); // non-meaningful

        makeMoveWithLogic("c2", "c4"); // pawn
        makeMoveWithLogic("h6", "g8"); // non-meaningful

        makeMoveWithLogic("c1", "a3"); // bishop
        makeMoveWithLogic("g8", "h6"); // non-meaningful

        makeMoveWithLogic("b1", "c3"); // knight
        makeMoveWithLogic("h6", "g8"); // non-meaningful

        makeMoveWithLogic("d1", "a4"); // queen
        makeMoveWithLogic("g8", "h6"); // non-meaningful

        var whiteRook = board.getPiece("a1");
        // Act
        try {
            makeMoveWithLogic("e1", "c1"); // castling to the left
        } catch (IllegalArgumentException e) {
            fail("Move should be valid");
        }

        // Assert
        assertThat("White rook should be on correct position", board.getPiece("d1"), equalTo(whiteRook));
    }

    @Test
    @DisplayName("Invalid castling - king moved before")
    void invalidCastlingKingMovedTest() {
        // Arrange
        makeMoveWithLogic("b1", "a3"); // non-meaningful
        makeMoveWithLogic("g7", "g5"); // pawn

        makeMoveWithLogic("a3", "b1"); // non-meaningful
        makeMoveWithLogic("f8", "h6"); // bishop

        makeMoveWithLogic("b1", "a3"); // non-meaningful
        makeMoveWithLogic("g8", "f6"); // knight

        makeMoveWithLogic("a3", "b1"); // non-meaningful
        makeMoveWithLogic("e8", "f8"); // king move

        makeMoveWithLogic("b1", "a3"); // non-meaningful
        makeMoveWithLogic("f8", "e8"); // king returns

        makeMoveWithLogic("a3", "b1"); // non-meaningful

        // Act && Assert
        try {
            makeMoveWithLogic("e8", "g8"); // castling to the right
            fail("Move should be invalid");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    @DisplayName("Invalid castling - rook moved before")
    void invalidCastlingRookMovedTest() {
        // Arrange
        makeMoveWithLogic("b1", "a3"); // non-meaningful
        makeMoveWithLogic("g7", "g5"); // pawn

        makeMoveWithLogic("a3", "b1"); // non-meaningful
        makeMoveWithLogic("f8", "h6"); // bishop

        makeMoveWithLogic("b1", "a3"); // non-meaningful
        makeMoveWithLogic("g8", "f6"); // knight

        makeMoveWithLogic("a3", "b1"); // non-meaningful
        makeMoveWithLogic("h8", "g8"); // rook move

        makeMoveWithLogic("b1", "a3"); // non-meaningful
        makeMoveWithLogic("g8", "h8"); // rook returns

        makeMoveWithLogic("a3", "b1"); // non-meaningful

        // Act && Assert
        try {
            makeMoveWithLogic("e8", "g8"); // castling to the right
            fail("Move should be invalid");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    @DisplayName("Invalid castling - king in check")
    void invalidCastlingCheckTest() {
        // Arrange
        makeMoveWithLogic("b2", "b4"); // pawn
        makeMoveWithLogic("g8", "h6"); // non-meaningful

        makeMoveWithLogic("c2", "c4"); // pawn
        makeMoveWithLogic("h6", "g8"); // non-meaningful

        makeMoveWithLogic("f2", "f4"); // pawn
        makeMoveWithLogic("g8", "h6"); // non-meaningful

        makeMoveWithLogic("c1", "a3"); // bishop
        makeMoveWithLogic("h6", "g8"); // non-meaningful

        makeMoveWithLogic("b1", "c3"); // knight
        makeMoveWithLogic("e7", "e6"); // pawn

        makeMoveWithLogic("d1", "a4"); // queen
        makeMoveWithLogic("d8", "h4"); // queen puts king in check

        // Act && Assert
        try {
            makeMoveWithLogic("e1", "c1"); // castling to the left
            fail("Move should be invalid");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    @DisplayName("Invalid castling - threat from enemy")
    void invalidCastlingThreatTest() {
        // Arrange
        makeMoveWithLogic("b2", "b4"); // pawn
        makeMoveWithLogic("g8", "h6"); // non-meaningful

        makeMoveWithLogic("c2", "c4"); // pawn
        makeMoveWithLogic("h6", "g8"); // non-meaningful

        makeMoveWithLogic("d2", "d4"); // pawn
        makeMoveWithLogic("g8", "h6"); // non-meaningful

        makeMoveWithLogic("c1", "a3"); // bishop
        makeMoveWithLogic("e7", "e6"); // pawn

        makeMoveWithLogic("b1", "c3"); // knight
        makeMoveWithLogic("d8", "f6"); // queen

        makeMoveWithLogic("d1", "a4"); // queen
        makeMoveWithLogic("f6", "d4"); // queen poses a threat on d1

        // Act && Assert
        try {
            makeMoveWithLogic("e1", "c1"); // castling to the left
            fail("Move should be invalid");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    @DisplayName("Invalid castling - knight between king and rook")
    void invalidCastlingPieceTest() {
        // Arrange
        makeMoveWithLogic("b2", "b4"); // pawn
        makeMoveWithLogic("g8", "h6"); // non-meaningful

        makeMoveWithLogic("c2", "c4"); // pawn
        makeMoveWithLogic("h6", "g8"); // non-meaningful

        makeMoveWithLogic("d2", "d4"); // pawn
        makeMoveWithLogic("g8", "h6"); // non-meaningful

        makeMoveWithLogic("c1", "a3"); // bishop
        makeMoveWithLogic("h6", "g8"); // non-meaningful

        makeMoveWithLogic("d1", "a4"); // queen
        makeMoveWithLogic("g8", "h6"); // non-meaningful

        // Act && Assert
        try {
            makeMoveWithLogic("e1", "c1"); // castling to the left
            fail("Move should be invalid");
        } catch (IllegalArgumentException ignored) {
        }
    }

    private Piece makeMoveWithLogic(String from, String to) {
        var validMoves = logic.getValidMoves(Board.parsePosition(from));
        var optionalMove = validMoves.stream().filter(m -> m.getDestination().equals(Board.parsePosition(to))).findFirst();

        if (optionalMove.isEmpty()) {
            throw new IllegalArgumentException();
        }

        Move move = optionalMove.get();
        logic.makeMove(move);

        return move.getMovedPiece();
    }
}