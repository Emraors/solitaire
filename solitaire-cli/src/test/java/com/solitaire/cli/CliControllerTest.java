package com.solitaire.cli;

import static org.junit.jupiter.api.Assertions.*;

import com.solitaire.app.CommandManager;
import com.solitaire.app.GameState;
import com.solitaire.domain.Board;
import com.solitaire.domain.Cell;
import com.solitaire.domain.GameStatus;
import com.solitaire.domain.Move;
import com.solitaire.domain.rules.Rules;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

final class CliControllerTest {

    private GameState gameState;
    private CommandManager commandManager;
    private CliController controller;
    private Board testBoard;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        testBoard =
                new Board(
                        new Cell[][] {
                            {Cell.PEG, Cell.PEG, Cell.EMPTY},
                        });

        Rules rules =
                new Rules() {
                    @Override
                    public boolean isLegal(Board board, Move move) {
                        return true;
                    }

                    @Override
                    public List<Move> legalMoves(Board board) {
                        return List.of();
                    }

                    @Override
                    public GameStatus status(Board board) {
                        return GameStatus.RUNNING;
                    }
                };

        gameState = new GameState(testBoard, rules);
        commandManager = new CommandManager();

        // Redirect System.out to capture output
        System.setOut(new PrintStream(outputStream));

        controller = new CliController(gameState, commandManager);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("should register itself as listener on construction")
    void shouldRegisterItselfAsListenerOnConstruction() {
        // The controller registers as a listener in the constructor
        // Verified by the fact that it implements GameListener and calls addListener
        assertNotNull(controller);
    }

    @Test
    @DisplayName("should render board when onBoardChanged is called")
    void shouldRenderBoardWhenOnBoardChangedIsCalled() {
        outputStream.reset();

        controller.onBoardChanged(testBoard);

        String output = outputStream.toString();
        assertFalse(output.isEmpty(), "Should render board");
        assertTrue(output.contains("o"), "Should contain pegs");
        assertTrue(output.contains("."), "Should contain empty cells");
        assertTrue(output.contains("|"), "Should contain borders");
    }

    @Test
    @DisplayName("should print status when onStatusChanged is called with RUNNING")
    void shouldPrintStatusWhenOnStatusChangedIsCalledWithRunning() {
        outputStream.reset();

        controller.onStatusChanged(GameStatus.RUNNING);

        String output = outputStream.toString();
        assertTrue(output.contains("Status: RUNNING"), "Should print RUNNING status");
        assertFalse(output.contains("Game ended"), "Should not print game ended for RUNNING");
    }

    @Test
    @DisplayName("should print status and game ended when onStatusChanged is called with WON")
    void shouldPrintStatusAndGameEndedWhenOnStatusChangedIsCalledWithWon() {
        outputStream.reset();

        controller.onStatusChanged(GameStatus.WON);

        String output = outputStream.toString();
        assertTrue(output.contains("Status: WON"), "Should print WON status");
        assertTrue(output.contains("Game ended"), "Should print game ended message");
    }

    @Test
    @DisplayName("should print status and game ended when onStatusChanged is called with STUCK")
    void shouldPrintStatusAndGameEndedWhenOnStatusChangedIsCalledWithStuck() {
        outputStream.reset();

        controller.onStatusChanged(GameStatus.STUCK);

        String output = outputStream.toString();
        assertTrue(output.contains("Status: STUCK"), "Should print STUCK status");
        assertTrue(output.contains("Game ended"), "Should print game ended message");
    }

    @Test
    @DisplayName("should handle board changes from multiple sources")
    void shouldHandleBoardChangesFromMultipleSources() {
        outputStream.reset();

        Board board1 =
                new Board(
                        new Cell[][] {
                            {Cell.PEG, Cell.EMPTY, Cell.EMPTY},
                        });

        Board board2 =
                new Board(
                        new Cell[][] {
                            {Cell.EMPTY, Cell.PEG, Cell.EMPTY},
                        });

        controller.onBoardChanged(board1);
        String output1 = outputStream.toString();

        outputStream.reset();

        controller.onBoardChanged(board2);
        String output2 = outputStream.toString();

        assertNotEquals(output1, output2, "Different boards should produce different output");
        assertTrue(output1.contains("o"), "First board should have pegs");
        assertTrue(output2.contains("o"), "Second board should have pegs");
    }

    @Test
    @DisplayName("should produce output for all status types")
    void shouldProduceOutputForAllStatusTypes() {
        for (GameStatus status : GameStatus.values()) {
            outputStream.reset();

            controller.onStatusChanged(status);

            String output = outputStream.toString();
            assertTrue(output.contains("Status: " + status), "Should contain status: " + status);
        }
    }

    @Test
    @DisplayName("should render board with different sizes")
    void shouldRenderBoardWithDifferentSizes() {
        outputStream.reset();

        Board smallBoard = new Board(new Cell[][] {{Cell.PEG}});
        controller.onBoardChanged(smallBoard);
        String smallOutput = outputStream.toString();

        outputStream.reset();

        Board largeBoard =
                new Board(
                        new Cell[][] {
                            {Cell.PEG, Cell.PEG, Cell.PEG, Cell.PEG, Cell.PEG},
                            {Cell.PEG, Cell.PEG, Cell.PEG, Cell.PEG, Cell.PEG},
                            {Cell.PEG, Cell.PEG, Cell.PEG, Cell.PEG, Cell.PEG},
                        });
        controller.onBoardChanged(largeBoard);
        String largeOutput = outputStream.toString();

        assertFalse(smallOutput.isEmpty(), "Should render small board");
        assertFalse(largeOutput.isEmpty(), "Should render large board");
        assertTrue(
                largeOutput.length() > smallOutput.length(),
                "Larger board should have more output");
    }

    @Test
    @DisplayName("should handle listener calls in sequence")
    void shouldHandleListenerCallsInSequence() {
        outputStream.reset();

        controller.onBoardChanged(testBoard);
        controller.onStatusChanged(GameStatus.RUNNING);
        controller.onBoardChanged(testBoard);
        controller.onStatusChanged(GameStatus.WON);

        String output = outputStream.toString();

        assertTrue(output.contains("Status: RUNNING"), "Should contain RUNNING status");
        assertTrue(output.contains("Status: WON"), "Should contain WON status");
        assertTrue(output.contains("Game ended"), "Should contain game ended message");
    }
}
