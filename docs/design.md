
# Milestone 5 - Chess Engine Design

## Class Diagram

This diagram shows the main classes in my chess engine. Game manages the board, turns, and move history. MoveGenerator checks which moves are legal. Each chess piece extends the Piece class.

```mermaid
classDiagram
    class Game {
        -Board board
        -List~Move~ history
        -Color sideToMove
        +Game()
        +Game(Board, Color)
        +board() Board
        +sideToMove() Color
        +history() List~Move~
        +legalMoves() List~Move~
        +findLegalMove(String) Optional~Move~
        +play(Move) void
        +undoLastMove() Optional~Move~
    }

    class MoveGenerator {
        -MoveGenerator()
        +legalMoves(Board, Color)$ List~Move~
        +isAttacked(Board, Position, Color)$ boolean
        +isInCheck(Board, Color)$ boolean
        ~pseudoLegalMoves(Board, Color)$ List~Move~
    }

    class Board {
        -Piece[][] squares
        +Board()
        +pieceAt(Position) Piece
        +isEmpty(Position) boolean
        +place(Position, Piece) void
        +positionsOf(Color) List~Position~
        +kingPosition(Color) Position
        +apply(Move) void
        +undo(Move) void
        +toString() String
    }

    class Piece {
        <<abstract>>
        -Color color
        -PieceType type
        #Piece(Color, PieceType)
        +color() Color
        +type() PieceType
        +symbol() char
        +pseudoLegalMoves(Board, Position)* List~Move~
        +attacks(Board, Position, Position) boolean
        +toString() String
    }

    class King {
        +King(Color)
        +pseudoLegalMoves(Board, Position) List~Move~
    }
    class Queen {
        +Queen(Color)
        +pseudoLegalMoves(Board, Position) List~Move~
    }
    class Rook {
        +Rook(Color)
        +pseudoLegalMoves(Board, Position) List~Move~
    }
    class Bishop {
        +Bishop(Color)
        +pseudoLegalMoves(Board, Position) List~Move~
    }
    class Knight {
        +Knight(Color)
        +pseudoLegalMoves(Board, Position) List~Move~
    }
    class Pawn {
        +Pawn(Color)
        +pseudoLegalMoves(Board, Position) List~Move~
        +attacks(Board, Position, Position) boolean
    }

    class Move {
        <<record>>
        +Position from
        +Position to
        +Piece moved
        +Piece captured
        +PieceType promotesTo
        +quiet(Position, Position, Piece)$ Move
        +capture(Position, Position, Piece, Piece)$ Move
        +promotion(Position, Position, Piece, Piece, PieceType)$ Move
        +isCapture() boolean
        +isPromotion() boolean
        +toString() String
    }

    class Position {
        <<record>>
        +int file
        +int rank
        +BOARD_SIZE$ int
        +isOnBoard(int, int)$ boolean
        +parse(String)$ Position
        +offsetOrNull(int, int) Position
        +toString() String
    }

    class Color {
        <<enumeration>>
        WHITE
        BLACK
        +opposite() Color
        +pawnDirection() int
        +pawnStartRank() int
        +promotionRank() int
    }

    class PieceType {
        <<enumeration>>
        PAWN
        KNIGHT
        BISHOP
        ROOK
        QUEEN
        KING
        +symbol() char
        +fromSymbol(char)$ PieceType
    }

    Piece <|-- King
    Piece <|-- Queen
    Piece <|-- Rook
    Piece <|-- Bishop
    Piece <|-- Knight
    Piece <|-- Pawn

    Game *-- Board
    Game --> Color
    Game --> MoveGenerator
    Game o-- Move
    Board o-- Piece
    Board --> Position
    MoveGenerator --> Board
    MoveGenerator --> Piece
    Move --> Position
    Move --> Piece
    Move --> PieceType
    Piece --> Color
    Piece --> PieceType
```

## Sequence Diagram

This diagram shows what happens when the game asks for legal moves. Each possible move is temporarily applied to the board. The engine checks whether the king is attacked, then undoes the move. Only moves that leave the king safe are returned.

```mermaid
sequenceDiagram
    actor Player
    participant Game
    participant MoveGenerator
    participant Board
    participant Piece

    Player->>Game: legalMoves()
    Game->>MoveGenerator: legalMoves(board, sideToMove)
    MoveGenerator->>MoveGenerator: pseudoLegalMoves(board, color)

    loop For each piece of this color
        MoveGenerator->>Board: positionsOf(color)
        MoveGenerator->>Board: pieceAt(from)
        Board-->>MoveGenerator: piece
        MoveGenerator->>Piece: pseudoLegalMoves(board, from)
        Piece-->>MoveGenerator: possible moves
    end

    loop For each possible move
        MoveGenerator->>Board: apply(move)
        MoveGenerator->>MoveGenerator: isInCheck(board, color)
        MoveGenerator->>Board: kingPosition(color)
        Board-->>MoveGenerator: king position
        opt King exists
            MoveGenerator->>MoveGenerator: isAttacked(board, king, opposite color)
            loop For each opponent piece
                MoveGenerator->>Board: positionsOf(opposite color)
                MoveGenerator->>Board: pieceAt(from)
                Board-->>MoveGenerator: opponent piece
                MoveGenerator->>Piece: attacks(board, from, king)
                Piece-->>MoveGenerator: true or false
            end
        end
        MoveGenerator->>Board: undo(move)

        alt King is safe
            MoveGenerator->>MoveGenerator: Add move to legal moves
        else King is in check
            MoveGenerator->>MoveGenerator: Skip this move
        end
    end

    MoveGenerator-->>Game: legal moves
    Game-->>Player: legal moves
```

## Design Notes

- `Game` manages the board, current turn, and move history.
- `MoveGenerator` is responsible for checking which moves are legal.
- `Board` stores the pieces and can apply or undo moves.
- Every chess piece extends `Piece` and defines its own movement.
- `Pawn` has its own `attacks()` method because pawns attack diagonally but move forward.
- Checking a move does not permanently change the board because `undo()` restores it afterward.

