package model.grid;

import javafx.util.Pair;
import model.pieces.Piece;
import model.pieces.Rook;

public class MoveData {
    private final Move move;
    private Pair<Piece, Square> killedPieceData;
    private boolean wasFirstPieceMove;
    private Pair<Rook, Square> movedRookData;

    MoveData(Move move, Piece killedPiece) {
        this.move = move;
        killedPieceData = new Pair<>(killedPiece, move.getDestination());
        wasFirstPieceMove = false;
    }

    public Move getMove() {
        return move;
    }

    public Pair<Piece, Square> getKilledPieceData() {
        return killedPieceData;
    }

    void setKilledPiecePosition(Square pos) {
        this.killedPieceData = new Pair<>(killedPieceData.getKey(), pos);
    }

    public boolean wasFirstPieceMove() {
        return wasFirstPieceMove;
    }

    void setFirstPieceMove() {
        wasFirstPieceMove = true;
    }

    public Pair<Rook, Square> getMovedRookDataOnCastling() {
        return movedRookData;
    }

    void setMovedRookDataOnCastling(Pair<Rook, Square> rookAndOldPosition) {
        if (!move.isCastlingMove()) {
            throw new java.lang.IllegalStateException();
        }

        movedRookData = rookAndOldPosition;
    }
}
