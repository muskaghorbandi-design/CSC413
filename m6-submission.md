
# M6 Submission — Chess Engine

## 1. Who decides, and who shows?

MoveGenerator.status() checks the board and decides whether the game is in check, checkmate, stalemate, or still in progress. GameController handles the game actions and tells BoardView to display the result to the player.

## 2. Dependency Rule

The controller can communicate with the engine and the view. However, the engine and model should not depend on the view package. This keeps the chess rules separate from how the game is displayed.

## 3. Adding a Swing View in M7

For M7, I would create a new Swing view that implements BoardView. I would also update Main.java to select the new view. The chess engine and GameController should not need changes because the new view uses the same interface.
