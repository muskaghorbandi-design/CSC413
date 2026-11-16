# Instructions for coding agents

This is a Java chess engine built with MVC. The model and controller were written by hand and are finished. Your job in M7 is to generate views only.

## Do

- Add new views in `src/main/java/edu/sfsu/csc413/chess/view/`.
- Implement `BoardView`. Read `BoardView`, `ConsoleView`, `EmojiView`, `PlayerAction`, and `GameController` before writing code.
- Read the game through `Game`: `board()`, `sideToMove()`, `legalMoves()`, `status()`, and `history()`.
- Send player actions through `controller.handle(...)`, using `PlayerAction`.
- Add command-line view selection in `Main.java`. This project uses an if/else statement, not a `chooseView` method.
- Create a Swing view selected by `swing`.
- Create a second playable view selected by `border`.
- Use only the JDK.

## Don't

- Don't change anything in `model/`, `engine/`, or `factory/`.
- Don't change `BoardView`, `PlayerAction`, `GameController`, `ConsoleView`, or `EmojiView`.
- Don't change `pom.xml` or existing tests.
- Don't call `game.play()` or `game.undoLastMove()` from a view. Only the controller changes the game.
- Don't calculate chess rules in a view. Legal moves come from `game.legalMoves()`; check and checkmate come from `game.status()`.
- Don't wait for the next move inside a Swing window. Its `start` method builds the window and returns; it never calls `controller.runLoop()`. A modal dialog that asks one question, such as choosing a promotion piece, is fine.
- Don't introduce new dependencies.

## Check your work

- Run `./mvnw test`. All 83 existing tests must pass.
- Do not modify or delete existing tests.
- Run `git diff --stat` and inspect all changed files.
- Changes must be limited to new view files and `Main.java`, plus any permitted new view tests.
- Check that the Swing view supports clicking moves, Undo, status messages, and promotion.
- Check that the second view can play a complete game.

## Stop and ask

If you think a protected file must change, stop and explain why instead of changing it.
