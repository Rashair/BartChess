package model.players;

import model.Colour;
import model.game.Logic;
import model.game.MoveTrace;
import model.grid.Board;
import model.grid.Move;
import model.grid.Square;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ComputerLogic {
    private final Logic logic;
    private final Board board;

    public ComputerLogic(Logic logic, Board board) {
        this.logic = logic;
        this.board = board;
    }

    private Move chooseMove(Colour colour) {
        List<Move> allMoves = new LinkedList<>();
        for (int i = 0; i < Board.rowsNum; ++i) {
            for (int j = 0; j < Board.columnsNum; ++j) {
                var piece = board.getPiece(i, j);
                if (piece != null && piece.colour == colour) {
                    var moves = logic.getValidMoves(i, j);
                    allMoves.addAll(moves);
                }
            }
        }

        return allMoves.get(new Random().nextInt(allMoves.size()));
    }

    public MoveTrace makeMove(Colour colour) {
        var move = chooseMove(colour);
        if (move != null) {
            return logic.makeMove(move);
        }

        return new MoveTrace();
    }
}
