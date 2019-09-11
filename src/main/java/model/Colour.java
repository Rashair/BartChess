package model;

import static java.lang.Math.abs;

public enum Colour {
    White(false),
    Black(true);

    private boolean value;

    Colour(boolean value) {
        this.value = value;
    }

    public int getIntValue() {
        return value ? 1 : 0;
    }

    public boolean getValue() {
        return value;
    }

    public Colour getOppositeColour() {
        return fromBoolean(!this.value);
    }

    public static int getNumberOfColours() {
        return 2;
    }

    public static Colour fromBoolean(boolean b) {
        return b ? Black : White;
    }
}
