package model.game;

import model.Colour;
import model.grid.Board;
import model.grid.Move;
import model.grid.Square;
import model.rules.IJudge;

import java.util.List;

// TODO: Add player interaction and making moves -> changing state
public class Logic {
    private final Board board;
    private final IJudge judge;
    private final State state;
    private boolean isGameOver;

    public Logic(Board board, IJudge judge, State state) {
        this.board = board;
        this.judge = judge;
        this.state = state;
    }

    public List<Move> getValidMovesForSquare(Square square) {
        var piece = board.getPiece(square);
        return piece.getValidMoves(judge, square.x, square.y);
    }

    public void makeMove(Move move, Colour playerColour) {
        board.movePiece(move);

        // If valid move was made then player cannot be in check anymore.
        if (state.isInCheck(playerColour)) {
            state.setCheck(playerColour, false);
        }

        var oppositePlayerColour = playerColour.getOppositeColour();
        if (judge.isKingInCheck(oppositePlayerColour)) {
            state.setCheck(oppositePlayerColour, true);
        }

        if (!judge.areAnyValidMovesForPlayer(playerColour.getOppositeColour())) {
            isGameOver = true;
        }
    }

    public boolean isGameOver() {
        return isGameOver;
    }
}
