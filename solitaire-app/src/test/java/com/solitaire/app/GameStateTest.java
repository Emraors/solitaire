package com.solitaire.app;

import static org.junit.jupiter.api.Assertions.*;

import com.solitaire.domain.*;
import com.solitaire.domain.rules.Rules;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

final class GameStateTest {

    private Board initialBoard;
    private Rules rules;
    private GameState gameState;
    private Move validMove;

    @BeforeEach
    void setUp() {
        initialBoard =
                new Board(
                        new Cell[][] {
                            {Cell.PEG, Cell.PEG, Cell.EMPTY},
                        });

        validMove = new Move(new Position(0, 0), new Position(0, 1), new Position(0, 2));

        rules =
                new Rules() {
                    @Override
                    public boolean isLegal(Board board, Move move) {
                        return move.equals(validMove);
                    }

                    @Override
                    public List<Move> legalMoves(Board board) {
                        return List.of(validMove);
                    }

                    @Override
                    public GameStatus status(Board board) {
                        return board.pegCount() == 1 ? GameStatus.WON : GameStatus.RUNNING;
                    }
                };

        gameState = new GameState(initialBoard, rules);
    }

    @Test
    @DisplayName("should initialize with the provided board")
    void shouldInitializeWithProvidedBoard() {
        assertEquals(initialBoard, gameState.board());
    }

    @Test
    @DisplayName("should initialize with the provided rules")
    void shouldInitializeWithProvidedRules() {
        assertEquals(rules, gameState.rules());
    }

    @Test
    @DisplayName("should initialize status based on rules")
    void shouldInitializeStatusBasedOnRules() {
        assertEquals(GameStatus.RUNNING, gameState.status());
    }

    @Test
    @DisplayName("should throw NullPointerException when initialBoard is null")
    void shouldThrowExceptionWhenInitialBoardIsNull() {
        assertThrows(NullPointerException.class, () -> new GameState(null, rules));
    }

    @Test
    @DisplayName("should throw NullPointerException when rules is null")
    void shouldThrowExceptionWhenRulesIsNull() {
        assertThrows(NullPointerException.class, () -> new GameState(initialBoard, null));
    }

    @Test
    @DisplayName("should return true for legal move")
    void shouldReturnTrueForLegalMove() {
        assertTrue(gameState.isLegal(validMove));
    }

    @Test
    @DisplayName("should return false for illegal move")
    void shouldReturnFalseForIllegalMove() {
        Move invalidMove = new Move(new Position(0, 2), new Position(0, 1), new Position(0, 0));
        assertFalse(gameState.isLegal(invalidMove));
    }

    @Test
    @DisplayName("should update board when setBoard is called")
    void shouldUpdateBoardWhenSetBoardIsCalled() {
        Board newBoard =
                new Board(
                        new Cell[][] {
                            {Cell.EMPTY, Cell.EMPTY, Cell.PEG},
                        });

        gameState.setBoard(newBoard);

        assertEquals(newBoard, gameState.board());
    }

    @Test
    @DisplayName("should update status when setBoard is called")
    void shouldUpdateStatusWhenSetBoardIsCalled() {
        Board newBoard =
                new Board(
                        new Cell[][] {
                            {Cell.PEG, Cell.EMPTY, Cell.EMPTY},
                        });

        gameState.setBoard(newBoard);

        assertEquals(GameStatus.WON, gameState.status());
    }

    @Test
    @DisplayName("should throw NullPointerException when setBoard receives null")
    void shouldThrowExceptionWhenSetBoardReceivesNull() {
        assertThrows(NullPointerException.class, () -> gameState.setBoard(null));
    }

    @Test
    @DisplayName("should notify listener when board changes")
    void shouldNotifyListenerWhenBoardChanges() {
        final Board[] notifiedBoard = new Board[1];
        final int[] callCount = {0};

        GameListener listener =
                new GameListener() {
                    @Override
                    public void onBoardChanged(Board newBoard) {
                        notifiedBoard[0] = newBoard;
                        callCount[0]++;
                    }

                    @Override
                    public void onStatusChanged(GameStatus newStatus) {}
                };

        gameState.addListener(listener);

        Board newBoard =
                new Board(
                        new Cell[][] {
                            {Cell.EMPTY, Cell.EMPTY, Cell.PEG},
                        });
        gameState.setBoard(newBoard);

        assertEquals(newBoard, notifiedBoard[0]);
        assertEquals(1, callCount[0]);
    }

