package view;

import javafx.css.PseudoClass;
import javafx.scene.layout.StackPane;
import model.grid.Square;

import java.util.List;

public class HighlightManager {
    private final PseudoClass highlight;
    private StackPane[][] panels;

    private List<Square> currentlyHighlighted;
    private Square selectedSquare;

    HighlightManager(StackPane[][] panels) {
        this.panels = panels;
        highlight = PseudoClass.getPseudoClass("highlighted");
    }

    void set(List<Square> squares, Square selected) {
        currentlyHighlighted = squares;
        selectedSquare = selected;
    }

    boolean contains(Square s) {
        return currentlyHighlighted.contains(s);
    }

    void set(boolean val) {
        if (currentlyHighlighted == null) {
            return;
        }

        setHighlightForSquare(selectedSquare, val);
        for (Square square : currentlyHighlighted) {
            setHighlightForSquare(square, val);
        }

        if (!val) {
            currentlyHighlighted = null;
        }
    }

    private void setHighlightForSquare(Square s, boolean val) {
        var squareView = panels[s.x][s.y];
        squareView.pseudoClassStateChanged(highlight, val);
    }

}
