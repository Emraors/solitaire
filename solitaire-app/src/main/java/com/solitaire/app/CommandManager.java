package com.solitaire.app;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;

@Log4j2
public final class CommandManager {

    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final Deque<Command> redoStack = new ArrayDeque<>();

    public boolean execute(Command command) {
        Objects.requireNonNull(command, "command");
        log.debug("Ready to execute command: {}", command);
        boolean ok = command.execute();

        if (!ok) {
            log.debug("Execution of {} failed", command);
            return false;
        }

        undoStack.push(command);
        redoStack.clear();
        return true;
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public boolean undo() {
        if (undoStack.isEmpty()) return false;

        Command cmd = undoStack.pop();
        log.debug("Ready to undo command: {}", cmd);
        cmd.undo();
        redoStack.push(cmd);
        return true;
    }

    public boolean redo() {
        if (redoStack.isEmpty()) return false;

        Command cmd = redoStack.pop();
        log.debug("Ready to redo command: {}", cmd);
        boolean ok = cmd.execute();

        if (!ok) {
            log.debug("Redo of command {} failed.", cmd);
            return false;
        }

        undoStack.push(cmd);
        return true;
    }

    public void clearHistory() {
        log.debug("Ready to clear caches");
        undoStack.clear();
        redoStack.clear();
    }
}
