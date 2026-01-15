package com.solitaire.app;

import com.solitaire.domain.Board;
import com.solitaire.domain.Move;
import lombok.extern.log4j.Log4j2;

import java.util.Objects;

@Log4j2
public final class ApplyMoveCommand implements Command {

    private final GameState gameState;
    private final Move move;

    private Board before;
    private boolean executed;

    public ApplyMoveCommand(GameState gameState, Move move) {
        this.gameState = Objects.requireNonNull(gameState, "game");
        this.move = Objects.requireNonNull(move, "move");
    }

    @Override
    public boolean execute() {
        log.debug("Ready to execute move: {}", move);
        if (executed) return false;

        if (!gameState.isLegal(move)) {
            log.debug("Move {} is not legal", move);
            return false;
        }

        before = gameState.board();
        Board after = before.applyUnchecked(move);

        gameState.setBoard(after);
        executed = true;
        return true;
    }

    @Override
    public void undo() {
        if (!executed) {
            log.debug("Cannot undo: command was not executed");
            return;
        }
        log.debug("Undoing move: {}", move);
        gameState.setBoard(before);
        executed = false;
    }
}
