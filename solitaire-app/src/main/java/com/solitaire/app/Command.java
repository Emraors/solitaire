package com.solitaire.app;

public interface Command {
    boolean execute();

    void undo();
}
