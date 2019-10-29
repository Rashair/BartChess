package model.pieces;

import model.Colour;
import model.grid.Move;
import model.rules.IJudge;

import java.util.List;

public class Pawn extends Piece {
    Pawn(Colour colour) {
        super(colour);
    }

    @Override
    public List<Move> getValidMoves(IJudge judge, int x, int y) {
        return judge.getValidMoves(this, x, y);
    }

    @Override
    public String toString() {
        return colour == Colour.White ? "\u2659" : "\u265F";
    }
}
