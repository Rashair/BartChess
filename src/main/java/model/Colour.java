package model;

public enum Colour {
    White(false),
    Black(true);

    private static final int numberOfColours = 2;
    private final boolean value;

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
        return numberOfColours;
    }

    public static Colour fromBoolean(boolean b) {
        return b ? Black : White;
    }
}
