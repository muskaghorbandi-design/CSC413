package edu.sfsu.csc413.chess.model;

/**
 * A single move: which piece went where, and what happened as a result.
 *
 * <p>A move records enough information to be undone. The captured piece is
 * stored so the move can be reversed later.
 *
 * @param from       the square the piece left
 * @param to         the square the piece arrived on
 * @param moved      the piece that moved
 * @param captured   the captured piece, or null if nothing was captured
 * @param promotesTo the promotion type, or null if this is not a promotion
 */
public record Move(Position from, Position to, Piece moved,
                   Piece captured, PieceType promotesTo) {

    /**
     * Creates a normal move to an empty square.
     */
    public static Move quiet(Position from, Position to, Piece moved) {
        return new Move(from, to, moved, null, null);
    }

    /**
     * Creates a move that captures another piece.
     */
    public static Move capture(Position from, Position to,
                               Piece moved, Piece captured) {
        return new Move(from, to, moved, captured, null);
    }

    /**
     * Creates a pawn promotion move.
     */
    public static Move promotion(Position from, Position to,
                                 Piece moved, Piece captured,
                                 PieceType promotesTo) {
        return new Move(from, to, moved, captured, promotesTo);
    }

    /**
     * Checks whether this move captures a piece.
     */
    public boolean isCapture() {
        return captured != null;
    }

    /**
     * Checks whether this move is a pawn promotion.
     */
    public boolean isPromotion() {
        return promotesTo != null;
    }

    /**
     * Returns the move in long algebraic form, such as e2e4 or e7e8q.
     */
    @Override
    public String toString() {
        String move = from.toString() + to.toString();

        if (isPromotion()) {
            move += Character.toLowerCase(promotesTo.symbol());
        }

        return move;
    }
}