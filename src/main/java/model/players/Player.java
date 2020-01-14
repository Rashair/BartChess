package model.players;

import model.Colour;
import model.grid.Move;
import model.grid.Square;

import java.util.List;

public abstract class Player {
    private Colour colour;

    public Player(Colour colour) {
        this.colour = colour;
    }

    public Colour getColour() {
        return colour;
    }
}
