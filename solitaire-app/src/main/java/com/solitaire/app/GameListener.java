package com.solitaire.app;

import com.solitaire.domain.Board;
import com.solitaire.domain.GameStatus;

public interface GameListener {
    void onBoardChanged(Board newBoard);

    void onStatusChanged(GameStatus newStatus);
}
