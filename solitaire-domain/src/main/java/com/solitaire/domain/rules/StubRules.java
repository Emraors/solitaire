package com.solitaire.domain.rules;

import com.solitaire.domain.Board;
import com.solitaire.domain.GameStatus;
import com.solitaire.domain.Move;
import java.util.List;
import lombok.extern.log4j.Log4j2;

@Log4j2
public final class StubRules implements Rules {

    @Override
    public boolean isLegal(Board board, Move move) {
        log.debug("StubRules.isLegal called for move: {} (always returns true)", move);
        return true; // TODO
    }

    @Override
    public List<Move> legalMoves(Board board) {
        log.debug("StubRules.legalMoves called (returns empty list)");
        return List.of(); // TODO
    }

    @Override
    public GameStatus status(Board board) {
        log.debug("StubRules.status called (always returns RUNNING)");
        return GameStatus.RUNNING; // TODO
    }
}
