package model;

import model.grid.Board;
import model.rules.IJudge;

public class GameLogic {
    private Board board;
    private IJudge judge;

    public GameLogic(Board board, IJudge judge) {
        this.board = board;
        this.judge = judge;
    }


}
