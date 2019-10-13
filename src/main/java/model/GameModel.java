package model;

import model.grid.Board;
import model.pieces.PieceFactory;
import model.rules.ClassicJudge;
import model.rules.IJudge;

public class GameModel {
    private final GameLogic logic;
    private final GameState state;
    private final PieceFactory factory;
    private final IJudge judge;
    private final Board board;

    public GameModel() {
        factory = PieceFactory.getInstance();
        board = new Board(factory);
        state = new GameState();
        judge = new ClassicJudge(board, state);
        logic = new GameLogic(board, judge);
    }

    public GameLogic getLogic() {
        return logic;
    }

    public IJudge getJudge() {
        return judge;
    }

    public Board getBoard() {
        return board;
    }

    public GameState getState() {
        return state;
    }
}
