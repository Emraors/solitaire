package com.solitaire.domain.rules;

import static org.junit.jupiter.api.Assertions.*;

import com.solitaire.domain.*;
import java.util.List;
import org.junit.jupiter.api.Test;

final class EnglishRulesTest {

    private final EnglishRules rules = new EnglishRules();

    @Test
    void shouldAllowValidHorizontalMove() {
        Board board =
                new Board(
                        new Cell[][] {
                            {Cell.PEG, Cell.PEG, Cell.EMPTY},
                        });

        Move move = new Move(new Position(0, 0), new Position(0, 1), new Position(0, 2));

        assertTrue(rules.isLegal(board, move));
    }

    @Test
    void shouldAllowValidVerticalMove() {
        Board board =
                new Board(
                        new Cell[][] {
                            {Cell.PEG}, {Cell.PEG}, {Cell.EMPTY},
                        });

        Move move = new Move(new Position(0, 0), new Position(1, 0), new Position(2, 0));

        assertTrue(rules.isLegal(board, move));
    }

    @Test
    void shouldRejectMoveFromEmptyCell() {
        Board board =
                new Board(
                        new Cell[][] {
                            {Cell.EMPTY, Cell.PEG, Cell.EMPTY},
                        });

        Move move = new Move(new Position(0, 0), new Position(0, 1), new Position(0, 2));

        assertFalse(rules.isLegal(board, move));
    }

    @Test
    void shouldRejectMoveOverEmptyCell() {
        Board board =
                new Board(
                        new Cell[][] {
                            {Cell.PEG, Cell.EMPTY, Cell.EMPTY},
                        });

        Move move = new Move(new Position(0, 0), new Position(0, 1), new Position(0, 2));

        assertFalse(rules.isLegal(board, move));
    }

    @Test
    void shouldRejectMoveToOccupiedCell() {
        Board board =
                new Board(
                        new Cell[][] {
                            {Cell.PEG, Cell.PEG, Cell.PEG},
                        });

        Move move = new Move(new Position(0, 0), new Position(0, 1), new Position(0, 2));

        assertFalse(rules.isLegal(board, move));
    }

    @Test
    void shouldRejectMoveInvolvingInvalidCell() {
        Board board =
                new Board(
                        new Cell[][] {
                            {Cell.INVALID, Cell.PEG, Cell.EMPTY},
                        });

        Move move = new Move(new Position(0, 0), new Position(0, 1), new Position(0, 2));

        assertFalse(rules.isLegal(board, move));
    }

    @Test
    void shouldRejectDiagonalMove() {
        Board board =
                new Board(
                        new Cell[][] {
                            {Cell.PEG, Cell.EMPTY, Cell.EMPTY},
                            {Cell.EMPTY, Cell.PEG, Cell.EMPTY},
                            {Cell.EMPTY, Cell.EMPTY, Cell.EMPTY},
                        });

        Move move = new Move(new Position(0, 0), new Position(1, 1), new Position(2, 2));

        assertFalse(rules.isLegal(board, move));
    }

    @Test
    void shouldRejectInvalidJumpDistance() {
        Board board =
                new Board(
                        new Cell[][] {
                            {Cell.PEG, Cell.PEG, Cell.PEG, Cell.EMPTY},
                        });

        Move move = new Move(new Position(0, 0), new Position(0, 1), new Position(0, 3));

        assertFalse(rules.isLegal(board, move));
    }

    @Test
    void shouldReturnSingleLegalMove() {
        Board board =
                new Board(
                        new Cell[][] {
                            {Cell.PEG, Cell.PEG, Cell.EMPTY},
                        });

        List<Move> moves = rules.legalMoves(board);

        assertEquals(1, moves.size());
        Move expected = new Move(new Position(0, 0), new Position(0, 1), new Position(0, 2));
        assertEquals(expected, moves.get(0));
    }

    @Test
    void shouldReturnMultipleLegalMoves() {
        Board board =
                new Board(
                        new Cell[][] {
                            {Cell.EMPTY, Cell.EMPTY, Cell.PEG, Cell.EMPTY, Cell.EMPTY},
                            {Cell.EMPTY, Cell.EMPTY, Cell.PEG, Cell.EMPTY, Cell.EMPTY},
                            {Cell.PEG, Cell.PEG, Cell.EMPTY, Cell.PEG, Cell.PEG},
                            {Cell.EMPTY, Cell.EMPTY, Cell.PEG, Cell.EMPTY, Cell.EMPTY},
                            {Cell.EMPTY, Cell.EMPTY, Cell.PEG, Cell.EMPTY, Cell.EMPTY},
                        });

        List<Move> moves = rules.legalMoves(board);

        assertEquals(4, moves.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoLegalMoves() {
        Board board =
                new Board(
                        new Cell[][] {
                            {Cell.PEG, Cell.EMPTY, Cell.EMPTY},
                        });

        List<Move> moves = rules.legalMoves(board);

        assertTrue(moves.isEmpty());
    }

    @Test
    void shouldReturnWonStatusWithOnePeg() {
        Board board =
                new Board(
                        new Cell[][] {
                            {Cell.PEG, Cell.EMPTY, Cell.EMPTY},
                        });

        GameStatus status = rules.status(board);

        assertEquals(GameStatus.WON, status);
    }

    @Test
    void shouldReturnStuckStatusWhenNoMovesAvailable() {
        Board board =
                new Board(
                        new Cell[][] {
                            {Cell.PEG, Cell.EMPTY, Cell.PEG},
                        });

        GameStatus status = rules.status(board);

        assertEquals(GameStatus.STUCK, status);
    }

    @Test
    void shouldReturnRunningStatusWhenMovesAvailable() {
        Board board =
                new Board(
                        new Cell[][] {
                            {Cell.PEG, Cell.PEG, Cell.EMPTY},
                        });

        GameStatus status = rules.status(board);

        assertEquals(GameStatus.RUNNING, status);
    }
}
