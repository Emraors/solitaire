package com.solitaire.cli;

import com.solitaire.app.ApplyMoveCommand;
import com.solitaire.app.CommandManager;
import com.solitaire.app.GameListener;
import com.solitaire.app.GameState;
import com.solitaire.domain.Board;
import com.solitaire.domain.GameStatus;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public final class CliController implements GameListener {

    private final GameState gameState;
    private final CommandManager commands;

    private final AsciiRenderer renderer = new AsciiRenderer();
    private final MoveParser parser = new MoveParser();

    private boolean needsPrompt = true;

    public CliController(GameState gameState, CommandManager commands) {
        this.gameState = gameState;
        this.commands = commands;

        this.gameState.addListener(this);
    }

    public void run() throws IOException {
        onBoardChanged(gameState.board());
        onStatusChanged(gameState.status());

        var in = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            if (needsPrompt) {
                printPrompt();
                needsPrompt = false;
            }

            var line = in.readLine();
            if (line == null) return;

            line = line.trim();

            if (line.equalsIgnoreCase("q")) return;

            if (line.equalsIgnoreCase("u")) {
                boolean ok = commands.undo();
                if (!ok) {
                    System.out.println("Nothing to undo.");
                    needsPrompt = true;
                }
                continue;
            }

            if (line.equalsIgnoreCase("r")) {
                boolean ok = commands.redo();
                if (!ok) {
                    System.out.println("Nothing to redo.");
                    needsPrompt = true;
                }
                continue;
            }

            var moveOpt = parser.parse(line);
            if (moveOpt.isEmpty()) {
                System.out.println("Invalid input.");
                needsPrompt = true;
                continue;
            }

            boolean ok = commands.execute(new ApplyMoveCommand(gameState, moveOpt.get()));
            if (!ok) {
                System.out.println("Illegal move.");
                needsPrompt = true;
            }
        }
    }

    private void printPrompt() {
        System.out.println("Enter move: fromR fromC toR toC | 'u' undo | 'r' redo | 'q' quit");
    }

    @Override
    public void onBoardChanged(Board newBoard) {
        System.out.println(renderer.render(newBoard));
        needsPrompt = true;
    }

    @Override
    public void onStatusChanged(GameStatus newStatus) {
        System.out.println("Status: " + newStatus);
        if (newStatus != GameStatus.RUNNING) {
            System.out.println("Game ended.");
        }
        needsPrompt = true;
    }
}
