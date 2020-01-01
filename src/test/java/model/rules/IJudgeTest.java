package model.rules;

import model.Colour;
import model.GameTest;
import model.game.State;
import model.grid.Board;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class IJudgeTest extends GameTest {
    Board board = model.getBoard();
    IJudge judge = model.getJudge();
    State state = model.getState();

    @BeforeEach
    void setUp() {
        board.initializePieces(judge.getInitialPositionsForAllPieces());
    }

    @AfterEach
    void cleanUp() {
        board.clear();
        clearState();
    }

    @Test
    void noValidMovesOnCheckMate() {
        // Arrange
        // Fastest possible check
        board.movePiece("f2", "f3"); // White pawn
        board.movePiece("e7", "e5"); // Black pawn
        board.movePiece("g2", "g4"); // White pawn
        board.movePiece("d8", "h4"); // Black Queen - checkmate

        var playerColour = Colour.White;
        state.setCheck(playerColour, true);

        // Act
        boolean anyValidMovesForPlayer = judge.areAnyValidMovesForPlayer(playerColour);

        // Assert
        assertFalse(anyValidMovesForPlayer, "There should be no valid moves on checkmate for white player\n" + board.toString());
    }

    @Test
    void noValidMovesOnStaleMate() {
        // Arrange
        // Fastest possible stale mate : https://www.chess.com/forum/view/game-showcase/fastest-stalemate-known-in-chess
        // 1 e3 a5
        board.movePiece("e2", "e3"); // White pawn
        board.movePiece("a7", "a5"); // Black pawn

        // 2 Qh5 Ra6
        board.movePiece("d1", "h5"); // White queen
        board.movePiece("a8", "a6"); // Black rook

        // 3 Qxa5 h5
        board.movePiece("h5", "a5"); // White queen
        board.movePiece("h7", "h5"); // Black pawn

        // 4 h5 Rah4
        board.movePiece("h2", "h4"); // White pawn
        board.movePiece("a6", "h6"); // Black rook

        // 5 Qxc7 f6
        board.movePiece("a5", "c7"); // White queen
        board.movePiece("f7", "f6"); // Black pawn

        // 6 Qxd7+ Kf7
        board.movePiece("c7", "d7"); // White queen
        board.movePiece("e8", "f7"); // Black king

        // 7 Qxb7 Qd3
        board.movePiece("d7", "b7"); // White queen
        board.movePiece("d8", "d3"); // Black queen

        // 8 Qxb8 Qh7
        board.movePiece("b7", "b8"); // White queen
        board.movePiece("d3", "h7"); // Black queen

        // 9 Qxc8 Kg6
        board.movePiece("b8", "c8"); // White queen
        board.movePiece("f7", "g6"); // Black king

        // Act -> 10 Qe6
        board.movePiece("c8", "e6"); // White queen

        // Assert
        assertFalse(judge.areAnyValidMovesForPlayer(Colour.Black), "There should be no valid moves on stalemate for black player\n" + board.toString());
    }
}