# M7 Submission — Views, Generated and Compared

## 1. Working Arrangement and AI Tool

I worked alone on M7.

I used the Codex CLI with my ChatGPT account to generate three different versions of the chess views. I used a fresh Codex session and a separate Git branch for each run. All three runs started from the same `main` baseline.

I used `AGENTS.md` to give Codex instructions about which files it could change. I also reviewed the generated code, tested the chessboard myself, used the Java debugger, and made a manual code change without Codex.

## 2. AGENTS.md and Guardrails

Before running Codex, I created `AGENTS.md` and committed it to the repository.

The instructions protected the existing chess engine, model, factory, controller, view interfaces, Maven configuration, and existing tests.

Codex was allowed to add new view implementations and view tests and update `Main.java`.

I wanted to keep the existing chess rules working while adding new ways to play the game. The views were required to send actions through `GameController.handle()` instead of changing the game directly.

## 3. Three Independent Codex Runs

### Run 1 — Basic Swing and Border Views

Branch: `m7-run-1`  
Commit: `f54859d`

I asked Codex to create a Swing chessboard and a bordered terminal chessboard. The Swing view needed clickable pieces, move handling, game-status messages, Undo, and safe pawn promotion. I also asked Codex to update `Main.java` so I could choose the views using `swing` and `border`.

Codex created `SwingView.java`, `BorderView.java`, and `BorderViewTest.java`, and updated `Main.java`.

The run passed 86 tests. I manually tested the Swing chessboard and completed a game ending in checkmate. I also tested the Border view with the moves `f2f3`, `e7e5`, `g2g4`, and `d8h4`.

I noticed that the Swing view initially displayed the side to move even after checkmate. Codex corrected that status message during Run 1.

### Run 2 — Legal Destination Highlighting

Branch: `m7-run-2`  
Commit: `e5549d8`

I used the same basic instructions as Run 1 but added one requirement: highlight legal destination squares when a player selects a piece.

Codex created `SwingView.java` and `BorderView.java` and updated `Main.java`.

This run passed 83 tests. I opened the Swing chessboard and clicked the white pawn on e2. The board highlighted e3 and e4 as legal destinations.

This feature made the game easier to understand because players could see where a selected piece was allowed to move.

### Run 3 — Selected-Piece Highlighting

Branch: `m7-run-3`  
Commit: `87c3556`

I kept the basic instructions but changed the extra requirement. Instead of highlighting legal destination squares, I asked Codex to highlight the selected piece.

Codex created `SwingView.java` and `BorderView.java` and updated `Main.java`.

This run passed 83 tests. I opened the Swing chessboard and clicked the white pawn on e2. The selected square was highlighted.

This helped players see which piece they had chosen.

## 4. Comparing the Three Runs

| Feature | Run 1 | Run 2 | Run 3 |
|---|---|---|---|
| Swing chessboard | Yes | Yes | Yes |
| Border terminal view | Yes | Yes | Yes |
| Selected-piece feedback | Basic | Border | Highlight |
| Legal destination highlighting | No | Yes | No |
| Undo | Yes | Yes | Yes |
| Safe queen promotion | Yes | Yes | Yes |
| Automated tests passed | 86 | 83 | 83 |
| Additional view tests | Yes | No | No |

All three runs used the existing controller and game rules. Run 1 had the advantage of adding automated Border view tests. Run 3 made the selected piece more noticeable. Run 2 was my favorite because showing legal moves made the chessboard easier to use.

I selected **Run 2** as the final version and merged it into `main`.

## 5. Idea Adopted From Another Run

I liked the selected-piece highlighting from Run 3, even though I chose Run 2.

Run 2 already used a border to show the selected piece, so I improved the border color to bright yellow. This combined Run 2's legal destination highlighting with the more noticeable selection feedback I liked in Run 3.

## 6. My Hand-Written Change

I edited `SwingView.java` myself using TextEdit, without Codex.

At line 107, I changed the selected-piece border color from `new Color(190, 95, 0)` to `Color.YELLOW`.

This made the selected piece easier to see while keeping the legal destination squares green.

My separate commit was:

`bf4d0ed` — `M7 by hand: change selected-piece highlight to yellow`

My first edit accidentally used `new Color.YELLOW`, which caused a compilation error. I checked the error, corrected the line to `Color.YELLOW`, and compiled the project successfully.

## 7. Code Review

I reviewed the generated Swing code and checked how it communicates with the existing game.

**Review 1 — `SwingView.java`, lines 59–76**

The `click()` method checks the clicked square and stores the selected piece. When a destination is clicked, it creates a move action and sends it through `controller.handle(new PlayerAction.Move(notation))`.

I checked that the view does not call `game.play()` directly.

**Review 2 — `SwingView.java`, lines 86–89**

The view calls `game.legalMoves()` and filters the moves using the selected starting square.

I checked that legal destination highlighting comes from the game engine rather than separate chess rules inside Swing.

**Review 3 — `SwingView.java`, lines 103–108**

The view colors legal destination squares green and draws a border around the selected square.

I reviewed this code before making my manual yellow-border change.

**Review 4 — `SwingView.java`, lines 139–157**

The `start()` method creates and displays the Swing window. It does not call `controller.runLoop()`, which would block the graphical interface.

I also checked the Event Dispatch Thread handling in lines 170–175.

## 8. Debugger Evidence

I used the Java debugger (`jdb`), version 25.0.

I started the Swing application using:

`jdb -classpath target/classes edu.sfsu.csc413.chess.Main swing`

I set a breakpoint with:

`stop at edu.sfsu.csc413.chess.view.SwingView:63`

After starting the application, I clicked the white pawn on e2.

The debugger stopped at:

`SwingView.click(), line=63`

The thread was:

`AWT-EventQueue-0`

I then entered:

`print position`

The debugger returned:

`position = "e2"`

This confirmed that clicking the e2 square passed the correct position into the Swing click handler.

## 9. Testing and Results

I ran:

`./mvnw -q compile`

After correcting my manual edit, compilation completed without errors.

I also ran:

`./mvnw test`

Final results:

- Tests run: 83
- Failures: 0
- Errors: 0
- Skipped: 0
- BUILD SUCCESS

I manually opened the Swing chessboard and tested piece selection, legal-move highlighting, and moving pieces. During the independent runs, I also tested Undo and a complete checkmate sequence.

The Border view was tested during the independent runs, including a game ending in checkmate.

## 10. Final Changes and Reflection

The final version adds `SwingView.java` and `BorderView.java` and updates `Main.java`. I also added `AGENTS.md` for the Codex instructions.

The existing chess engine and protected files were not changed.

I learned that AI-generated code still needs careful review and testing. The three runs produced different user-interface features even though the main task was the same. Comparing them helped me choose the version that was easiest to use.

I also learned how to find and fix a Java compilation error and how to use a debugger to inspect a click event. My final version keeps the existing chess rules while adding graphical and terminal ways to play.