    @Test
    @DisplayName("should notify listener when status changes")
    void shouldNotifyListenerWhenStatusChanges() {
        final GameStatus[] notifiedStatus = new GameStatus[1];
        final int[] callCount = {0};

        GameListener listener =
                new GameListener() {
                    @Override
                    public void onBoardChanged(Board newBoard) {}

                    @Override
                    public void onStatusChanged(GameStatus newStatus) {
                        notifiedStatus[0] = newStatus;
                        callCount[0]++;
                    }
                };

        gameState.addListener(listener);

        Board newBoard =
                new Board(
                        new Cell[][] {
                            {Cell.PEG, Cell.EMPTY, Cell.EMPTY},
                        });
        gameState.setBoard(newBoard);

        assertEquals(GameStatus.WON, notifiedStatus[0]);
        assertEquals(1, callCount[0]);
    }

    @Test
    @DisplayName("should not notify status listener when status does not change")
    void shouldNotNotifyStatusListenerWhenStatusDoesNotChange() {
        final int[] statusCallCount = {0};
        final int[] boardCallCount = {0};

        GameListener listener =
                new GameListener() {
                    @Override
                    public void onBoardChanged(Board newBoard) {
                        boardCallCount[0]++;
                    }

                    @Override
                    public void onStatusChanged(GameStatus newStatus) {
                        statusCallCount[0]++;
                    }
                };

        gameState.addListener(listener);

        Board newBoard =
                new Board(
                        new Cell[][] {
                            {Cell.EMPTY, Cell.PEG, Cell.PEG},
                        });
        gameState.setBoard(newBoard);

        assertEquals(1, boardCallCount[0]);
        assertEquals(0, statusCallCount[0]);
    }

    @Test
    @DisplayName("should notify multiple listeners")
    void shouldNotifyMultipleListeners() {
        final int[] listener1Calls = {0};
        final int[] listener2Calls = {0};

        GameListener listener1 =
                new GameListener() {
                    @Override
                    public void onBoardChanged(Board newBoard) {
                        listener1Calls[0]++;
                    }

                    @Override
                    public void onStatusChanged(GameStatus newStatus) {}
                };

        GameListener listener2 =
                new GameListener() {
                    @Override
                    public void onBoardChanged(Board newBoard) {
                        listener2Calls[0]++;
                    }

                    @Override
                    public void onStatusChanged(GameStatus newStatus) {}
                };

        gameState.addListener(listener1);
        gameState.addListener(listener2);

        Board newBoard =
                new Board(
                        new Cell[][] {
                            {Cell.EMPTY, Cell.EMPTY, Cell.PEG},
                        });
        gameState.setBoard(newBoard);

        assertEquals(1, listener1Calls[0]);
        assertEquals(1, listener2Calls[0]);
    }

    @Test
    @DisplayName("should remove listener successfully")
    void shouldRemoveListenerSuccessfully() {
        final int[] callCount = {0};

        GameListener listener =
                new GameListener() {
                    @Override
                    public void onBoardChanged(Board newBoard) {
                        callCount[0]++;
                    }

                    @Override
                    public void onStatusChanged(GameStatus newStatus) {}
                };

        gameState.addListener(listener);
        gameState.removeListener(listener);

        Board newBoard =
                new Board(
                        new Cell[][] {
                            {Cell.EMPTY, Cell.EMPTY, Cell.PEG},
                        });
        gameState.setBoard(newBoard);

        assertEquals(0, callCount[0]);
    }

    @Test
    @DisplayName("should throw NullPointerException when adding null listener")
    void shouldThrowExceptionWhenAddingNullListener() {
        assertThrows(NullPointerException.class, () -> gameState.addListener(null));
    }

    @Test
    @DisplayName("should handle removing non-existent listener without error")
    void shouldHandleRemovingNonExistentListenerWithoutError() {
        GameListener listener =
                new GameListener() {
                    @Override
                    public void onBoardChanged(Board newBoard) {}

                    @Override
                    public void onStatusChanged(GameStatus newStatus) {}
                };

        assertDoesNotThrow(() -> gameState.removeListener(listener));
    }
}
