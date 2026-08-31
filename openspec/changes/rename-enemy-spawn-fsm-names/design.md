## Context

See proposal.md — Why. The enemy-spawn FSM has one implementation seam (`game.entity.EnemyManager`
builds the states and the `TransitionTable`, threads `EnemySpawnContext`, and ticks the machine once
per frame). The state and event names are string constants centralized in
`game.utils.GameResources` (`STATE_*` / `EVENT_*`); the state classes read those constants, so a
rename is mostly a constant + class-name change with a handful of consumers.

The one non-local coupling is that three event tokens are also the `<generateEvent name="...">`
values authored in `game/src/main/resources/level/level-1.xml`, parsed by the SAX builder and
compared as raw strings in `HordeSpawner` (`validateWaitTimeHordes`, `createHordeEvent`) and
`EnemySpawnContext.spawnNextHorde`. Those string values must change atomically with the XML.

## Goals / Non-Goals

**Goals:**

- Remove every state/event string-value collision and give each state/event a name that reads as a
  phase (states) or a guard/outcome (events), with no behaviour change.
- Keep the rename mechanical and centralized: the machine wiring, the state classes, the two
  string-comparing helpers, the level XML, and the Italian docs stay mutually consistent.

**Non-Goals:**

- No change to the engine `statemachine` framework, transition semantics, timing, or the fail-fast
  validation behaviour.
- No cleanup of the unused `GAME_STATE_*` / `GAME_EVENT_*` constants (separate concern).
- No rename of `EnemyManager.isBossDead()` or other method names — only the FSM states and events.

## Decisions

### D1: States are named as ongoing phases; events as guards/outcomes — kept in disjoint word-spaces

States use present-continuous phrases (`spawningHorde`, `awaitingTimer`, `awaitingClear`,
`awaitingBossDefeat`) and a terminal outcome (`levelCleared`); events use short guard/outcome words
(`pending`, `ready`, `hordeTimed`, `hordeClearable`, `bossSpawned`, `bossDefeated`). Because the two
sets never share a spelling, the `(stateName, eventName)` table stays readable and the original
`waitTime==waitTime` / `waitKill==waitKill` collisions cannot recur.

- **Terminal state = `levelCleared`, not `bossDefeated`.** The boss-death *event* is `bossDefeated`;
  naming the terminal *state* `bossDefeated` too would rebuild the exact collision being removed
  (the current code already dodged it with the awkward `"bossKilledFinal"`). `levelCleared` names the
  outcome the state represents and stays distinct from the event.
- *Alternative considered:* keep values identical to the enum-like class names (e.g. state
  `bossDefeated`, event `bossDefeated`) and rely on the two-part table key. Rejected: the whole
  motivation is human readability, and identical spellings defeat it even if the machine works.

### D2: Constant string values change, not just the Java constant names

Each `STATE_*` / `EVENT_*` constant gets both a new Java name and a new string value. Changing only
the Java identifier while leaving the value as `"waitTime"` would leave the collisions and the
misleading tokens in the runtime data and in any debug output. The three XML-facing values
(`hordeTimed`, `hordeClearable`, `bossSpawned`) therefore propagate into `level-1.xml`.

- *Alternative considered:* Java-only rename, values frozen. Rejected by the chosen full scope (see
  proposal.md) — it would preserve the confusing tokens and the state/event collision.

### D3: Single-commit atomic rename, no compatibility shim

The constants, state classes, `EnemyManager` wiring, the string-comparing helpers, and `level-1.xml`
change together. No alias/back-compat layer is added for the old XML tokens: the project ships a
single bundled level and no external level packs, so a migration shim would be dead weight.

## Risks / Trade-offs

- **Stale XML token after rename → level fails to load / mis-routes.** The old
  `waitTime`/`waitKill`/`bossGenerated` tokens have no transition once the constants change, so a
  missed occurrence surfaces loudly (unsupported-event error or a validation failure), not silently.
  → Mitigation: grep the repo for each old token across `.java`, `.xml`, and docs after editing, and
  re-run the build + a smoke launch that reaches `bossSpawned` → `levelCleared`.
- **BREAKING for any externally authored level XML.** → Mitigation: documented as BREAKING in the
  proposal; the token mapping table in the docs doubles as a migration guide.
- **Docs/diagrams drift from code.** The Mermaid state diagrams and name tables in four docs embed
  the literal names. → Mitigation: update them in the same change and re-validate the diagrams.

## Migration Plan

1. Rename the eleven `GameResources` constants (names + values) in one edit.
2. Rename the five state classes and update `EnemyManager` (state instances, `TransitionTable`
   edges, the ASCII transition comment).
3. Update the string-comparing consumers (`HordeSpawner`, `EnemySpawnContext`).
4. Re-spell the three `<generateEvent>` tokens in `level-1.xml`.
5. Update the four Italian docs (name tables, prose, Mermaid diagrams).
6. Build with JDK 17 and smoke-launch to confirm the machine runs through to the terminal state;
   grep to prove zero old tokens remain.

Rollback: revert the change commit — there is no data migration or persisted state.
