package com.solitaire.domain.rules;

import com.solitaire.domain.Board;
import com.solitaire.domain.GameStatus;
import com.solitaire.domain.Move;
import java.util.List;

public final class StubRules implements Rules {

    @Override
    public boolean isLegal(Board board, Move move) {
        return true; // TODO
    }

    @Override
    public List<Move> legalMoves(Board board) {
        return List.of(); // TODO
    }

    @Override
    public GameStatus status(Board board) {
        return GameStatus.RUNNING; // TODO
    }
}
