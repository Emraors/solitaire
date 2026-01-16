package com.solitaire.cli;

import com.solitaire.app.factory.ApplicationFactory;
import com.solitaire.domain.factory.DomainFactory;
import lombok.extern.log4j.Log4j2;

@Log4j2
public final class Main {

    public static void main(String[] args) throws Exception {
        log.info("Starting Solitaire application");

        var domainObjects = DomainFactory.buildDomain();
        var applicationObjects = ApplicationFactory.buildApplicationObjects(domainObjects);

        log.debug("Game initialized with {} pegs", domainObjects.board().pegCount());
        new CliController(
                        applicationObjects.gameState(),
                        applicationObjects.manager(),
                        new AsciiRenderer(),
                        new MoveParser())
                .run();
    }
}
