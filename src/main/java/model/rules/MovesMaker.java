package model.rules;

import model.Colour;
import model.grid.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

class MovesMaker {
    private final Board board;
    private final Colour pieceColour;
    private final int x;
    private final int y;

    private List<Square> currentPositions;

    MovesMaker(Board board, Colour pieceColour, int x, int y) {
        this.board = board;
        this.pieceColour = pieceColour;
        this.x = x;
        this.y = y;
    }


    List<Move> getVerticalAndHorizontalMoves() {
        currentPositions = new ArrayList<>(Board.rowsNum + Board.columnsNum);
        for (int i = x + 1; i < Board.rowsNum; ++i) {
            if (!addPositionAndCheckIfCanContinue(i, y))
                break;
        }

        for (int i = x - 1; i >= 0; --i) {
            if (!addPositionAndCheckIfCanContinue(i, y))
                break;
        }

        for (int j = y + 1; j < Board.columnsNum; ++j) {
            if (!addPositionAndCheckIfCanContinue(x, j))
                break;
        }

        for (int j = y - 1; j >= 0; --j) {
            if (!addPositionAndCheckIfCanContinue(x, j))
                break;
        }

        return Move.createMovesFromSource(new Square(x, y), currentPositions.toArray(Square[]::new));
    }

    List<Move> getDiagonalMoves() {
        currentPositions = new ArrayList<>(Board.rowsNum + Board.columnsNum);
        for (int i = x + 1, j = y - 1; i < Board.rowsNum && j >= 0; ++i, --j) {
            if (!addPositionAndCheckIfCanContinue(i, j))
                break;
        }

        for (int i = x + 1, j = y + 1; i < Board.rowsNum && j < Board.columnsNum; ++i, ++j) {
            if (!addPositionAndCheckIfCanContinue(i, j))
                break;
        }

        for (int i = x - 1, j = y - 1; i >= 0 && j >= 0; --i, --j) {
            if (!addPositionAndCheckIfCanContinue(i, j))
                break;
        }

        for (int i = x - 1, j = y + 1; i >= 0 && j < Board.columnsNum; --i, ++j) {
            if (!addPositionAndCheckIfCanContinue(i, j))
                break;
        }

        return Move.createMovesFromSource(new Square(x, y), currentPositions.toArray(Square[]::new));
    }

    private boolean addPositionAndCheckIfCanContinue(int i, int j) {
        var currentPiece = board.getPiece(i, j);
        if (currentPiece != null) {
            if (currentPiece.colour != pieceColour)
                currentPositions.add(new Square(i, j));
            return false;
        }

        currentPositions.add(new Square(i, j));
        return true;
    }
}
