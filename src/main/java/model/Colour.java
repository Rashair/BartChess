package model;

import java.util.concurrent.ThreadLocalRandom;

public enum Colour {
    White(false),
    Black(true);

    private static final int numberOfColours = 2;
    private final boolean value;

    Colour(boolean value) {
        this.value = value;
    }

    public static int getNumberOfColours() {
        return numberOfColours;
    }

    public static Colour fromBoolean(boolean b) {
        return b ? Black : White;
    }

    public static Colour fromInt(int i) {
        return i == 1 ? fromBoolean(true) : fromBoolean(false);
    }

    public static Colour getRandomColour() {
        var randomValue = ThreadLocalRandom.current().nextInt(0, numberOfColours);
        return fromInt(randomValue);
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
}
