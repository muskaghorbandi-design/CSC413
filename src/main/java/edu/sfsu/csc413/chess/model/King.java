package edu.sfsu.csc413.chess.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The king: one square in any of the eight directions.
 *
 * Castling is not implemented yet because it depends on
 * move history and king safety.
 */
public class King extends Piece {

    /**
     * The eight surrounding squares, as {file, rank} deltas.
     */
    private static final int[][] OFFSETS = {
            { 0, 1 },
            { 1, 1 },
            { 1, 0 },
            { 1, -1 },
            { 0, -1 },
            { -1, -1 },
            { -1, 0 },
            { -1, 1 }
    };

    public King(Color color) {
        super(color, PieceType.KING);
    }

    @Override
    public List<Move> pseudoLegalMoves(Board board, Position from) {
        List<Move> moves = new ArrayList<>();

        for (int[] offset : OFFSETS) {

            // Find the square the king would move to
            Position to = from.offsetOrNull(offset[0], offset[1]);

            // Ignore squares outside the board
            if (to == null) {
                continue;
            }

            Piece pieceAtDestination = board.pieceAt(to);

            // Empty square = normal move
            if (pieceAtDestination == null) {
                moves.add(Move.quiet(from, to, this));
            }

            // Opponent piece = capture
            else if (pieceAtDestination.color() != color()) {
                moves.add(Move.capture(
                        from,
                        to,
                        this,
                        pieceAtDestination
                ));
            }
        }

        return moves;
    }
}