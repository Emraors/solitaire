package com.solitaire.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

final class BoardTest {

    @Test
    void testPegCount() {
        Board b =
                new Board(
                        new Cell[][] {
                            {Cell.INVALID, Cell.INVALID, Cell.INVALID},
                            {Cell.PEG, Cell.PEG, Cell.EMPTY},
                            {Cell.INVALID, Cell.INVALID, Cell.INVALID},
                        });

        assertEquals(2, b.pegCount());
    }

    @Test
    void testCellAt() {
        Board b = new Board(new Cell[][] {{Cell.EMPTY}});

        assertEquals(Cell.INVALID, b.cellAt(new Position(-1, 0)));
        assertEquals(Cell.INVALID, b.cellAt(new Position(0, 1)));
    }

    @Test
    void testApplyUnchecked() {
        Board b =
                new Board(
                        new Cell[][] {
                            {Cell.EMPTY, Cell.EMPTY, Cell.EMPTY},
                            {Cell.PEG, Cell.PEG, Cell.EMPTY},
                            {Cell.EMPTY, Cell.EMPTY, Cell.EMPTY},
                        });

        Move move =
                new Move(
                        new Position(1, 0), // from: PEG
                        new Position(1, 1), // over: PEG to jump
                        new Position(1, 2) // to: EMPTY destination
                        );

        Board result = b.applyUnchecked(move);

        assertEquals(Cell.EMPTY, result.cellAt(new Position(1, 0)));
        assertEquals(Cell.EMPTY, result.cellAt(new Position(1, 1)));
        assertEquals(Cell.PEG, result.cellAt(new Position(1, 2)));

        assertEquals(Cell.PEG, b.cellAt(new Position(1, 0)));
        assertEquals(Cell.PEG, b.cellAt(new Position(1, 1)));
        assertEquals(Cell.EMPTY, b.cellAt(new Position(1, 2)));
    }
}
