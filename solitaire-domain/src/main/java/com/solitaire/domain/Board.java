package com.solitaire.domain;

import java.util.Arrays;
import java.util.Objects;

public final class Board {

    private final int rows;
    private final int cols;
    private final Cell[][] cells;

    public Board(Cell[][] cells) {
        Objects.requireNonNull(cells, "cells");
        if (cells.length == 0) throw new IllegalArgumentException("empty board");

        this.rows = cells.length;
        this.cols = cells[0].length;

        this.cells = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            if (cells[r].length != cols) {
                throw new IllegalArgumentException("non-rectangular board");
            }
            System.arraycopy(cells[r], 0, this.cells[r], 0, cols);
        }
    }

    public int rows() {
        return rows;
    }

    public int cols() {
        return cols;
    }

    public boolean isInside(Position p) {
        return p.r() >= 0 && p.r() < rows && p.c() >= 0 && p.c() < cols;
    }

    public Cell cellAt(Position p) {
        if (!isInside(p)) return Cell.INVALID;
        return cells[p.r()][p.c()];
    }

    /** Returns a new board with a single cell changed. (Used by rules/apply-move logic) */
    public Board withCell(Position p, Cell newCell) {
        if (!isInside(p)) throw new IllegalArgumentException("position out of bounds: " + p);
        Cell[][] copy = deepCopyCells();
        copy[p.r()][p.c()] = newCell;
        return new Board(copy);
    }

    /** Apply the move without validating legality. Validation is delegated to Rules. */
    public Board applyUnchecked(Move move) {
        Cell[][] copy = deepCopyCells();

        Position from = move.from();
        Position over = move.over();
        Position to = move.to();

        copy[from.r()][from.c()] = Cell.EMPTY;
        copy[over.r()][over.c()] = Cell.EMPTY;
        copy[to.r()][to.c()] = Cell.PEG;

        return new Board(copy);
    }

    public int pegCount() {
        int count = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c] == Cell.PEG) count++;
            }
        }
        return count;
    }

    private Cell[][] deepCopyCells() {
        Cell[][] copy = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            System.arraycopy(this.cells[r], 0, copy[r], 0, cols);
        }
        return copy;
    }

    @Override
    public String toString() {
        return "Board{" + "rows=" + rows + ", cols=" + cols + ", pegs=" + pegCount() + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Board other)) return false;
        if (rows != other.rows || cols != other.cols) return false;
        return Arrays.deepEquals(cells, other.cells);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(cells);
    }
}
