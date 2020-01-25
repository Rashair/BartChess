package view.board;

import javafx.css.PseudoClass;
import javafx.scene.layout.StackPane;
import model.grid.Square;

import java.util.List;

public class HighlightManager {
    private final StackPane[][] panels;
    private final PseudoClass highlightMove;
    private final PseudoClass highlightCheck;

    private List<Square> currentlyHighlighted;
    private Square selectedSquare;
    private boolean isInCheck = true;

    HighlightManager(StackPane[][] panels) {
        this.panels = panels;
        highlightMove = PseudoClass.getPseudoClass("highlighted");
        highlightCheck = PseudoClass.getPseudoClass("in-check");
    }

    void setCheck(Square s, boolean val) {
        isInCheck = val;
        var squareView = panels[s.x][s.y];
        squareView.pseudoClassStateChanged(highlightCheck, val);
    }

    boolean isInCheck() {
        return isInCheck;
    }

    void set(List<Square> squares, Square selected) {
        currentlyHighlighted = squares;
        selectedSquare = selected;
        setActive(true);
    }

    boolean contains(Square s) {
        return currentlyHighlighted.contains(s);
    }

    void setActive(boolean val) {
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
        squareView.pseudoClassStateChanged(highlightMove, val);
    }
}
