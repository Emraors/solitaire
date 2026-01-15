package com.solitaire.domain.rules;

import com.solitaire.domain.*;
import java.util.ArrayList;
import java.util.List;

public final class EnglishRules implements Rules {

    @Override
    public boolean isLegal(Board board, Move move) {
        Position from = move.from();
        Position over = move.over();
        Position to = move.to();

        // must be valid holes (not INVALID)
        if (board.cellAt(from) == Cell.INVALID) return false;
        if (board.cellAt(over) == Cell.INVALID) return false;
        if (board.cellAt(to) == Cell.INVALID) return false;

        // occupancy rules
        if (board.cellAt(from) != Cell.PEG) return false;
        if (board.cellAt(over) != Cell.PEG) return false;
        if (board.cellAt(to) != Cell.EMPTY) return false;

        // must be orthogonal jump by 2
        int dr = to.r() - from.r();
        int dc = to.c() - from.c();

        boolean orthogonalTwo = (Math.abs(dr) == 2 && dc == 0) || (Math.abs(dc) == 2 && dr == 0);

        if (!orthogonalTwo) return false;

        // midpoint must match over
        Position expectedOver = new Position(from.r() + dr / 2, from.c() + dc / 2);
        return expectedOver.equals(over);
    }

    @Override
    public List<Move> legalMoves(Board board) {
        List<Move> moves = new ArrayList<>();

        for (int r = 0; r < board.rows(); r++) {
            for (int c = 0; c < board.cols(); c++) {
                Position from = new Position(r, c);
                if (board.cellAt(from) != Cell.PEG) continue;

                addIfLegal(board, moves, from, -2, 0);
                addIfLegal(board, moves, from, +2, 0);
                addIfLegal(board, moves, from, 0, -2);
                addIfLegal(board, moves, from, 0, +2);
            }
        }

        return moves;
    }

    private void addIfLegal(Board board, List<Move> out, Position from, int dr, int dc) {
        Position to = new Position(from.r() + dr, from.c() + dc);
        Position over = new Position(from.r() + dr / 2, from.c() + dc / 2);

        Move m = new Move(from, over, to);
        if (isLegal(board, m)) {
            out.add(m);
        }
    }

    @Override
    public GameStatus status(Board board) {
        int pegs = board.pegCount();

        if (pegs == 1) {
            return GameStatus.WON;
        }

        if (legalMoves(board).isEmpty()) {
            return GameStatus.STUCK;
        }

        return GameStatus.RUNNING;
    }
}
