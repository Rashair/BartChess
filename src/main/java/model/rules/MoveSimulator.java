package model.rules;

import model.grid.Board;
import model.grid.Move;
import model.grid.MoveData;

class MoveSimulator {
    private final Board board;
    private MoveData previousMoveData;
    private Move move;

    MoveSimulator(Board board) {
        this.board = board;
    }

    void setMove(Move move) {
        this.move = move;
        this.previousMoveData = board.getLastMoveData();
    }

    void makeMove() {
        board.movePiece(move);
    }

    void reverseMove() {
        board.reverseLastMove(previousMoveData);
    }
}
