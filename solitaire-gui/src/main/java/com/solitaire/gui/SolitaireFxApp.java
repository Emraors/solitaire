package com.solitaire.gui;

import com.solitaire.app.ApplyMoveCommand;
import com.solitaire.app.CommandManager;
import com.solitaire.app.GameState;
import com.solitaire.app.factory.ApplicationFactory;
import com.solitaire.domain.Cell;
import com.solitaire.domain.GameStatus;
import com.solitaire.domain.Move;
import com.solitaire.domain.Position;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public final class SolitaireFxApp extends Application {

    private GameState gameState;
    private CommandManager commandManager;

    private final Label statusLabel = new Label();
    private final GridPane boardGrid = new GridPane();

    private final Button undoButton = new Button("Undo");
    private final Button redoButton = new Button("Redo");
    private final Button restartButton = new Button("Restart");

    private Position firstClick;

    private FxGameListener listener;

    @Override
    public void start(Stage stage) {
        initFreshGame();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));

        statusLabel.setText("Status: " + gameState.status());

        boardGrid.setHgap(6);
        boardGrid.setVgap(6);
        boardGrid.setAlignment(Pos.CENTER);

        undoButton.setOnAction(
                ignored -> {
                    firstClick = null;
                    commandManager.undo();
                });
        redoButton.setOnAction(
                ignored -> {
                    firstClick = null;
                    commandManager.redo();
                });
        restartButton.setOnAction(
                ignored -> {
                    firstClick = null;
                    initFreshGame();
                    renderBoard();
                });

        HBox top = new HBox(12);
        top.setAlignment(Pos.CENTER_LEFT);
        top.getChildren().addAll(statusLabel, undoButton, redoButton, restartButton);

        root.setTop(top);
        root.setCenter(boardGrid);

        renderBoard();

        Scene scene = new Scene(root);
        stage.setTitle("Solitaire");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    private void initFreshGame() {
        // Detach old listener if we're restarting.
        if (gameState != null && listener != null) {
            gameState.removeListener(listener);
        }

        var appObjects = ApplicationFactory.createEnglishSolitaireGame();

        this.gameState = appObjects.gameState();
        this.commandManager = appObjects.manager();

        this.listener =
                new FxGameListener(
                        ignored -> {
                            renderBoard();
                            updateButtons();
                        },
                        status -> {
                            statusLabel.setText("Status: " + status);
                            updateButtons();
                        });

        gameState.addListener(listener);
        updateButtons();
    }

    private void updateButtons() {
        undoButton.setDisable(commandManager == null || !commandManager.canUndo());
        redoButton.setDisable(commandManager == null || !commandManager.canRedo());
    }

    private void renderBoard() {
        boardGrid.getChildren().clear();

        var board = gameState.board();

        for (int r = 0; r < board.rows(); r++) {
            for (int c = 0; c < board.cols(); c++) {
                Position pos = new Position(r, c);
                var cell = board.cellAt(pos);

                Button b = new Button();
                b.setMinSize(38, 38);
                b.setFocusTraversable(false);

                switch (cell) {
                    case INVALID -> {
                        b.setDisable(true);
                        b.setOpacity(0);
                        b.setText("");
                    }
                    case EMPTY -> {
                        b.setText("·");
                        b.setDisable(false);
                    }
                    case PEG -> {
                        b.setText("●");
                        b.setDisable(false);
                    }
                }

                if (firstClick != null && firstClick.equals(pos)) {
                    b.setStyle("-fx-border-color: #4a90e2; -fx-border-width: 2px;");
                }

                b.setOnAction(ignored -> onCellClicked(pos));

                boardGrid.add(b, c, r);
            }
        }

        updateButtons();
    }

    private void onCellClicked(Position pos) {
        // Avoid confusing interactions when the game is over.
        if (gameState.status() != GameStatus.RUNNING) {
            firstClick = null;
            renderBoard();
            return;
        }

        var cell = gameState.board().cellAt(pos);

        if (firstClick == null) {
            // Only pegs can be moved.
            if (cell != Cell.PEG) {
                return;
            }
            firstClick = pos;
            renderBoard();
            return;
        }

        // Clicking the same cell twice cancels the selection.
        if (firstClick.equals(pos)) {
            firstClick = null;
            renderBoard();
            return;
        }

        Position from = firstClick;
        firstClick = null;

        // Moves in peg solitaire are two steps orthogonally. The jumped peg is the midpoint.
        int dr = pos.r() - from.r();
        int dc = pos.c() - from.c();

        if (!((Math.abs(dr) == 2 && dc == 0) || (Math.abs(dc) == 2 && dr == 0))) {
            renderBoard();
            return;
        }

        Position over = new Position(from.r() + dr / 2, from.c() + dc / 2);

        Move move = new Move(from, over, pos);
        if (!gameState.isLegal(move)) {
            renderBoard();
            return;
        }

        commandManager.execute(new ApplyMoveCommand(gameState, move));
    }
}
