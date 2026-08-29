## 1. Engine — game-manager factory seam

- [x] 1.1 Add `control.GameManagerFactory` interface with `GameManager create(JPanel panel, ApplicationContext context) throws Exception`; verify engine compiles.
- [x] 1.2 Change `windows.GamePanel` to accept a `GameManagerFactory` and build its manager via `factory.create(this, context)`; verify `GamePanel` no longer imports any `impl.*` type and engine compiles.

## 2. Engine — reusable window shell

- [x] 2.1 Rename `windows.Application` to `windows.GameFrame` using a language-server rename; verify no dangling references remain and engine compiles.
- [x] 2.2 Remove `main()` from `GameFrame` and add constructor params `(String title, int width, int height, ApplicationContext context, GameManagerFactory factory)`, moving the hard-coded title and 1360x660 out of the engine; verify engine contains no `main` and compiles.

## 3. Launcher — composition root

- [x] 3.1 Create `launcher.TigerSupplyGameManagerFactory implements GameManagerFactory` returning `new it.spaghettisource.tigersupply.engine.impl.control.GameManager(panel, context)`; verify the launcher module compiles against engine.
- [x] 3.2 Create `launcher.Launcher` with `main()` that creates the `ApplicationContext`, supplies the title and 1360x660 dimensions, and constructs `GameFrame` with the factory; verify the launcher compiles.

## 4. Launcher — packaging

- [x] 4.1 Add `maven-shade-plugin` to `launcher/pom.xml` with `Main-Class: it.spaghettisource.tigersupply.launcher.Launcher`; verify `mvn -q -pl launcher -am package` produces a jar whose manifest declares the main class.
- [x] 4.2 (Optional) Add `exec-maven-plugin` to the launcher POM; verify `mvn -pl launcher exec:java` starts the application.

## 5. Verification

- [x] 5.1 Build the full reactor with `mvn -q clean package` and confirm every module compiles.
- [x] 5.2 Launch the shaded launcher jar and confirm the window opens with the configured title and 1360x660 playfield and the game plays identically (presentation -> hangar -> level -> game over) to pre-change behavior.
- [x] 5.3 Grep the engine `windows` package to confirm the shell, panel, and input listeners reference no `impl.*` types and no `main` method exists in engine, satisfying the game-agnostic requirement.
