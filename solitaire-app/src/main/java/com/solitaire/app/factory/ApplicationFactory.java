package com.solitaire.app.factory;

import com.solitaire.app.CommandManager;
import com.solitaire.app.GameState;
import com.solitaire.domain.factory.DomainFactory;
import com.solitaire.domain.factory.DomainObjects;

public class ApplicationFactory {

    /**
     * Creates a complete application for English Peg Solitaire.
     *
     * <p>This method encapsulates the domain creation and is the recommended way for UI modules
     * to initialize the game. It hides the domain factory dependency from UI layers.
     *
     * @return fully initialized ApplicationObjects with game state and command manager
     */
    public static ApplicationObjects createEnglishSolitaireGame() {
        DomainObjects domainObjects = DomainFactory.buildDomain();
        return buildApplicationObjects(domainObjects);
    }

    /**
     * Creates application objects from existing domain objects.
     *
     * <p>This method is useful for testing or when you need custom domain configuration.
     *
     * @param domainObjects pre-configured domain objects (board and rules)
     * @return ApplicationObjects wrapping the domain with command management
     */
    public static ApplicationObjects buildApplicationObjects(DomainObjects domainObjects) {
        return new ApplicationObjects(
                new GameState(domainObjects.board(), domainObjects.rules()), new CommandManager());
    }
}
