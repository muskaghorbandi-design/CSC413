
package edu.sfsu.csc413.chess.engine;

import edu.sfsu.csc413.chess.factory.BoardFactory;
import edu.sfsu.csc413.chess.model.Board;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * One game of chess: the board, whose turn it is,
 * and the moves played so far.
 */
public class Game {

    private final Board board;

    private final List<Move> history = new ArrayList<>();

    private Color sideToMove;

    /**
     * Starts a game from the standard opening position.
     */
    public Game() {
        this(BoardFactory.standard(), Color.WHITE);
    }

    /**
     * Starts a game from a given board and side to move.
     */
    public Game(Board board, Color sideToMove) {
        this.board = board;
        this.sideToMove = sideToMove;
    }

    /**
     * Returns the board for this game.
     */
    public Board board() {
        return board;
    }

    /**
     * Returns the side whose turn it is.
     */
    public Color sideToMove() {
        return sideToMove;
    }

    /**
     * Returns a copy of the moves played so far.
     *
     * Returning a copy prevents callers from changing
     * the game's private history.
     */
    public List<Move> history() {
        return List.copyOf(history);
    }

    /**
     * Returns every pseudo-legal move for the side to move.
     *
     * In M4, MoveGenerator handles move generation.
     * King safety will be handled in M5.
     */
    public List<Move> legalMoves() {
        return MoveGenerator.legalMoves(board, sideToMove);
    }

    /**
     * Finds a legal move that matches the given notation.
     *
     * For example: e2e4 or e7e8q.
     */
    public Optional<Move> findLegalMove(String notation) {
        for (Move move : legalMoves()) {
            if (move.toString().equalsIgnoreCase(notation)) {
                return Optional.of(move);
            }
        }

        return Optional.empty();
    }

    /**
     * Plays a legal move and passes the turn.
     */
    public void play(Move move) {

        // Game decides whether the move is legal.
        if (!legalMoves().contains(move)) {
            throw new IllegalArgumentException(
                    "Illegal move: " + move
            );
        }

        // Apply the move to the board.
        board.apply(move);

        // Remember the move so it can be undone later.
        history.add(move);

        // Give the turn to the other side.
        sideToMove = sideToMove.opposite();
    }

    /**
     * Undoes the most recently played move.
     */
    public Optional<Move> undoLastMove() {

        // There is nothing to undo.
        if (history.isEmpty()) {
            return Optional.empty();
        }

        // Remove the most recent move.
        Move move = history.remove(history.size() - 1);

        // Restore the board.
        board.undo(move);

        // Give the turn back to the previous player.
        sideToMove = sideToMove.opposite();

        return Optional.of(move);
    }
}
