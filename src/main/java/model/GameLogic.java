package model;

import model.grid.Board;
import model.rules.IJudge;

// TODO: Add player interaction and making moves -> changing state
public class GameLogic {
    private final Board board;
    private final IJudge judge;

    public GameLogic(Board board, IJudge judge) {
        this.board = board;
        this.judge = judge;
    }


}
