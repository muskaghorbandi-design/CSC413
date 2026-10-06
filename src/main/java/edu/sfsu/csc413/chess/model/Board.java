package edu.sfsu.csc413.chess.model;

import edu.sfsu.csc413.chess.factory.PieceFactory;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private final Piece[][] squares;

    public Board() {
        squares = new Piece[8][8];
    }

    public Piece pieceAt(Position position) {
        return squares[position.file()][position.rank()];
    }

    public boolean isEmpty(Position position) {
        return pieceAt(position) == null;
    }

    public void place(Position position, Piece piece) {
        squares[position.file()][position.rank()] = piece;
    }

    public List<Position> positionsOf(Color color) {
        List<Position> positions = new ArrayList<>();

        for (int file = 0; file < 8; file++) {
            for (int rank = 0; rank < 8; rank++) {
                Piece piece = squares[file][rank];

                if (piece != null && piece.color() == color) {
                    positions.add(new Position(file, rank));
                }
            }
        }

        return positions;
    }

    /**
     * Applies a move to the board.
     * Game is responsible for deciding whether the move is legal.
     */
    public void apply(Move move) {
        // Remove the piece from its original position.
        place(move.from(), null);

        if (move.isPromotion()) {
            /*
             * For M3, promotion creates a new piece using PieceFactory.
             * This creates a model -> factory dependency, which is an
             * accepted tradeoff for this milestone.
             */
            place(
                    move.to(),
                    PieceFactory.create(
                            move.promotesTo(),
                            move.moved().color()
                    )
            );
        } else {
            // Normal move: put the moving piece at its destination.
            place(move.to(), move.moved());
        }
    }

    /**
     * Undoes a previously applied move.
     */
    public void undo(Move move) {
        // Put the original moving piece back.
        place(move.from(), move.moved());

        // Restore the captured piece.
        // This is null when nothing was captured.
        place(move.to(), move.captured());
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (int rank = 7; rank >= 0; rank--) {
            int emptyCount = 0;

            for (int file = 0; file < 8; file++) {
                Piece piece = squares[file][rank];

                if (piece == null) {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        result.append(emptyCount);
                        emptyCount = 0;
                    }

                    result.append(piece.symbol());
                }
            }

            if (emptyCount > 0) {
                result.append(emptyCount);
            }

            if (rank > 0) {
                result.append('/');
            }
        }

        return result.toString();
    }
}