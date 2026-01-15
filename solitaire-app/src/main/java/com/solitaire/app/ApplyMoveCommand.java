package com.solitaire.app;

import com.solitaire.domain.Board;
import com.solitaire.domain.Move;
import java.util.Objects;

public final class ApplyMoveCommand implements Command {

    private final Game game;
    private final Move move;

    private Board before;
    private boolean executed;

    public ApplyMoveCommand(Game game, Move move) {
        this.game = Objects.requireNonNull(game, "game");
        this.move = Objects.requireNonNull(move, "move");
    }

    @Override
    public boolean execute() {
        if (executed) return false;

        if (!game.isLegal(move)) {
            return false;
        }

        before = game.board();
        Board after = before.applyUnchecked(move);

        game.setBoard(after);
        executed = true;
        return true;
    }

    @Override
    public void undo() {
        if (!executed) return;
        game.setBoard(before);
        executed = false;
    }
}
