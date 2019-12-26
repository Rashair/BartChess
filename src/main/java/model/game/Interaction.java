package model.game;

import model.Colour;
import model.players.Player;

public class Interaction {
    private final Logic logic;
    private final Player[] players;
    private int currentTurnPlayerIndex;

    public Interaction(Logic logic, Player player1, Player player2) {
        this.logic = logic;
        players = new Player[Colour.getNumberOfColours()];
        players[player1.getColour().getIntValue()] = player1;
        players[player2.getColour().getIntValue()] = player2;
    }

    public void StartGame() {
        currentTurnPlayerIndex = Colour.White.getIntValue();
        while (!logic.isGameOver()) {
            int previousPlayerIndex = currentTurnPlayerIndex;
            while (previousPlayerIndex == currentTurnPlayerIndex) {
                makePlayerMove(players[currentTurnPlayerIndex]);
            }
        }

        System.out.println("Winner is " + Colour.fromInt(currentTurnPlayerIndex).toString() + " player.");
    }

    private void makePlayerMove(Player player) {
        var square = player.chooseSourceSquare();
        var validMoves = logic.getValidMoves(square);
        var move = player.chooseMove(validMoves);
        if (move == null)
            return;

        logic.makeMove(move, player.getColour());
        currentTurnPlayerIndex = player.getColour().getOppositeColour().getIntValue();
    }
}
