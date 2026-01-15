package com.solitaire.app;

import com.solitaire.domain.Board;
import com.solitaire.domain.GameStatus;
import com.solitaire.domain.Move;
import com.solitaire.domain.rules.Rules;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;

@Log4j2
public final class GameState {

    private final Rules rules;

    private Board board;
    private GameStatus status;

    private final List<GameListener> listeners = new ArrayList<>();

    public GameState(Board initialBoard, Rules rules) {
        this.board = Objects.requireNonNull(initialBoard, "initialBoard");
        this.rules = Objects.requireNonNull(rules, "rules");
        this.status = rules.status(board);
    }

    public Board board() {
        return board;
    }

    public GameStatus status() {
        return status;
    }

    public Rules rules() {
        return rules;
    }

    public void addListener(GameListener listener) {
        listeners.add(Objects.requireNonNull(listener));
    }

    public void removeListener(GameListener listener) {
        listeners.remove(listener);
    }

    /** Mutates the game state. Only intended to be called by Commands. */
    void setBoard(Board newBoard) {
        this.board = Objects.requireNonNull(newBoard, "newBoard");

        GameStatus old = this.status;
        this.status = rules.status(board);

        notifyBoardChanged();
        if (old != status) {
            notifyStatusChanged();
        }
    }

    public boolean isLegal(Move move) {
        return rules.isLegal(board, move);
    }

    private void notifyBoardChanged() {
        log.debug("Notifying {} listeners of board change", listeners.size());
        for (GameListener l : listeners) {
            l.onBoardChanged(board);
        }
    }

    private void notifyStatusChanged() {
        log.debug("Notifying {} listeners of status change to {}", listeners.size(), status);
        for (GameListener l : listeners) {
            l.onStatusChanged(status);
        }
    }
}
