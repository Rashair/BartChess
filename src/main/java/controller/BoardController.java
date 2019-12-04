package controller;

import model.Colour;
import model.GameModel;
import model.grid.Board;
import model.grid.Move;
import model.grid.Square;
import model.players.Computer;
import model.players.Human;
import model.rules.IJudge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BoardController {
    private final IJudge judge;
    private final GameModel model;
    private final Board board;

    public BoardController(GameModel model) {
        this.model = model;
        this.board = model.getBoard();
        this.judge = model.getJudge();
    }

    public void InitializeGame() {
        board.initializePieces(judge.getInitialPositionsForAllPieces());
        Colour randomColour = Colour.getRandomColour();
        var player1 = new Human(randomColour);
        var player2 = new Computer(randomColour.getOppositeColour());
    }

    public void NewGame() {

        //var interaction = new Interaction(logic, player1, player2);
    }

    public boolean isEmptySquare(int row, int col) {
        return board.isEmptySquare(row, col);
    }

    public String getSquareDisplay(int row, int col) {
        return board.getPiece(row, col).toString();
    }

    public List<Square> getValidMoves(int row, int col) {
        var piece = board.getPiece(row, col);
        if (piece != null) {
            // TODO: Cache moves
            List<Move> moves = piece.getValidMoves(judge, row, col);
            return Arrays.stream(moves.toArray()).map(move -> ((Move) move).getDestination()).collect(Collectors.toList());
        }

        return new ArrayList<Square>();
    }
}
