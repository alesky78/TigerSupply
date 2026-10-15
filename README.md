# TigerSupply
TigerSupply is the repository for a retro-inspired 90s-style shoot 'em up game, featuring fast-paced arcade action, classic gameplay mechanics, and nostalgic pixel-art aesthetics

## Requirements

- **Java 17** or higher
- **Maven 3.x**

## Running

The project is a multi-module Maven reactor (`engine` → `game` → `{launcher, sandbox}`).

### Running from an IDE

You can run the program directly from any IDE by launching one of the `main` entry points
(no Maven goal involved). This runs the app straight from the compiled classes, which is handy
for setting breakpoints and debugging:

- **Game** — `it.spaghettisource.tigersupply.launcher.Launcher` (`launcher` module).
- **Sandbox** — `it.spaghettisource.tigersupply.sandbox.SandboxLauncher` (`sandbox` module).

### Running the game

Run the game directly from the reactor using the `launcher` module:

```bash
mvn -pl launcher exec:java
```

Alternatively, build the uber-jar and run it:

```bash
mvn package
java -jar launcher/target/tigersupply.jar
```

### Running the sandbox

The sandbox is a debug/development tool to run a single entity (projectile, enemy, effect) in
isolation. Run it from the reactor using the `sandbox` module:

```bash
mvn -pl sandbox exec:java
```

Alternatively, build the uber-jar and run it:

```bash
mvn package
java -jar sandbox/target/tigersupply-sandbox.jar
```

> Note: if the modules have not been built yet, run `mvn install` from the root first to install
> `engine` and `game` into the local repository.
