package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.model.Piece;
import edu.sfsu.csc413.chess.model.Position;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;

/** An event-driven board. All component access is confined to Swing's EDT. */
public class SwingView implements BoardView {
    private final JButton[][] squares = new JButton[8][8];
    private final JLabel status = new JLabel(" ");
    private final JLabel message = new JLabel(" ");
    private final JButton undo = new JButton("Undo");
    private final JPanel content = new JPanel(new BorderLayout(8, 8));
    private JFrame frame;
    private GameController controller;
    private Game game;
    private Position selected;

    private void buildBoard() {
        JPanel board = new JPanel(new GridLayout(8, 8));
        for (int rank = 7; rank >= 0; rank--) {
            for (int file = 0; file < 8; file++) {
                Position position = new Position(file, rank);
                JButton square = new JButton();
                square.setPreferredSize(new Dimension(76, 76));
                square.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
                square.setOpaque(true);
                square.setBorderPainted(true);
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
        JPanel footer = new JPanel(new BorderLayout());
        footer.add(message, BorderLayout.CENTER);
        footer.add(undo, BorderLayout.EAST);
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.add(status, BorderLayout.NORTH);
        content.add(board, BorderLayout.CENTER);
        content.add(footer, BorderLayout.SOUTH);
    }

    private void click(Position position) {
        if (game == null || game.status().isGameOver()) {
            return;
        }
        Piece piece = game.board().pieceAt(position);
        if (position.equals(selected)) {
            selected = null;
            refresh();
        } else if (piece != null && piece.color() == game.sideToMove()) {
            selected = position;
            message.setText("Selected " + position + "; green squares are legal destinations.");
            refresh();
        } else if (selected != null) {
            String notation = selected.toString() + position;
            selected = null;
            message.setText(" ");
            // BoardView's default promotion choice safely supplies a queen.
            controller.handle(new PlayerAction.Move(notation));
        } else {
            message.setText("Select one of " + game.sideToMove() + "'s pieces first.");
        }
    }

    private void refresh() {
        if (squares[0][0] == null) {
            buildBoard();
        }
        Set<Position> destinations = new HashSet<>();
        if (selected != null) {
            game.legalMoves().stream().filter(move -> move.from().equals(selected))
                    .forEach(move -> destinations.add(move.to()));
        }
        boolean over = game.status().isGameOver();
        for (int file = 0; file < 8; file++) {
            for (int rank = 0; rank < 8; rank++) {
                Position position = new Position(file, rank);
                Piece piece = game.board().pieceAt(position);
                JButton square = squares[file][rank];
                // Explicit side prefixes avoid glyph/font ambiguity on either square color.
                String label = piece == null ? "" :
                        (piece.color() == edu.sfsu.csc413.chess.model.Color.WHITE ? "w" : "b")
                                + piece.type().symbol();
                square.setText(label);
                square.setForeground(Color.BLACK);
                square.setBackground(destinations.contains(position) ? new Color(135, 205, 135)
                        : (file + rank) % 2 == 0 ? new Color(160, 175, 195)
                        : new Color(245, 240, 225));
                square.setBorder(BorderFactory.createLineBorder(
                        position.equals(selected) ?Color.YELLOW : Color.DARK_GRAY,
                        position.equals(selected) ? 4 : 1));
                square.setToolTipText(position + (piece == null ? " (empty)"
                        : " " + piece.color() + " " + piece.type()));
                square.getAccessibleContext().setAccessibleName(square.getToolTipText());
            }
        }
        String result = switch (game.status()) {
            case IN_PROGRESS -> "In progress";
            case CHECK -> "Check";
            case CHECKMATE -> "Checkmate. " + game.sideToMove().opposite() + " wins.";
            case STALEMATE -> "Stalemate. The game is a draw.";
        };
        status.setText(game.sideToMove() + " to move | " + result + " | w = White, b = Black");
        undo.setEnabled(!over && !game.history().isEmpty());
    }

    @Override
    public void render(Game game) {
        onEdt(() -> {
            this.game = game;
            selected = null;
            refresh();
        });
    }

    @Override
    public void showMessage(String text) {
        onEdt(() -> message.setText(text));
    }

    @Override
    public void start(GameController controller) {
        onEdt(() -> {
            this.controller = controller;
            this.game = controller.game();
            refresh();
            frame = new JFrame("Chess");
            frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent event) {
                    controller.handle(new PlayerAction.Quit());
                    frame.dispose();
                }
            });
            frame.setContentPane(content);
            frame.pack();
            frame.setLocationByPlatform(true);
            frame.setVisible(true);
        });
    }

    @Override
    public void close() {
        // The controller calls close on game over too: leave the result visible.
        onEdt(() -> {
            if (frame != null && (game == null || !game.status().isGameOver())) {
                frame.dispose();
            }
        });
    }

    private static void onEdt(Runnable action) {
        if (SwingUtilities.isEventDispatchThread()) {
            action.run();
        } else {
            SwingUtilities.invokeLater(action);
        }
    }
}
