package model.game;

import model.Colour;
import model.grid.Board;
import model.grid.Move;
import model.grid.Square;
import model.pieces.Piece;
import model.rules.IJudge;

import java.util.ArrayList;
import java.util.List;

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

    public void initializeBoard() {
        board.initializePieces(judge.getInitialPositionsForAllPieces());
    }

    public List<Move> getValidMoves(Square square) {
        return getValidMoves(square.x, square.y);
    }

    public List<Move> getValidMoves(int x, int y) {
        var piece = board.getPiece(x, y);
        return piece != null ? piece.getValidMoves(judge, x, y) :
                new ArrayList<>();
    }

    public MoveTrace makeMove(Move move, Colour playerColour) {
        board.movePiece(move);

        return setStateAndReturnTrace(move, playerColour);
    }

    public MoveTrace promotePiece(Square pos, Class<? extends Piece> promoted) {
        board.promotePiece(pos, promoted);
        var piece = board.getPiece(pos);

        return setStateAndReturnTrace(new Move(pos, pos, piece), piece.colour);
    }

    private MoveTrace setStateAndReturnTrace(Move move, Colour playerColour) {
        // If valid move was made then player cannot be in check anymore.
        if (state.isInCheck(playerColour)) {
            state.setCheck(playerColour, false);
        }

        var oppositePlayerColour = playerColour.getOppositeColour();
        if (judge.isKingInCheck(oppositePlayerColour)) {
            state.setCheck(oppositePlayerColour, true);
        }

        var result = new MoveTrace(move);
        if (!judge.areAnyValidMovesForPlayer(oppositePlayerColour)) {
            isGameOver = true;
            Colour winner = null;
            if (state.isInCheck(oppositePlayerColour)) {
                winner = playerColour;
            }

            result.setGameOver(winner);
        }

        return result;
    }

    public boolean isGameOver() {
        return isGameOver;
    }
}
