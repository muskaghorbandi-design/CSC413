package edu.sfsu.csc413.chess.model;

import java.util.List;

/**
 * A chess piece with a color and type.
 *
 * Each specific piece decides how it can move by implementing
 * pseudoLegalMoves.
 */
public abstract class Piece {

    private final Color color;
    private final PieceType type;

    protected Piece(Color color, PieceType type) {
        this.color = color;
        this.type = type;
    }

    public Color color() {
        return color;
    }

    public PieceType type() {
        return type;
    }

    public char symbol() {
        char symbol = type.symbol();

        if (color == Color.BLACK) {
            return Character.toLowerCase(symbol);
        }

        return symbol;
    }

    /**
     * Returns the moves this piece can make from the given position.
     */
    public abstract List<Move> pseudoLegalMoves(Board board, Position from);

    /**
     * Checks whether this piece attacks the target square.
     */
    public boolean attacks(Board board, Position from, Position target) {
        return pseudoLegalMoves(board, from).stream()
                .anyMatch(move -> move.to().equals(target));
    }

    @Override
    public String toString() {
        return String.valueOf(symbol());
    }
}