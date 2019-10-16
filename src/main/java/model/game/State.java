package model.game;

import model.Colour;

public class State {
    private final boolean[] isInCheck;
    private static final int size = Colour.getNumberOfColours();

    public State() {
        isInCheck = new boolean[size];
    }

    public boolean isInCheck(Colour colour) {
        return isInCheck[colour.getIntValue()];
    }

    public void setCheck(Colour colour, boolean value) {
        isInCheck[colour.getIntValue()] = value;
    }
}
