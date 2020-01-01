package model.rules;

import model.Colour;
import model.grid.Board;
import model.grid.Square;

import java.util.HashSet;
import java.util.Set;

class MovesCreator {
    private final Board board;
    private final Colour pieceColour;
    private final int x;
    private final int y;

    private Set<Square> currentPositions;

    MovesCreator(Board board, Colour pieceColour, int x, int y) {
        this.board = board;
        this.pieceColour = pieceColour;
        this.x = x;
        this.y = y;
    }


    Set<Square> getVerticalAndHorizontalMoves() {
        setupCurrentPositions();
        for (int i = x + 1; i < Board.rowsNum; ++i) {
            if (addPositionAndCheckIfShouldStop(i, y)) {
                break;
            }
        }

        for (int i = x - 1; i >= 0; --i) {
            if (addPositionAndCheckIfShouldStop(i, y)) {
                break;
            }
        }

        for (int j = y + 1; j < Board.columnsNum; ++j) {
            if (addPositionAndCheckIfShouldStop(x, j)) {
                break;
            }
        }

        for (int j = y - 1; j >= 0; --j) {
            if (addPositionAndCheckIfShouldStop(x, j)) {
                break;
            }
        }

        return currentPositions;
    }

    Set<Square> getDiagonalMoves() {
        setupCurrentPositions();
        for (int i = x + 1, j = y - 1; i < Board.rowsNum && j >= 0; ++i, --j) {
            if (addPositionAndCheckIfShouldStop(i, j)) {
                break;
            }
        }

        for (int i = x + 1, j = y + 1; i < Board.rowsNum && j < Board.columnsNum; ++i, ++j) {
            if (addPositionAndCheckIfShouldStop(i, j)) {
                break;
            }
        }

        for (int i = x - 1, j = y - 1; i >= 0 && j >= 0; --i, --j) {
            if (addPositionAndCheckIfShouldStop(i, j)) {
                break;
            }
        }

        for (int i = x - 1, j = y + 1; i >= 0 && j < Board.columnsNum; --i, ++j) {
            if (addPositionAndCheckIfShouldStop(i, j)) {
                break;
            }
        }

        return currentPositions;
    }


    private void setupCurrentPositions() {
        currentPositions = new HashSet<>(Board.rowsNum + Board.columnsNum);
    }

    private boolean addPositionAndCheckIfShouldStop(int i, int j) {
        var currentPiece = board.getPiece(i, j);
        if (currentPiece != null) {
            if (currentPiece.colour != pieceColour) {
                currentPositions.add(new Square(i, j));
            }
            return true;
        }

        currentPositions.add(new Square(i, j));
        return false;
    }
}
