package model.pieces;

import model.Colour;

import java.lang.reflect.InvocationTargetException;

public class PieceFactory {
    private static PieceFactory factory;

    private PieceFactory() {}

    public static PieceFactory getInstance() {
        if (factory == null) {
            factory = new PieceFactory();
            return factory;
        }

        throw new SecurityException("You cannot create another instance of " + PieceFactory.class.getName());
    }

    public <T extends Piece> Piece create(Class<T> tClass, Colour color) {
        try {
            return tClass.getDeclaredConstructor(Colour.class).newInstance(color);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException("Piece " + tClass.getName() + " does not exists or it does not have " +
                    "appropriate constructor.", e);
        }
    }
}
