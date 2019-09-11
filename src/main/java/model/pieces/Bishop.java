package model.pieces;

import model.Colour;
import model.rules.IJudge;
import model.grid.Move;

import java.util.List;

public class Bishop extends Piece {
    Bishop(Colour colour) {
        super(colour);
    }

    @Override
    public List<Move> getValidMoves(IJudge judge, int x, int y) {
        return judge.getValidMoves(this, x , y);
    }
}
