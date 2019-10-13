package model;

import model.grid.Board;
import model.rules.IJudge;

public class GameLogic {
    private final Board board;
    private final IJudge judge;

    public GameLogic(Board board, IJudge judge) {
        this.board = board;
        this.judge = judge;
    }


}
