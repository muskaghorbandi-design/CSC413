
package edu.sfsu.csc413.chess.engine;

import edu.sfsu.csc413.chess.model.Board;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Move;
import edu.sfsu.csc413.chess.model.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates the moves a side can make on the board.
 * M4 moves this logic out of Game to keep the classes
 * responsible for their own tasks.
 */
public final class MoveGenerator {

    private MoveGenerator() {
    }

    /**
     * Returns all legal moves for the given color.
     * For M4, legal moves and pseudo-legal moves are the same.
     */
    public static List<Move> legalMoves(Board board, Color color) {
        return pseudoLegalMoves(board, color);
    }

    /**
     * Collects all possible moves for pieces of the given color.
     * King safety will be handled in M5.
     */
    static List<Move> pseudoLegalMoves(Board board, Color color) {
        List<Move> moves = new ArrayList<>();

        for (Position from : board.positionsOf(color)) {
            moves.addAll(board.pieceAt(from).pseudoLegalMoves(board, from));
        }

        return moves;
    }
}
