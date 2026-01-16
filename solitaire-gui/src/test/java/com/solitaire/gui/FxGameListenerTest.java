package com.solitaire.gui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.solitaire.domain.Board;
import com.solitaire.domain.GameStatus;
import com.solitaire.domain.factory.DomainFactory;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class FxGameListenerTest {

    @BeforeAll
    static void initJavaFx() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException alreadyStarted) {
            // ok
        }
    }

    @Test
    void callbacksRunOnFxThread() throws Exception {
        var domain = DomainFactory.buildDomain();
        Board board = domain.board();

        CountDownLatch latch = new CountDownLatch(2);
        AtomicBoolean boardOnFx = new AtomicBoolean(false);
        AtomicBoolean statusOnFx = new AtomicBoolean(false);

        FxGameListener listener =
                new FxGameListener(
                        b -> {
                            boardOnFx.set(Platform.isFxApplicationThread());
                            latch.countDown();
                        },
                        s -> {
                            statusOnFx.set(Platform.isFxApplicationThread());
                            latch.countDown();
                        });

        listener.onBoardChanged(board);
        listener.onStatusChanged(GameStatus.RUNNING);

        assertTrue(latch.await(2, TimeUnit.SECONDS), "FX callbacks did not run in time");
        assertTrue(boardOnFx.get(), "Board callback did not run on FX thread");
        assertTrue(statusOnFx.get(), "Status callback did not run on FX thread");
    }
}

