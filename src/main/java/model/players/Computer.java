package model.players;

import model.Colour;
import model.game.Logic;
import model.game.MoveTrace;
import model.grid.Board;
import model.grid.Move;
import model.grid.Square;

import java.util.List;
import java.util.Locale;

public class Computer extends Player {
    private final Logic logic;
    private final Board board;

    public Computer(Colour colour, Logic logic, Board board) {
        super(colour);
        this.logic = logic;
        this.board = board;
    }

    public Move chooseMove() {
        for (int i = 0; i < Board.rowsNum; ++i) {
            for (int j = 0; j < Board.columnsNum; ++j) {
                var piece = board.getPiece(i, j);
                if (piece != null && piece.colour == this.getColour()) {
                    var moves = logic.getValidMoves(i, j);
                    if (!moves.isEmpty()) {
                        return moves.get(0);
                    }
                }
            }
        }

        return null;
    }

    public MoveTrace makeMove() {
        var move = chooseMove();
        if (move != null) {
            return logic.makeMove(move);
        }

        return new MoveTrace();
    }
}
