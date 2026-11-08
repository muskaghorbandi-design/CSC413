
package edu.sfsu.csc413.chess;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.view.BoardView;
import edu.sfsu.csc413.chess.view.ConsoleView;
import edu.sfsu.csc413.chess.view.EmojiView;
import edu.sfsu.csc413.chess.view.GameController;

/**
 * Starts a chess game using the selected view.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {

        // Create a new game.
        Game game = new Game();

        // Select the console or emoji view.
        BoardView view;

        if (args.length > 0 && args[0].equals("emoji")) {
            view = new EmojiView();
        } else {
            view = new ConsoleView();
        }

        // Connect the model and view.
        GameController controller =
                new GameController(game, view);

        // Start the game.
        controller.start();
    }
}
