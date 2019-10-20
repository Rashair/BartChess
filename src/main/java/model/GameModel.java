package model;

import model.game.Interaction;
import model.game.Logic;
import model.game.State;
import model.grid.Board;
import model.pieces.PieceFactory;
import model.players.Computer;
import model.players.Human;
import model.rules.ClassicJudge;
import model.rules.IJudge;

// TODO: Must start implementing MVC pattern - controller will start game
public class GameModel {
    private final Logic logic;
    private final State state;
    private final IJudge judge;
    private final Board board;

    public GameModel() {
        PieceFactory factory = PieceFactory.getInstance();
        board = new Board(factory);
        state = new State();
        judge = new ClassicJudge(board, state);
        logic = new Logic(board, judge, state);
    }

    public Logic getLogic() {
        return logic;
    }

    public IJudge getJudge() {
        return judge;
    }

    public Board getBoard() {
        return board;
    }

    public State getState() {
        return state;
    }

    public void NewGame() {
        board.initializePieces(judge.getInitialPositionsForAllPieces());
        Colour randomColour = Colour.getRandomColour();
        var player1 = new Human(randomColour);
        var player2 = new Computer(randomColour.getOppositeColour());
        var interaction = new Interaction(logic, player1, player2);
    }
}
