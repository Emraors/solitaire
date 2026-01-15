package com.solitaire.cli;

import static org.junit.jupiter.api.Assertions.*;

import com.solitaire.domain.Board;
import com.solitaire.domain.Cell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

final class AsciiRendererTest {

    private AsciiRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = new AsciiRenderer();
    }

    @Test
    @DisplayName("should render simple 3x3 board with all pegs")
    void shouldRenderSimple3x3BoardWithAllPegs() {
        Board board =
                new Board(
                        new Cell[][] {
                            {Cell.PEG, Cell.PEG, Cell.PEG},
                            {Cell.PEG, Cell.PEG, Cell.PEG},
                            {Cell.PEG, Cell.PEG, Cell.PEG},
                        });

        String result = renderer.render(board);

        assertNotNull(result);
        assertTrue(result.contains("0 1 2"), "Should contain column numbers");
        assertTrue(result.contains("o o o"), "Should contain pegs rendered as 'o'");
        assertTrue(result.contains("+------+"), "Should contain border lines");
        assertTrue(result.contains(" 0 |"), "Should contain row number 0");
        assertTrue(result.contains(" 1 |"), "Should contain row number 1");
        assertTrue(result.contains(" 2 |"), "Should contain row number 2");
    }

    @Test
    @DisplayName("should render board with empty cells")
    void shouldRenderBoardWithEmptyCells() {
        Board board =
                new Board(
                        new Cell[][] {
                            {Cell.PEG, Cell.EMPTY, Cell.PEG},
                            {Cell.EMPTY, Cell.PEG, Cell.EMPTY},
                            {Cell.PEG, Cell.EMPTY, Cell.PEG},
                        });

        String result = renderer.render(board);

        assertNotNull(result);
        assertTrue(result.contains("o . o"), "Should contain pattern with pegs and empty cells");
        assertTrue(result.contains(". o ."), "Should contain pattern with empty and pegs");
        assertFalse(result.contains("..."), "Should not have three consecutive dots");
    }

    @Test
    @DisplayName("should render board with invalid cells")
    void shouldRenderBoardWithInvalidCells() {
        Board board =
                new Board(
                        new Cell[][] {
                            {Cell.INVALID, Cell.PEG, Cell.INVALID},
                            {Cell.PEG, Cell.PEG, Cell.PEG},
                            {Cell.INVALID, Cell.PEG, Cell.INVALID},
                        });

        String result = renderer.render(board);

        assertNotNull(result);
        assertTrue(result.contains("  o  "), "Should contain invalid cells as spaces");
        assertTrue(result.contains("o o o"), "Should contain all pegs row");
    }

    @Test
    @DisplayName("should render English board 7x7")
    void shouldRenderEnglishBoard7x7() {
        Cell[][] cells = new Cell[7][7];

        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                cells[r][c] = isEnglishValidHole(r, c) ? Cell.PEG : Cell.INVALID;
            }
        }
        cells[3][3] = Cell.EMPTY;

        Board board = new Board(cells);
        String result = renderer.render(board);

        assertNotNull(result);
        assertTrue(result.contains("0 1 2 3 4 5 6"), "Should contain all column numbers");
        assertTrue(result.contains(" 0 |"), "Should contain row 0");
        assertTrue(result.contains(" 3 |"), "Should contain row 3");
        assertTrue(result.contains(" 6 |"), "Should contain row 6");
        assertTrue(result.contains("."), "Should contain center empty cell");
        assertTrue(result.contains("o"), "Should contain pegs");
        assertTrue(result.contains("+--------------+"), "Should contain proper border for 7 cols");
    }

    @Test
    @DisplayName("should render single cell board")
    void shouldRenderSingleCellBoard() {
        Board board = new Board(new Cell[][] {{Cell.PEG}});

        String result = renderer.render(board);

        assertNotNull(result);
        assertTrue(result.contains("0"), "Should contain column 0");
        assertTrue(result.contains("o"), "Should contain peg");
        assertTrue(result.contains("+--+"), "Should contain border for 1 col");
    }

    @Test
    @DisplayName("should render rectangular board 2x5")
    void shouldRenderRectangularBoard2x5() {
        Board board =
                new Board(
                        new Cell[][] {
                            {Cell.PEG, Cell.PEG, Cell.EMPTY, Cell.PEG, Cell.PEG},
                            {Cell.EMPTY, Cell.PEG, Cell.PEG, Cell.PEG, Cell.EMPTY},
                        });

        String result = renderer.render(board);

        assertNotNull(result);
        assertTrue(result.contains("0 1 2 3 4"), "Should contain 5 column numbers");
        assertTrue(result.contains(" 0 |"), "Should contain row 0");
        assertTrue(result.contains(" 1 |"), "Should contain row 1");
        assertTrue(result.contains("+----------+"), "Should contain border for 5 cols");
        assertTrue(result.contains("o o . o o"), "Should contain first row pattern");
        assertTrue(result.contains(". o o o ."), "Should contain second row pattern");
    }

    @Test
    @DisplayName("should render board with all empty cells")
    void shouldRenderBoardWithAllEmptyCells() {
        Board board =
                new Board(
                        new Cell[][] {
                            {Cell.EMPTY, Cell.EMPTY},
                            {Cell.EMPTY, Cell.EMPTY},
                        });

        String result = renderer.render(board);

        assertNotNull(result);
        assertTrue(result.contains(". ."), "Should contain empty cells");
        assertFalse(result.contains("o"), "Should not contain any pegs");
    }

    @Test
    @DisplayName("should render board with all invalid cells")
    void shouldRenderBoardWithAllInvalidCells() {
        Board board =
                new Board(
                        new Cell[][] {
                            {Cell.INVALID, Cell.INVALID},
                            {Cell.INVALID, Cell.INVALID},
                        });

        String result = renderer.render(board);

        assertNotNull(result);
        assertTrue(result.contains("   "), "Should contain spaces for invalid cells");
        assertFalse(result.contains("o"), "Should not contain pegs");
        assertFalse(result.contains("."), "Should not contain dots");
    }

    @Test
    @DisplayName("should include column headers at top and bottom")
    void shouldIncludeColumnHeadersAtTopAndBottom() {
        Board board = new Board(new Cell[][] {{Cell.PEG, Cell.PEG, Cell.PEG}});

        String result = renderer.render(board);

        String[] lines = result.split("\n");
        assertTrue(lines[0].contains("0 1 2"), "First line should contain column numbers");
        assertTrue(
                lines[lines.length - 1].contains("0 1 2"),
                "Last line should contain column numbers");
    }

    @Test
    @DisplayName("should include row numbers on both sides")
    void shouldIncludeRowNumbersOnBothSides() {
        Board board =
                new Board(
                        new Cell[][] {
                            {Cell.PEG}, {Cell.PEG}, {Cell.PEG},
                        });

        String result = renderer.render(board);

        assertTrue(result.contains(" 0 |"), "Should have row 0 on left");
        assertTrue(result.contains("| 0"), "Should have row 0 on right");
        assertTrue(result.contains(" 1 |"), "Should have row 1 on left");
        assertTrue(result.contains("| 1"), "Should have row 1 on right");
        assertTrue(result.contains(" 2 |"), "Should have row 2 on left");
        assertTrue(result.contains("| 2"), "Should have row 2 on right");
    }

    @Test
    @DisplayName("should handle double digit row numbers")
    void shouldHandleDoubleDigitRowNumbers() {
        Cell[][] cells = new Cell[12][3];
        for (int r = 0; r < 12; r++) {
            for (int c = 0; c < 3; c++) {
                cells[r][c] = Cell.PEG;
            }
        }

        Board board = new Board(cells);
        String result = renderer.render(board);

        assertNotNull(result);
        assertTrue(result.contains(" 9 |"), "Should contain single digit row 9");
        assertTrue(result.contains("10 |"), "Should contain double digit row 10");
        assertTrue(result.contains("11 |"), "Should contain double digit row 11");
        assertTrue(result.contains("| 10"), "Should contain row 10 on right side");
    }

    @Test
    @DisplayName("should handle double digit column numbers")
    void shouldHandleDoubleDigitColumnNumbers() {
        Cell[][] cells = new Cell[2][12];
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 12; c++) {
                cells[r][c] = Cell.PEG;
            }
        }

        Board board = new Board(cells);
        String result = renderer.render(board);

        assertNotNull(result);
        assertTrue(
                result.contains("0 1 2 3 4 5 6 7 8 9 10 11"),
                "Should contain all column numbers including double digits");
    }

    @Test
    @DisplayName("should return consistent output for same board")
    void shouldReturnConsistentOutputForSameBoard() {
        Board board =
                new Board(
                        new Cell[][] {
                            {Cell.PEG, Cell.EMPTY, Cell.PEG},
                            {Cell.EMPTY, Cell.PEG, Cell.EMPTY},
                        });

        String result1 = renderer.render(board);
        String result2 = renderer.render(board);

        assertEquals(result1, result2, "Should produce identical output for same board");
    }

    private boolean isEnglishValidHole(int r, int c) {
        boolean top = r <= 1;
        boolean bottom = r >= 5;
        boolean left = c <= 1;
        boolean right = c >= 5;

        boolean inCornerBlock = (top || bottom) && (left || right);
        return !inCornerBlock;
    }
}
