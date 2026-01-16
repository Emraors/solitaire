package com.solitaire.app;

import com.solitaire.domain.Board;
import com.solitaire.domain.Cell;
import com.solitaire.domain.Move;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;

@Log4j2
public final class ApplyMoveCommand implements Command {

    private final GameState gameState;
    private final Move move;

    /**
     * Tracks whether the command is currently in an executed state (i.e., can be undone).
     * This flag is set to true when execute() succeeds and false when undo() is called,
     * allowing the same command instance to be re-executed after being undone (for redo
     * functionality).
     */
    private boolean isCurrentlyExecuted;

    /**
     * Lightweight snapshot of the three cells affected by this move, used for undo.
     * Storing only these three cell states instead of the entire board significantly
     * reduces memory consumption for long games with extensive undo history.
     */
    private Cell cellAtFrom;
    private Cell cellAtOver;
    private Cell cellAtTo;

    public ApplyMoveCommand(GameState gameState, Move move) {
        this.gameState = Objects.requireNonNull(gameState, "game");
        this.move = Objects.requireNonNull(move, "move");
    }

    @Override
    public boolean execute() {
        log.debug("Ready to execute move: {}", move);

        // Prevent double execution - command must be undone before it can be executed again
        if (isCurrentlyExecuted) {
            log.debug("Command is already in executed state, cannot execute again");
            return false;
        }

        if (!gameState.isLegal(move)) {
            log.debug("Move {} is not legal", move);
            return false;
        }

        // Capture minimal state needed for undo (just the three affected cells)
        Board currentBoard = gameState.board();
        cellAtFrom = currentBoard.cellAt(move.from());
        cellAtOver = currentBoard.cellAt(move.over());
        cellAtTo = currentBoard.cellAt(move.to());

        Board after = currentBoard.applyUnchecked(move);
        gameState.setBoard(after);

        isCurrentlyExecuted = true;
        return true;
    }

    @Override
    public void undo() {
        if (!isCurrentlyExecuted) {
            log.debug("Cannot undo: command is not in executed state");
            return;
        }

        log.debug("Undoing move: {}", move);

        // Restore the three affected cells to their previous state
        Board currentBoard = gameState.board();
        Board restored = currentBoard
                .withCell(move.from(), cellAtFrom)
                .withCell(move.over(), cellAtOver)
                .withCell(move.to(), cellAtTo);

        gameState.setBoard(restored);
        isCurrentlyExecuted = false;
    }
}
