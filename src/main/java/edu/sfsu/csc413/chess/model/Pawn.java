package edu.sfsu.csc413.chess.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The pawn — the piece that breaks every rule the others follow.
 *
 * Pawns move forward, capture diagonally, can move two squares
 * from their starting position, and promote on the last rank.
 */
public class Pawn extends Piece {

    /**
     * What a pawn may become on reaching the far rank.
     */
    private static final PieceType[] PROMOTION_CHOICES = {
            PieceType.QUEEN,
            PieceType.ROOK,
            PieceType.BISHOP,
            PieceType.KNIGHT
    };

    public Pawn(Color color) {
        super(color, PieceType.PAWN);
    }

    @Override
    public List<Move> pseudoLegalMoves(Board board, Position from) {
        List<Move> moves = new ArrayList<>();

        // White moves up the board, black moves down
        int direction = color() == Color.WHITE ? 1 : -1;

        // Starting rank for the two-square move
        int startingRank = color() == Color.WHITE ? 1 : 6;

        // Last rank where promotion happens
        int promotionRank = color() == Color.WHITE ? 7 : 0;

        // ----- Move forward one square -----

        Position oneForward = from.offsetOrNull(0, direction);

        if (oneForward != null && board.isEmpty(oneForward)) {

            if (oneForward.rank() == promotionRank) {
                addPromotions(moves, from, oneForward, null);
            } else {
                moves.add(Move.quiet(from, oneForward, this));
            }

            // ----- Move forward two squares -----
            // Only allowed from the pawn's starting rank.
            // The first square must also be empty.
            if (from.rank() == startingRank) {
                Position twoForward = from.offsetOrNull(0, direction * 2);

                if (twoForward != null && board.isEmpty(twoForward)) {
                    moves.add(Move.quiet(from, twoForward, this));
                }
            }
        }

        // ----- Capture diagonally -----

        int[] fileDirections = { -1, 1 };

        for (int fileDirection : fileDirections) {
            Position diagonal =
                    from.offsetOrNull(fileDirection, direction);

            if (diagonal == null) {
                continue;
            }

            Piece captured = board.pieceAt(diagonal);

            // A pawn can only capture an opponent's piece
            if (captured != null && captured.color() != color()) {

                if (diagonal.rank() == promotionRank) {
                    addPromotions(
                            moves,
                            from,
                            diagonal,
                            captured
                    );
                } else {
                    moves.add(
                            Move.capture(
                                    from,
                                    diagonal,
                                    this,
                                    captured
                            )
                    );
                }
            }
        }

        return moves;
    }

    /**
     * Adds all four possible promotion moves.
     */
    private void addPromotions(
            List<Move> moves,
            Position from,
            Position to,
            Piece captured) {

        for (PieceType promotion : PROMOTION_CHOICES) {
            moves.add(
                    Move.promotion(
                            from,
                            to,
                            this,
                            captured,
                            promotion
                    )
            );
        }
    }

    /**
     * A pawn attacks the two squares diagonally ahead of it,
     * whether or not a piece is standing there.
     */
    @Override
    public boolean attacks(
            Board board,
            Position from,
            Position target) {

        int direction = color() == Color.WHITE ? 1 : -1;

        Position left =
                from.offsetOrNull(-1, direction);

        Position right =
                from.offsetOrNull(1, direction);

        return target.equals(left) || target.equals(right);
    }
}
