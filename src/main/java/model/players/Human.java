package model.players;

import model.Colour;
import model.grid.Move;
import model.grid.Square;

import java.util.List;

public class Human extends Player {
    public Human(Colour colour) {
        super(colour);
    }

    @Override
    public Square chooseSourceSquare() {
        return null;
    }

    @Override
    public Move chooseMove(List<Move> validMoves) {
        if (validMoves.isEmpty())
            return null;
        return null;
    }
}
