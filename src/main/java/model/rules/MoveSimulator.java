package model.rules;

import model.grid.Board;
import model.grid.Move;
import model.pieces.Piece;

class MoveSimulator {
    private final Board board;
    private Move previousMove;
    private Move move;
    private Piece destinationPiece;

    MoveSimulator(Board board) {
        this.board = board;
    }

    void setMove(Move move) {
        this.move = move;
        destinationPiece = board.getPiece(move.getDestination());
    }

    void makeMove() {
        previousMove = board.getLastMove();
        board.movePiece(move);
    }

    void reverseMove() {
        board.movePiece(move.getDestination(), move.getSource());
        board.setPiece(move.getDestination(), destinationPiece);
        board.setLastMove(previousMove);
    }
}
