package model.pieces;

import model.Colour;
import model.grid.Board;
import model.rules.IJudge;
import model.grid.Move;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece {
    public final Colour colour;
    protected boolean isAlive;

    Piece(Colour colour) {
        this.colour = colour;
        isAlive = true;
    }

    public abstract List<Move> getValidMoves(IJudge judge, int x, int y);

    public List<Move> getValidMoves(IJudge judge, String from) {
        var p = Board.parsePosition(from);
        return getValidMoves(judge, p.x, p.y);
    }

    protected void kill() {
        isAlive = false;
    }
}
