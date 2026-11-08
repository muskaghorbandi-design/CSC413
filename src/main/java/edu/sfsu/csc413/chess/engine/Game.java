
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
     */
    public List<Move> history() {
        return List.copyOf(history);
    }

    /**
     * Returns all legal moves for the side to move.
     */
    public List<Move> legalMoves() {
        return MoveGenerator.legalMoves(board, sideToMove);
    }

    /**
     * Finds a legal move matching the given notation.
     *
     * Examples: e2e4 or e7e8q.
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

        if (!legalMoves().contains(move)) {
            throw new IllegalArgumentException(
                    "Illegal move: " + move
            );
        }

        board.apply(move);

        history.add(move);

        sideToMove = sideToMove.opposite();
    }

    /**
     * Undoes the most recently played move.
     */
    public Optional<Move> undoLastMove() {

        if (history.isEmpty()) {
            return Optional.empty();
        }

        Move move = history.remove(history.size() - 1);

        board.undo(move);

        sideToMove = sideToMove.opposite();

        return Optional.of(move);
    }

    // M6: Game status

    /**
     * Returns the current game status.
     *
     * The MoveGenerator determines whether the game
     * is in progress, check, checkmate, or stalemate.
     */
    public GameStatus status() {
        return MoveGenerator.status(board, sideToMove);
    }

    /**
     * Returns true if the game has ended
     * in checkmate or stalemate.
     */
    public boolean isGameOver() {
        return status().isGameOver();
    }
}
