package com.solitaire.domain.rules;

import com.solitaire.domain.Board;
import com.solitaire.domain.GameStatus;
import com.solitaire.domain.Move;
import java.util.List;

public interface Rules {

    boolean isLegal(Board board, Move move);

    List<Move> legalMoves(Board board);

    GameStatus status(Board board);
}
