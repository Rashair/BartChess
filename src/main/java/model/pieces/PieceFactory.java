package model.pieces;

import model.Colour;

import java.lang.reflect.InvocationTargetException;

public class PieceFactory {
    private static PieceFactory factory;

    private PieceFactory() {
    }

    public static PieceFactory getInstance() {
        if (factory == null) {
            factory = new PieceFactory();
        }

        return factory;
    }

    public Piece create(Class<? extends Piece> tClass, Colour color) {
        try {
            return tClass.getDeclaredConstructor(Colour.class).newInstance(color);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException("Piece " + tClass.getName() + " does not exists or it does not have " +
                    "appropriate constructor.", e);
        }
    }
}
