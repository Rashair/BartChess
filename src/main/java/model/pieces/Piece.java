package model.pieces;

import model.Colour;
import model.grid.Board;
import model.grid.Move;
import model.rules.IJudge;

import java.util.List;

// TODO: Create EmptyPiece class instead of null.
public abstract class Piece {
    public final Colour colour;

    Piece(Colour colour) {
        this.colour = colour;
    }

    public abstract List<Move> getValidMoves(IJudge judge, int x, int y);

    public List<Move> getValidMoves(IJudge judge, String from) {
        var p = Board.parsePosition(from);
        return getValidMoves(judge, p.x, p.y);
    }

    @Override
    public String toString() {
        return Character.toString(this.getClass().getSimpleName().charAt(0));
    }
}
