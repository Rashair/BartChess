package model.rules;

import model.Colour;
import model.grid.Move;
import model.pieces.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public interface IJudge {
    List<Move> getValidMoves(King king, int x, int y);

    List<Move> getValidMoves(Queen queen, int x, int y);

    List<Move> getValidMoves(Rook rook, int x, int y);

    List<Move> getValidMoves(Bishop bishop, int x, int y);

    List<Move> getValidMoves(Knight knight, int x, int y);

    List<Move> getValidMoves(Pawn pawn, int x, int y);

    boolean isKingInCheck(Colour kingColour);

    boolean areAnyValidMovesForPlayer(Colour playerColour);

    Map<Pair<Class<? extends Piece>, Colour>, String> getInitialPositionsForAllPieces();
}
