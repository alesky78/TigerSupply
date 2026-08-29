# Technology Stack

## Programming Languages

- **Java** — 17 (`<maven.compiler.release>17</maven.compiler.release>` in the root
  [pom.xml](../../../pom.xml)) — used for 100% of the source code (168 files across the
  `engine` module).

## Frameworks

- **Java AWT / Swing** — no version (part of the JDK) — windowing (`JFrame`), the render
  surface (`JPanel`), 2D drawing (`Graphics2D`, `BufferedImage`, `LookupOp`,
  `AffineTransform`, `GlyphVector`), and input events (`KeyListener`, `MouseListener`,
  `MouseMotionListener`).
- **Java Sound API** (`javax.sound.sampled`) — no version (part of the JDK) — playback of
  `.wav`/`.au` audio buffers on dedicated player threads.
- **Java XML / SAX** (`javax.xml.parsers`, `org.xml.sax`) — no version (part of the JDK) —
  parsing the level-script XML (`EnemyDataBuilderSaxXml`).
- **Java Reflection** (`java.lang.reflect`) — no version (part of the JDK) — data-driven
  instantiation of entities/algorithms named as strings in the level XML (`ClassFactory`).

No third-party application framework (e.g. Spring, LWJGL, libGDX, JavaFX) is used — the game
is built directly on the JDK standard library.

## Infrastructure

- None. TigerSupply is a local, offline desktop application with no deployed
  infrastructure, cloud services, or networking of any kind.

## Build Tools

- **Maven** — multi-module reactor (`packaging=pom` root aggregating `engine`, `game`,
  `launcher`).
- **maven-compiler-plugin** — 3.13.0
- **maven-resources-plugin** — 3.3.1
- **maven-clean-plugin** — 3.4.0
- **maven-surefire-plugin** — 3.3.0 (configured but has no tests to run today)
- **maven-jar-plugin** — 3.4.2
- **maven-install-plugin** — 3.1.2
- **maven-deploy-plugin** — 3.1.2
- **maven-site-plugin** — 3.12.1
- **maven-project-info-reports-plugin** — 3.6.1
- No shade/assembly/exec plugin is configured in any module, so there is no fat-jar/run
  target defined by the build today.

## Testing Tools

- **JUnit Jupiter** (`org.junit.jupiter:junit-jupiter-api`, `junit-jupiter-params`) — 5.11.0
  (via `org.junit:junit-bom:5.11.0` import) — declared as a `test`-scope dependency in all
  three module POMs, but **no test source files exist yet** in any module.
- No mocking library (Mockito, EasyMock, …), assertion library beyond JUnit's own
  (AssertJ, Hamcrest, …), or code-coverage tool (JaCoCo, Cobertura, …) is configured.

## Tooling / Process (not part of the shipped game)

- **OpenSpec CLI** (`@fission-ai/openspec`, installed via `npm install -g`) — used by the
  GitHub Copilot coding-agent setup workflow
  ([.github/workflows/copilot-setup-steps.yml](../../../.github/workflows/copilot-setup-steps.yml))
  for the spec-driven-change process in `openspec/`; unrelated to the game's runtime stack.
