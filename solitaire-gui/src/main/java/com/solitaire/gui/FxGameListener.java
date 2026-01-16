package com.solitaire.gui;

import com.solitaire.app.GameListener;
import com.solitaire.domain.Board;
import com.solitaire.domain.GameStatus;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.application.Platform;

/**
 * Small adapter that ensures GameState events update the JavaFX UI on the FX Application Thread.
 */
final class FxGameListener implements GameListener {

    private final Consumer<Board> onBoardChanged;
    private final Consumer<GameStatus> onStatusChanged;

    FxGameListener(Consumer<Board> onBoardChanged, Consumer<GameStatus> onStatusChanged) {
        this.onBoardChanged = Objects.requireNonNull(onBoardChanged, "onBoardChanged");
        this.onStatusChanged = Objects.requireNonNull(onStatusChanged, "onStatusChanged");
    }

    @Override
    public void onBoardChanged(Board board) {
        Platform.runLater(() -> onBoardChanged.accept(board));
    }

    @Override
    public void onStatusChanged(GameStatus status) {
        Platform.runLater(() -> onStatusChanged.accept(status));
    }
}

