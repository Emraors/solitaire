package com.solitaire.app;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public final class CommandManager {

    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final Deque<Command> redoStack = new ArrayDeque<>();

    public boolean execute(Command command) {
        Objects.requireNonNull(command, "command");

        boolean ok = command.execute();
        if (!ok) return false;

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
        cmd.undo();
        redoStack.push(cmd);
        return true;
    }

    public boolean redo() {
        if (redoStack.isEmpty()) return false;

        Command cmd = redoStack.pop();
        boolean ok = cmd.execute();

        // if redo fails, something is inconsistent
        if (!ok) return false;

        undoStack.push(cmd);
        return true;
    }

    public void clearHistory() {
        undoStack.clear();
        redoStack.clear();
    }
}
