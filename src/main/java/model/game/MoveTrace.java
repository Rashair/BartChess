package model.game;

import model.Colour;
import model.grid.Move;

public class MoveTrace {
    public Move move;
    private boolean isGameOver;
    private Colour winner;

    public MoveTrace() {
    }

    public MoveTrace(Move move) {
        this.move = move;
        isGameOver = false;
        winner = null;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(Colour winner) {
        isGameOver = true;
        this.winner = winner;
    }

    public boolean isDraw() {
        return isGameOver() && this.winner == null;
    }

    public Colour getWinner() {
        return winner;
    }

    public boolean isValid() {
        return move != null;
    }
}
