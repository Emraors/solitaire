package com.solitaire.app;

import com.solitaire.domain.Board;
import com.solitaire.domain.Move;
import java.util.Objects;

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
        if (executed) return false;

        if (!gameState.isLegal(move)) {
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
        if (!executed) return;
        gameState.setBoard(before);
        executed = false;
    }
}
