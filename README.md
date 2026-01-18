# Solitaire

An experimentation born out of boredom: a Java-based implementation of Peg Solitaire (English Board).
The main objective of this project is simply to keep the "fun" alive and give me something different to work on, apart 
from the enterprise Java projects I usually work on daily.

## Requirements

*   Java 22 or later
*   Maven

## Building the Project

To build the entire project and run tests:

```bash
mvn clean install
```

## Running the Game

You can run the CLI application directly using Maven from the project root:

```bash
mvn -pl solitaire-cli exec:java -Dexec.mainClass="com.solitaire.cli.Main"
```

You can run the JavaFX GUI from the project root:

```bash
mvn -pl solitaire-gui javafx:run
```


