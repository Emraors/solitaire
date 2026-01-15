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
import lombok.extern.log4j.Log4j2;

@Log4j2
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
        log.debug("CliController initialized and registered as listener");
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

            if (line.equalsIgnoreCase("q")) {
                log.debug("User quit command");
                return;
            }

            if (line.equalsIgnoreCase("u")) {
                log.debug("Undo command requested");
                boolean ok = commands.undo();
                if (!ok) {
                    System.out.println("Nothing to undo.");
                    log.debug("Undo failed - nothing to undo");
                    needsPrompt = true;
                }
                continue;
            }

            if (line.equalsIgnoreCase("r")) {
                log.debug("Redo command requested");
                boolean ok = commands.redo();
                if (!ok) {
                    System.out.println("Nothing to redo.");
                    log.debug("Redo failed - nothing to redo");
                    needsPrompt = true;
                }
                continue;
            }

            var moveOpt = parser.parse(line);
            if (moveOpt.isEmpty()) {
                System.out.println("Invalid input.");
                log.debug("Failed to parse move from input: '{}'", line);
                needsPrompt = true;
                continue;
            }

            log.debug("Parsed move: {}", moveOpt.get());
            boolean ok = commands.execute(new ApplyMoveCommand(gameState, moveOpt.get()));
            if (!ok) {
                System.out.println("Illegal move.");
                log.debug("Move execution failed: {}", moveOpt.get());
                needsPrompt = true;
            }
        }
    }

    private void printPrompt() {
        System.out.println("Enter move: fromR fromC toR toC | 'u' undo | 'r' redo | 'q' quit");
    }

    @Override
    public void onBoardChanged(Board newBoard) {
        log.debug("Board changed notification received. Pegs: {}", newBoard.pegCount());
        System.out.println(renderer.render(newBoard));
        needsPrompt = true;
    }

    @Override
    public void onStatusChanged(GameStatus newStatus) {
        log.debug("Status changed to: {}", newStatus);
        System.out.println("Status: " + newStatus);
        if (newStatus != GameStatus.RUNNING) {
            System.out.println("Game ended.");
            log.debug("Game ended with status: {}", newStatus);
        }
        needsPrompt = true;
    }
}
