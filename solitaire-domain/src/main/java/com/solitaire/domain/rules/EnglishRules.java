package com.solitaire.domain.rules;

import com.solitaire.domain.*;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;

@Log4j2
public final class EnglishRules implements Rules {

    @Override
    public boolean isLegal(Board board, Move move) {
        Position from = move.from();
        Position over = move.over();
        Position to = move.to();

        // must be valid holes (not INVALID)
        if (board.cellAt(from) == Cell.INVALID) {
            log.debug("Move {} illegal: from position is INVALID", move);
            return false;
        }
        if (board.cellAt(over) == Cell.INVALID) {
            log.debug("Move {} illegal: over position is INVALID", move);
            return false;
        }
        if (board.cellAt(to) == Cell.INVALID) {
            log.debug("Move {} illegal: to position is INVALID", move);
            return false;
        }

        // occupancy rules
        if (board.cellAt(from) != Cell.PEG) {
            log.debug("Move {} illegal: from position has no PEG", move);
            return false;
        }
        if (board.cellAt(over) != Cell.PEG) {
            log.debug("Move {} illegal: over position has no PEG", move);
            return false;
        }
        if (board.cellAt(to) != Cell.EMPTY) {
            log.debug("Move {} illegal: to position is not EMPTY", move);
            return false;
        }

        // must be orthogonal jump by 2
        int dr = to.r() - from.r();
        int dc = to.c() - from.c();

        boolean orthogonalTwo = (Math.abs(dr) == 2 && dc == 0) || (Math.abs(dc) == 2 && dr == 0);

        if (!orthogonalTwo) {
            log.debug("Move {} illegal: not an orthogonal jump by 2", move);
            return false;
        }

        // midpoint must match over
        Position expectedOver = new Position(from.r() + dr / 2, from.c() + dc / 2);
        boolean valid = expectedOver.equals(over);
        if (!valid) {
            log.debug("Move {} illegal: over position {} doesn't match expected {}", move, over, expectedOver);
        } else {
            log.debug("Move {} is legal", move);
        }
        return valid;
    }

    @Override
    public List<Move> legalMoves(Board board) {
        log.debug("Computing legal moves for board with {} pegs", board.pegCount());
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

        log.debug("Found {} legal moves", moves.size());
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
        log.debug("Computing game status. Pegs: {}", pegs);

        if (pegs == 1) {
            log.debug("Game status: WON (1 peg remaining)");
            return GameStatus.WON;
        }

        if (legalMoves(board).isEmpty()) {
            log.debug("Game status: STUCK (no legal moves available)");
            return GameStatus.STUCK;
        }

        log.debug("Game status: RUNNING");
        return GameStatus.RUNNING;
    }
}
