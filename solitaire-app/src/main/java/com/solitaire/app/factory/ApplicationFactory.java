package com.solitaire.app.factory;

import com.solitaire.app.CommandManager;
import com.solitaire.app.GameState;
import com.solitaire.domain.factory.DomainObjects;

public class ApplicationFactory {

    public static ApplicationObjects buildApplicationObjects(DomainObjects domainObjects) {
        return new ApplicationObjects(
                new GameState(domainObjects.board(), domainObjects.rules()), new CommandManager());
    }
}
