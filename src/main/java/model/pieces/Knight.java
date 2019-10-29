package model.pieces;

import model.Colour;
import model.grid.Move;
import model.rules.IJudge;

import java.util.List;

public class Knight extends Piece {
    Knight(Colour colour) {
        super(colour);
    }

    @Override
    public List<Move> getValidMoves(IJudge judge, int x, int y) {
        return judge.getValidMoves(this, x, y);
    }

    @Override
    public String toString() {
        return colour == Colour.White ? "\u2658" : "\u265E";
    }
}
