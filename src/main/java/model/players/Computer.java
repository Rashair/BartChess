package model.players;

import model.Colour;
import model.grid.Move;
import model.grid.Square;

import java.util.List;

public class Computer extends Player {
    public Computer(Colour colour) {
        super(colour);
    }

    @Override
    public Square chooseSourceSquare() {
        return null;
    }

    @Override
    public Move chooseMove(List<Move> validMoves) {
        return null;
    }
}
