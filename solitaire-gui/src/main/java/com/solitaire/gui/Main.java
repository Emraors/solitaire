package com.solitaire.gui;

import javafx.application.Application;

/**
 * JavaFX launcher class.
 *
 * <p>We keep a distinct "Main" so Maven/JavaFX plugins can reference it reliably.
 */
public final class Main {

    public static void main(String[] args) {
        Application.launch(SolitaireFxApp.class, args);
    }
}

