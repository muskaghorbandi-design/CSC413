package edu.sfsu.csc413.chess.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The queen: slides along both straight and diagonal directions.
 */
public class Queen extends Piece {

    /**
     * All eight directions:
     * four straight and four diagonal.
     */
    private static final int[][] DIRECTIONS = {
            { 0, 1 },
            { 1, 0 },
            { 0, -1 },
            { -1, 0 },
            { 1, 1 },
            { 1, -1 },
            { -1, -1 },
            { -1, 1 }
    };

    public Queen(Color color) {
        super(color, PieceType.QUEEN);
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

                // Stop when the queen reaches the edge of the board
                if (to == null) {
                    break;
                }

                Piece pieceAtDestination = board.pieceAt(to);

                // Empty square: queen can move there and keep going
                if (pieceAtDestination == null) {
                    moves.add(Move.quiet(from, to, this));
                }

                // Opponent piece: capture it and stop in this direction
                else if (pieceAtDestination.color() != color()) {
                    moves.add(Move.capture(
                            from,
                            to,
                            this,
                            pieceAtDestination
                    ));
                    break;
                }

                // Our own piece blocks the queen
                else {
                    break;
                }

                distance++;
            }
        }

        return moves;
    }
}