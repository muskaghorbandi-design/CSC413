
package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.engine.GameStatus;
import edu.sfsu.csc413.chess.model.Move;
import edu.sfsu.csc413.chess.model.PieceType;

import java.util.List;
import java.util.Optional;

/**
 * Connects player actions to the chess game.
 * The model handles rules, and the view handles display.
 */
public class GameController {

    private final Game game;
    private final BoardView view;
    private boolean stopped;

    public GameController(Game game, BoardView view) {
        this.game = game;
        this.view = view;
    }

    /**
     * Returns the game used by this controller.
     */
    public Game game() {
        return game;
    }

    /**
     * Welcomes the player, draws the board,
     * and starts the selected view.
     */
    public void start() {
        view.showMessage("Welcome to Chess!");
        view.render(game);
        view.start(this);
    }

    /**
     * Handles actions until the game ends
     * or the player chooses to stop.
     */
    public void runLoop() {
        while (!stopped && !game.isGameOver()) {
            PlayerAction action = view.nextAction(game);
            handle(action);
        }
    }

    /**
     * Handles one action and updates the view.
     */
    public void handle(PlayerAction action) {

        if (stopped || game.isGameOver()) {
            return;
        }

        switch (action) {
            case PlayerAction.Move moveAction -> {
                String notation = moveAction.notation();
                Optional<Move> move =
                        game.findLegalMove(notation);

                // Ask for a promotion piece if one was not given.
                if (move.isEmpty() && notation.length() == 4) {
                    PieceType choice = view.promotionChoice(game);

                    String promotionNotation = notation
                            + Character.toLowerCase(choice.symbol());

                    move = game.findLegalMove(promotionNotation);
                }

                if (move.isPresent()) {
                    game.play(move.get());
                } else {
                    view.showMessage(
                            "Not a legal move: '" + notation + "'."
                    );
                }
            }

            case PlayerAction.Undo ignored -> {
                if (game.undoLastMove().isEmpty()) {
                    view.showMessage("Nothing to undo.");
                }
            }

            case PlayerAction.ShowMoves ignored -> {
                List<String> moves = game.legalMoves()
                        .stream()
                        .map(Move::toString)
                        .sorted()
                        .toList();

                view.showMessage(
                        moves.size() + " legal moves: "
                                + String.join(" ", moves)
                );
            }

            case PlayerAction.Quit ignored -> {
                stopped = true;
                view.showMessage("Goodbye.");
                view.close();
                return;
            }
        }

        view.render(game);

        if (game.isGameOver()) {
            GameStatus status = game.status();

            if (status == GameStatus.CHECKMATE) {
                view.showMessage(
                        "Checkmate. "
                                + game.sideToMove().opposite()
                                + " wins."
                );
            } else if (status == GameStatus.STALEMATE) {
                view.showMessage(
                        "Stalemate. The game is a draw."
                );
            }

            stopped = true;
            view.close();
        }
    }
}
