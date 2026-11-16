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

/** Click a friendly piece, then a destination. All game changes go through the controller. */
public class SwingView implements BoardView {
    private final JButton[][] squares = new JButton[8][8];
    private final JLabel status = new JLabel(" ");
    private final JLabel message = new JLabel(" ");
    private final JButton undo = new JButton("Undo");
    private GameController controller;
    private Game game;
    private JFrame frame;
    private Position selected;

    @Override
    public void start(GameController controller) {
        onEventThread(() -> {
            this.controller = controller;
            this.game = controller.game();
            frame = new JFrame("Chess");
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent event) {
                    controller.handle(new PlayerAction.Quit());
                    frame.dispose();
                }
            });
            JPanel board = new JPanel(new GridLayout(8, 8));
            for (int rank = 7; rank >= 0; rank--) {
                for (int file = 0; file < 8; file++) {
                    Position position = new Position(file, rank);
                    JButton square = new JButton();
                    square.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
                    square.setPreferredSize(new Dimension(72, 72));
                    square.setOpaque(true);
                    square.setContentAreaFilled(true);
                    square.addActionListener(event -> click(position));
                    squares[file][rank] = square;
                    board.add(square);
                }
            }
            undo.addActionListener(event -> {
                selected = null;
                message.setText(" ");
                controller.handle(new PlayerAction.Undo());
            });
            JPanel information = new JPanel(new GridLayout(0, 1));
            information.add(status);
            information.add(message);
            information.add(new JLabel("w = White, b = Black; P pawn, N knight, B bishop, R rook, Q queen, K king"));
            information.add(new JLabel("Click your piece, then its destination. Promotions become queens."));
            information.add(undo);
            frame.add(board, BorderLayout.CENTER);
            frame.add(information, BorderLayout.SOUTH);
            refresh();
            frame.pack();
            frame.setLocationByPlatform(true);
            frame.setVisible(true);
        });
    }

    private void click(Position position) {
        if (game.status().isGameOver()) {
            return;
        }
        Piece piece = game.board().pieceAt(position);
        if (piece != null && piece.color() == game.sideToMove()) {
            selected = position;
            message.setText("Selected " + position + ". Click a destination.");
            refresh();
        } else if (selected != null) {
            String notation = selected.toString() + position;
            selected = null;
            message.setText(" ");
            // The controller supplies the default queen when promotion is needed.
            controller.handle(new PlayerAction.Move(notation));
        } else {
            message.setText("Select one of your pieces first.");
        }
    }

    @Override
    public void render(Game game) {
        onEventThread(() -> {
            this.game = game;
            selected = null;
            if (frame != null) {
                refresh();
            }
        });
    }

    private void refresh() {
        boolean finished = game.status().isGameOver();
        String description = switch (game.status()) {
            case IN_PROGRESS -> game.sideToMove() + " to move";
            case CHECK -> game.sideToMove() + " to move — Check!";
            case CHECKMATE -> "Checkmate. " + game.sideToMove().opposite() + " wins.";
            case STALEMATE -> "Stalemate. The game is a draw.";
        };
        status.setText(description);
        undo.setEnabled(!finished);
        for (int file = 0; file < 8; file++) {
            for (int rank = 0; rank < 8; rank++) {
                Position position = new Position(file, rank);
                Piece piece = game.board().pieceAt(position);
                JButton square = squares[file][rank];
                boolean light = (file + rank) % 2 != 0;
                square.setBackground(light ? new Color(245, 232, 205) : new Color(65, 85, 110));
                square.setForeground(light ? Color.BLACK : Color.WHITE);
                String label = piece == null ? "" :
                        (piece.color() == edu.sfsu.csc413.chess.model.Color.WHITE ? "w" : "b")
                                + piece.type().symbol();
                square.setText(label);
                square.setToolTipText(position + (piece == null ? " (empty)" : " " + piece.color() + " " + piece.type()));
                square.getAccessibleContext().setAccessibleName(square.getToolTipText());
                square.setBorder(BorderFactory.createLineBorder(
                        position.equals(selected) ? Color.YELLOW : Color.GRAY,
                        position.equals(selected) ? 5 : 1));
                square.setEnabled(!finished);
            }
        }
    }

    @Override
    public void showMessage(String text) {
        onEventThread(() -> message.setText(text));
    }

    @Override
    public void close() {
        onEventThread(() -> {
            if (frame != null) {
                // Leave the result visible after the controller ends the game.
                if (game.status().isGameOver()) {
                    refresh();
                } else {
                    frame.dispose();
                }
            }
        });
    }

    private static void onEventThread(Runnable task) {
        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }
}
