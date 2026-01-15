package com.solitaire.app;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

final class CommandManagerTest {

    private CommandManager commandManager;

    @BeforeEach
    void setUp() {
        commandManager = new CommandManager();
    }

    @Test
    @DisplayName("should execute command successfully and return true")
    void shouldExecuteCommandSuccessfullyAndReturnTrue() {
        final int[] executeCount = {0};
        Command command =
                new Command() {
                    @Override
                    public boolean execute() {
                        executeCount[0]++;
                        return true;
                    }

                    @Override
                    public void undo() {}
                };

        boolean result = commandManager.execute(command);

        assertTrue(result);
        assertEquals(1, executeCount[0]);
        assertTrue(commandManager.canUndo());
        assertFalse(commandManager.canRedo());
    }

    @Test
    @DisplayName("should return false when command execution fails")
    void shouldReturnFalseWhenCommandExecutionFails() {
        Command command =
                new Command() {
                    @Override
                    public boolean execute() {
                        return false;
                    }

                    @Override
                    public void undo() {}
                };

        boolean result = commandManager.execute(command);

        assertFalse(result);
        assertFalse(commandManager.canUndo());
        assertFalse(commandManager.canRedo());
    }

    @Test
    @DisplayName("should throw NullPointerException when executing null command")
    void shouldThrowExceptionWhenExecutingNullCommand() {
        assertThrows(NullPointerException.class, () -> commandManager.execute(null));
    }

    @Test
    @DisplayName("should return false when cannot undo with empty stack")
    void shouldReturnFalseWhenCannotUndoWithEmptyStack() {
        assertFalse(commandManager.canUndo());
        assertFalse(commandManager.undo());
    }

    @Test
    @DisplayName("should return false when cannot redo with empty stack")
    void shouldReturnFalseWhenCannotRedoWithEmptyStack() {
        assertFalse(commandManager.canRedo());
        assertFalse(commandManager.redo());
    }

    @Test
    @DisplayName("should undo command successfully")
    void shouldUndoCommandSuccessfully() {
        final int[] undoCount = {0};
        Command command =
                new Command() {
                    @Override
                    public boolean execute() {
                        return true;
                    }

                    @Override
                    public void undo() {
                        undoCount[0]++;
                    }
                };

        commandManager.execute(command);
        boolean result = commandManager.undo();

        assertTrue(result);
        assertEquals(1, undoCount[0]);
        assertFalse(commandManager.canUndo());
        assertTrue(commandManager.canRedo());
    }

    @Test
    @DisplayName("should redo command successfully")
    void shouldRedoCommandSuccessfully() {
        final int[] executeCount = {0};
        Command command =
                new Command() {
                    @Override
                    public boolean execute() {
                        executeCount[0]++;
                        return true;
                    }

                    @Override
                    public void undo() {}
                };

        commandManager.execute(command);
        commandManager.undo();
        boolean result = commandManager.redo();

        assertTrue(result);
        assertEquals(2, executeCount[0]);
        assertTrue(commandManager.canUndo());
        assertFalse(commandManager.canRedo());
    }

    @Test
    @DisplayName("should return false when redo execution fails")
    void shouldReturnFalseWhenRedoExecutionFails() {
        final int[] executeCount = {0};
        Command command =
                new Command() {
                    @Override
                    public boolean execute() {
                        executeCount[0]++;
                        return executeCount[0] == 1;
                    }

                    @Override
                    public void undo() {}
                };

        commandManager.execute(command);
        commandManager.undo();
        boolean result = commandManager.redo();

        assertFalse(result);
        assertEquals(2, executeCount[0]);
        assertFalse(commandManager.canUndo());
        assertFalse(commandManager.canRedo()); // Command is removed when redo fails
    }

    @Test
    @DisplayName("should clear redo stack when new command is executed")
    void shouldClearRedoStackWhenNewCommandIsExecuted() {
        Command command1 =
                new Command() {
                    @Override
                    public boolean execute() {
                        return true;
                    }

                    @Override
                    public void undo() {}
                };

        Command command2 =
                new Command() {
                    @Override
                    public boolean execute() {
                        return true;
                    }

                    @Override
                    public void undo() {}
                };

        commandManager.execute(command1);
        commandManager.undo();
        assertTrue(commandManager.canRedo());

        commandManager.execute(command2);

        assertFalse(commandManager.canRedo());
        assertTrue(commandManager.canUndo());
    }

    @Test
    @DisplayName("should handle multiple undo and redo operations")
    void shouldHandleMultipleUndoAndRedoOperations() {
        Command command1 =
                new Command() {
                    @Override
                    public boolean execute() {
                        return true;
                    }

                    @Override
                    public void undo() {}
                };

        Command command2 =
                new Command() {
                    @Override
                    public boolean execute() {
                        return true;
                    }

                    @Override
                    public void undo() {}
                };

        Command command3 =
                new Command() {
                    @Override
                    public boolean execute() {
                        return true;
                    }

                    @Override
                    public void undo() {}
                };

        commandManager.execute(command1);
        commandManager.execute(command2);
        commandManager.execute(command3);

        assertTrue(commandManager.undo());
        assertTrue(commandManager.undo());
        assertTrue(commandManager.canUndo());
        assertTrue(commandManager.canRedo());

        assertTrue(commandManager.redo());
        assertTrue(commandManager.redo());
        assertFalse(commandManager.canRedo());
        assertTrue(commandManager.canUndo());
    }

    @Test
    @DisplayName("should clear all history")
    void shouldClearAllHistory() {
        Command command =
                new Command() {
                    @Override
                    public boolean execute() {
                        return true;
                    }

                    @Override
                    public void undo() {}
                };

        commandManager.execute(command);
        commandManager.undo();
        assertTrue(commandManager.canRedo());

        commandManager.clearHistory();

        assertFalse(commandManager.canUndo());
        assertFalse(commandManager.canRedo());
    }

    @Test
    @DisplayName("should maintain correct state after complex operation sequence")
    void shouldMaintainCorrectStateAfterComplexOperationSequence() {
        final int[] executeCount = {0};
        final int[] undoCount = {0};

        Command command =
                new Command() {
                    @Override
                    public boolean execute() {
                        executeCount[0]++;
                        return true;
                    }

                    @Override
                    public void undo() {
                        undoCount[0]++;
                    }
                };

        commandManager.execute(command);
        commandManager.execute(command);
        commandManager.undo();
        commandManager.undo();
        commandManager.redo();
        commandManager.execute(command);

        assertEquals(4, executeCount[0]);
        assertEquals(2, undoCount[0]);
        assertTrue(commandManager.canUndo());
        assertFalse(commandManager.canRedo());
    }
}
