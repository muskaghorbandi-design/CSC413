
package edu.sfsu.csc413.chess.engine;

import edu.sfsu.csc413.chess.model.Board;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Move;
import edu.sfsu.csc413.chess.model.Piece;
import edu.sfsu.csc413.chess.model.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates legal moves for a side on the board.
 * Also checks attacks and king safety.
 */
public final class MoveGenerator {

    private MoveGenerator() {
    }

    /**
     * Returns moves that do not leave the given color's
     * king in check. The board is restored after each move.
     */
    public static List<Move> legalMoves(Board board, Color color) {
        List<Move> legal = new ArrayList<>();

        for (Move move : pseudoLegalMoves(board, color)) {
            board.apply(move);

            boolean inCheck;
            try {
                inCheck = isInCheck(board, color);
            } finally {
                board.undo(move);
            }

            if (!inCheck) {
                legal.add(move);
            }
        }

        return legal;
    }

    /**
     * Returns true if any piece of the attacking color
     * attacks the target square.
     *
     * @param board the current board
     * @param target the square being checked
     * @param attacker the attacking color
     * @return true if the target is attacked
     */
    public static boolean isAttacked(
            Board board, Position target, Color attacker) {

        for (Position from : board.positionsOf(attacker)) {
            Piece piece = board.pieceAt(from);

            if (piece.attacks(board, from, target)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if the given color's king is attacked
     * by the opposite color. A missing king is not in check.
     *
     * @param board the current board
     * @param color the king's color
     * @return true if the king is in check
     */
    public static boolean isInCheck(Board board, Color color) {
        Position king = board.kingPosition(color);

        if (king == null) {
            return false;
        }

        return isAttacked(board, king, color.opposite());
    }

    /**
     * Collects pseudo-legal moves for all pieces
     * of the given color.
     */
    static List<Move> pseudoLegalMoves(Board board, Color color) {
        List<Move> moves = new ArrayList<>();

        for (Position from : board.positionsOf(color)) {
            moves.addAll(
                    board.pieceAt(from).pseudoLegalMoves(board, from)
            );
        }

        return moves;
    }
}
