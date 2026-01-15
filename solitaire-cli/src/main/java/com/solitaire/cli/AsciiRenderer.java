package com.solitaire.cli;

import com.solitaire.domain.Board;
import com.solitaire.domain.Cell;
import com.solitaire.domain.Position;

public final class AsciiRenderer {

    public String render(Board board) {
        StringBuilder sb = new StringBuilder();

        sb.append("    ");
        for (int c = 0; c < board.cols(); c++) {
            sb.append(c).append(' ');
        }
        sb.append('\n');

        sb.append("   +");
        for (int c = 0; c < board.cols(); c++) {
            sb.append("--");
        }
        sb.append("+\n");

        for (int r = 0; r < board.rows(); r++) {
            sb.append(String.format("%2d |", r));

            for (int c = 0; c < board.cols(); c++) {
                Cell cell = board.cellAt(new Position(r, c));
                sb.append(toChar(cell)).append(' ');
            }

            sb.append("| ").append(r).append('\n');
        }

        sb.append("   +");
        for (int c = 0; c < board.cols(); c++) {
            sb.append("--");
        }
        sb.append("+\n");

        sb.append("    ");
        for (int c = 0; c < board.cols(); c++) {
            sb.append(c).append(' ');
        }
        sb.append('\n');

        return sb.toString();
    }

    private char toChar(Cell cell) {
        return switch (cell) {
            case INVALID -> ' ';
            case EMPTY -> '.';
            case PEG -> 'o';
        };
    }
}
