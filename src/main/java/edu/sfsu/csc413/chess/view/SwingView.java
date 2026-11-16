package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.model.Piece;
import edu.sfsu.csc413.chess.model.Position;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/** Event-driven chess board. All UI work and player actions run on the EDT. */
public class SwingView implements BoardView {
    private final JButton[][] squares = new JButton[8][8];
    private Game game;
    private GameController controller;
    private JFrame frame;
    private JLabel status;
    private JLabel message;
    private JButton undo;
    private Position selected;
    private String latestMessage = "Click a piece, then its destination. Promotion uses a queen.";

    @Override
    public void start(GameController controller) {
        onEdt(() -> {
            this.controller = controller;
            game = controller.game();
            frame = new JFrame("Chess — White at bottom");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent event) {
                    controller.handle(new PlayerAction.Quit());
                }
            });
            status = new JLabel();
            message = new JLabel(latestMessage);
            JPanel board = new JPanel(new GridLayout(8, 8));
            for (int rank = 7; rank >= 0; rank--) {
                for (int file = 0; file < 8; file++) {
                    Position position = new Position(file, rank);
                    JButton square = new JButton();
                    square.setPreferredSize(new Dimension(72, 72));
                    square.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
                    square.setOpaque(true);
                    square.setContentAreaFilled(true);
                    square.setForeground(Color.BLACK);
                    square.addActionListener(event -> click(position));
                    squares[file][rank] = square;
                    board.add(square);
                }
            }
            undo = new JButton("Undo");
            undo.addActionListener(event -> {
                selected = null;
                showMessage(" ");
                controller.handle(new PlayerAction.Undo());
            });
            JPanel footer = new JPanel(new BorderLayout(8, 8));
            footer.add(message, BorderLayout.CENTER);
            footer.add(undo, BorderLayout.EAST);
            frame.add(status, BorderLayout.NORTH);
            frame.add(board, BorderLayout.CENTER);
            frame.add(footer, BorderLayout.SOUTH);
            draw();
            frame.pack();
            frame.setLocationByPlatform(true);
            frame.setVisible(true);
        });
    }

    private void click(Position position) {
        if (game.status().isGameOver()) {
            return;
        }
        if (selected == null) {
            Piece piece = game.board().pieceAt(position);
            if (piece == null || piece.color() != game.sideToMove()) {
                showMessage("Select a " + game.sideToMove() + " piece first.");
                return;
            }
            selected = position;
            showMessage("Selected " + position + ". Click its destination (or again to cancel).");
            draw();
        } else if (selected.equals(position)) {
            selected = null;
            showMessage("Selection cleared.");
            draw();
        } else {
            String notation = selected.toString() + position;
            selected = null;
            showMessage(" ");
            // The controller validates the move and supplies the default queen promotion.
            controller.handle(new PlayerAction.Move(notation));
        }
    }

    @Override
    public void render(Game game) {
        onEdt(() -> {
            this.game = game;
            selected = null;
            if (frame != null) {
                draw();
            }
        });
    }

    private void draw() {
        var state = game.status();
        String detail = switch (state) {
            case IN_PROGRESS -> "Click a piece, then its destination";
            case CHECK -> "Check";
            case CHECKMATE -> "Checkmate — " + game.sideToMove().opposite() + " wins";
            case STALEMATE -> "Stalemate — draw";
        };
        status.setText((state.isGameOver() ? "" : game.sideToMove() + " to move | ") + detail
                + " | w = White, b = Black; N = Knight");
        undo.setEnabled(!state.isGameOver() && !game.history().isEmpty());
        for (int file = 0; file < 8; file++) {
            for (int rank = 0; rank < 8; rank++) {
                Position position = new Position(file, rank);
                Piece piece = game.board().pieceAt(position);
                JButton square = squares[file][rank];
                // Explicit color prefixes and black text remain readable on both backgrounds,
                // without relying on platform-specific chess glyph fonts.
                square.setText(piece == null ? "" :
                        (piece.color() == edu.sfsu.csc413.chess.model.Color.WHITE ? "w" : "b")
                                + piece.type().symbol());
                square.setBackground((file + rank) % 2 == 0
                        ? new Color(160, 180, 195) : new Color(245, 235, 210));
                square.setBorder(BorderFactory.createLineBorder(
                        position.equals(selected) ? new Color(180, 65, 0) : Color.GRAY,
                        position.equals(selected) ? 4 : 1));
                square.setToolTipText(position + (piece == null ? " (empty)"
                        : " " + piece.color() + " " + piece.type()));
                square.getAccessibleContext().setAccessibleName(square.getToolTipText());
                square.setEnabled(!state.isGameOver());
            }
        }
    }

    @Override
    public void showMessage(String text) {
        onEdt(() -> {
            latestMessage = text;
            if (message != null) {
                message.setText(text);
            }
        });
    }

    // Keep the final board and result visible when the controller ends the game.
    // The window's close button disposes it, including after the controller stops.

    private static void onEdt(Runnable action) {
        if (SwingUtilities.isEventDispatchThread()) {
            action.run();
        } else {
            SwingUtilities.invokeLater(action);
        }
    }
}
