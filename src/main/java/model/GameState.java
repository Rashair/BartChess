package model;

import model.grid.Square;

import java.util.*;

public class GameState {
    private final boolean[] isInCheck;

    public GameState() {
        int size = Colour.getNumberOfColours();
        isInCheck = new boolean[size];
    }

    public boolean isInCheck(Colour colour) {
        return isInCheck[colour.getIntValue()];
    }

    public void setCheck(Colour colour, boolean value) {
        isInCheck[colour.getIntValue()] = value;
    }
}
