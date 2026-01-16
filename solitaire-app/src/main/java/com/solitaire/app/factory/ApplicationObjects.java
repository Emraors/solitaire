package com.solitaire.app.factory;

import com.solitaire.app.CommandManager;
import com.solitaire.app.GameState;

public record ApplicationObjects(GameState gameState, CommandManager manager) {}
