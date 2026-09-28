package edu.sfsu.csc413.chess.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A knight chess piece.
 *
 * A knight moves in an L-shape and can jump over other pieces.
 */
public class Knight extends Piece {

    /**
     * All eight possible knight moves.
     */
    private static final int[][] OFFSETS = {
            { 1, 2 },
            { 2, 1 },
            { 2, -1 },
            { 1, -2 },
            { -1, -2 },
            { -2, -1 },
            { -2, 1 },
            { -1, 2 }
    };

    public Knight(Color color) {
        super(color, PieceType.KNIGHT);
    }

    @Override
    public List<Move> pseudoLegalMoves(Board board, Position from) {
        List<Move> moves = new ArrayList<>();

        for (int[] offset : OFFSETS) {

            // Find the square the knight would move to
            Position to = from.offsetOrNull(offset[0], offset[1]);

            // Ignore moves that go outside the board
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