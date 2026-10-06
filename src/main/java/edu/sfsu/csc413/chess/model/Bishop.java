package edu.sfsu.csc413.chess.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The bishop: slides any distance along the four diagonals.
 */
public class Bishop extends Piece {

    /**
     * The four diagonal directions, as {file, rank} deltas.
     */
    private static final int[][] DIRECTIONS = {
            { 1, 1 },
            { 1, -1 },
            { -1, -1 },
            { -1, 1 }
    };

    public Bishop(Color color) {
        super(color, PieceType.BISHOP);
    }

    @Override
    public List<Move> pseudoLegalMoves(Board board, Position from) {
        List<Move> moves = new ArrayList<>();

        for (int[] direction : DIRECTIONS) {
            int fileStep = direction[0];
            int rankStep = direction[1];

            int distance = 1;

            while (true) {
                Position to = from.offsetOrNull(
                        fileStep * distance,
                        rankStep * distance
                );

                // Stop when we reach the edge of the board
                if (to == null) {
                    break;
                }

                Piece pieceAtDestination = board.pieceAt(to);

                // Empty square: bishop can move there and keep going
                if (pieceAtDestination == null) {
                    moves.add(Move.quiet(from, to, this));
                }

                // Opponent piece: bishop can capture it, but cannot go past it
                else if (pieceAtDestination.color() != color()) {
                    moves.add(Move.capture(
                            from,
                            to,
                            this,
                            pieceAtDestination
                    ));
                    break;
                }

                // Our own piece blocks the bishop
                else {
                    break;
                }

                distance++;
            }
        }

        return moves;
    }
}