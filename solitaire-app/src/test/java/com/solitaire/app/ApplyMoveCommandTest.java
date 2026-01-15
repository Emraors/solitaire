package com.solitaire.app;

import static org.junit.jupiter.api.Assertions.*;

import com.solitaire.domain.*;
import com.solitaire.domain.rules.Rules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

final class ApplyMoveCommandTest {

    private GameState gameState;
    private Board initialBoard;
    private Move validMove;
    private Move invalidMove;

    @BeforeEach
    void setUp() {
        initialBoard =
                new Board(
                        new Cell[][] {
                            {Cell.PEG, Cell.PEG, Cell.EMPTY},
                        });

        Rules rules =
                new Rules() {
                    @Override
                    public boolean isLegal(Board board, Move move) {
                        return move.equals(validMove);
                    }

                    @Override
                    public java.util.List<Move> legalMoves(Board board) {
                        return java.util.List.of(validMove);
                    }

                    @Override
                    public GameStatus status(Board board) {
                        return GameStatus.RUNNING;
                    }
                };

        gameState = new GameState(initialBoard, rules);

        validMove = new Move(new Position(0, 0), new Position(0, 1), new Position(0, 2));
        invalidMove = new Move(new Position(0, 2), new Position(0, 1), new Position(0, 0));
    }

    @Test
    @DisplayName("should execute a valid move and return true")
    void shouldExecuteValidMove() {
        ApplyMoveCommand command = new ApplyMoveCommand(gameState, validMove);

        boolean result = command.execute();

        assertTrue(result);
        assertNotEquals(initialBoard, gameState.board());
    }

    @Test
    @DisplayName("should reject an invalid move and return false")
    void shouldRejectInvalidMove() {
        ApplyMoveCommand command = new ApplyMoveCommand(gameState, invalidMove);

        boolean result = command.execute();

        assertFalse(result);
        assertEquals(initialBoard, gameState.board());
    }

    @Test
    @DisplayName("should not allow executing the same command twice")
    void shouldNotExecuteTwice() {
        ApplyMoveCommand command = new ApplyMoveCommand(gameState, validMove);

        boolean firstResult = command.execute();
        boolean secondResult = command.execute();

        assertTrue(firstResult);
        assertFalse(secondResult);
    }

    @Test
    @DisplayName("should restore the board to its previous state when undoing an executed move")
    void shouldUndoExecutedMove() {
        ApplyMoveCommand command = new ApplyMoveCommand(gameState, validMove);

        command.execute();
        Board afterExecute = gameState.board();
        command.undo();

        assertEquals(initialBoard, gameState.board());
        assertNotEquals(afterExecute, gameState.board());
    }

    @Test
    @DisplayName("should have no effect when undoing an unexecuted command")
    void shouldNotUndoUnexecutedMove() {
        ApplyMoveCommand command = new ApplyMoveCommand(gameState, validMove);

        command.undo();

        assertEquals(initialBoard, gameState.board());
    }

    @Test
    @DisplayName("should not allow undoing a command twice")
    void shouldNotUndoTwice() {
        ApplyMoveCommand command = new ApplyMoveCommand(gameState, validMove);

        command.execute();
        command.undo();
        Board afterUndo = gameState.board();
        command.undo();

        assertEquals(afterUndo, gameState.board());
        assertEquals(initialBoard, gameState.board());
    }

    @Test
    @DisplayName("should correctly update board cells using applyUnchecked")
    void shouldApplyMoveUsingApplyUnchecked() {
        ApplyMoveCommand command = new ApplyMoveCommand(gameState, validMove);

        command.execute();
        Board resultBoard = gameState.board();

        assertEquals(Cell.EMPTY, resultBoard.cellAt(new Position(0, 0)));
        assertEquals(Cell.EMPTY, resultBoard.cellAt(new Position(0, 1)));
        assertEquals(Cell.PEG, resultBoard.cellAt(new Position(0, 2)));
    }

    @Test
    @DisplayName("should throw NullPointerException when gameState is null")
    void shouldThrowExceptionWhenGameStateIsNull() {
        assertThrows(NullPointerException.class, () -> new ApplyMoveCommand(null, validMove));
    }

    @Test
    @DisplayName("should throw NullPointerException when move is null")
    void shouldThrowExceptionWhenMoveIsNull() {
        assertThrows(NullPointerException.class, () -> new ApplyMoveCommand(gameState, null));
    }

    @Test
    @DisplayName("should allow re-executing a command after it has been undone")
    void shouldAllowReExecuteAfterUndo() {
        ApplyMoveCommand command = new ApplyMoveCommand(gameState, validMove);

        boolean firstExecute = command.execute();
        command.undo();
        boolean secondExecute = command.execute();

        assertTrue(firstExecute);
        assertTrue(secondExecute);
        assertNotEquals(initialBoard, gameState.board());
    }
}
