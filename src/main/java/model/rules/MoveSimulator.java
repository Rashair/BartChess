package model.rules;

import model.grid.Board;
import model.grid.Move;
import model.pieces.Piece;

class MoveSimulator {
    private final Board board;
    private Move previousMove;
    private Move move;
    private Piece pieceToKill;

    MoveSimulator(Board board) {
        this.board = board;
    }

    void setMove(Move move) {
        this.move = move;
        this.pieceToKill = board.getPiece(move.getDestination());
    }

    void makeMove() {
        previousMove = board.getLastMove();
        board.movePiece(move);
    }

    void reverseMove() {
        board.movePiece(move.getReverse());
        board.setPiece(move.getDestination(), pieceToKill);
        board.setLastMove(previousMove);
    }
}
