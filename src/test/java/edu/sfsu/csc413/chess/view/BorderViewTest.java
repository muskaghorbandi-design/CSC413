package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.engine.GameStatus;
import edu.sfsu.csc413.chess.factory.BoardFactory;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.PieceType;
import edu.sfsu.csc413.chess.model.Position;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class BorderViewTest {
    @Test
    void playsCompleteGameAndFlipsBothAxes() {
        var output = new ByteArrayOutputStream();
        var view = new BorderView(new PrintStream(output),
                new Scanner("f2f3\ne7e5\ng2g4\nd8h4\n"));
        Game game = new Game();
        new GameController(game, view).start();

        assertEquals(GameStatus.CHECKMATE, game.status());
        assertEquals(4, game.history().size());
        String text = output.toString();
        assertTrue(text.contains("8 | r | n | b | q | k | b | n | r | 8"));
        assertTrue(text.contains("1 | R | N | B | K | Q | B | N | R | 1"));
        assertTrue(text.contains("    a   b   c   d   e   f   g   h"));
        assertTrue(text.contains("    h   g   f   e   d   c   b   a"));
        assertTrue(text.contains("Checkmate. BLACK wins."));
    }

    @Test
    void handlesIllegalMovesUndoMoveListingAndEndOfInput() {
        var output = new ByteArrayOutputStream();
        var view = new BorderView(new PrintStream(output),
                new Scanner("e2e5\ne2e4\nundo\nmoves\n"));
        Game game = new Game();
        new GameController(game, view).start();

        assertTrue(game.history().isEmpty());
        assertEquals(Color.WHITE, game.sideToMove());
        assertTrue(output.toString().contains("Not a legal move: 'e2e5'"));
        assertTrue(output.toString().contains("20 legal moves:"));
        assertTrue(output.toString().contains("Goodbye."));
    }

    @Test
    void acceptsPromotionWithoutSuffix() {
        Game game = new Game(BoardFactory.fromFen("k7/4P3/8/8/8/8/8/4K3"), Color.WHITE);
        var view = new BorderView(new PrintStream(new ByteArrayOutputStream()),
                new Scanner("e7e8\nquit\n"));
        new GameController(game, view).start();

        assertEquals(PieceType.QUEEN, game.board().pieceAt(Position.parse("e8")).type());
    }
}
