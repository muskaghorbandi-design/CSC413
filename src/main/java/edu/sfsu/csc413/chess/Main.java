package edu.sfsu.csc413.chess;

import edu.sfsu.csc413.chess.factory.PieceFactory;
import edu.sfsu.csc413.chess.model.Board;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.PieceType;
import edu.sfsu.csc413.chess.model.Position;
import edu.sfsu.csc413.chess.view.PieceGlyphs;
import edu.sfsu.csc413.chess.view.TextBoardRenderer;

public final class Main {

    public static void main(String[] args) {
        Board board = new Board();

        PieceType[] backRank = {
                PieceType.ROOK,
                PieceType.KNIGHT,
                PieceType.BISHOP,
                PieceType.QUEEN,
                PieceType.KING,
                PieceType.BISHOP,
                PieceType.KNIGHT,
                PieceType.ROOK
        };

        for (int file = 0; file < 8; file++) {

            // White pieces
            board.place(
                    new Position(file, 0),
                    PieceFactory.create(backRank[file], Color.WHITE)
            );

            board.place(
                    new Position(file, 1),
                    PieceFactory.create(PieceType.PAWN, Color.WHITE)
            );

            // Black pieces
            board.place(
                    new Position(file, 6),
                    PieceFactory.create(PieceType.PAWN, Color.BLACK)
            );

            board.place(
                    new Position(file, 7),
                    PieceFactory.create(backRank[file], Color.BLACK)
            );
        }

        System.out.println(
                new TextBoardRenderer(PieceGlyphs.LETTERS).render(board)
        );
    }

    private Main() {
    }
}