## Why

The enemy-spawn finite-state machine (wired in `game.entity.EnemyManager`, context
`game.scene.statemachine.EnemySpawnContext`) names its states and events in a way that is
actively confusing:

- **State/event value collisions.** `STATE_WAIT_TIME` and `EVENT_WAIT_TIME` are both the literal
  string `"waitTime"`; `STATE_WAIT_KILL` and `EVENT_WAIT_KILL` are both `"waitKill"`. The machine
  works only because the transition table keys on the `(stateName, eventName)` pair, but a reader
  cannot tell a state from the event that leads into it. The terminal state was already given a
  distinct value (`STATE_BOSS_KILLED = "bossKilledFinal"` vs `EVENT_BOSS_KILLED = "bossKilled"`)
  precisely to dodge this collision — the same discipline was never applied to the other two pairs.
- **`wait` is overloaded.** Three events start with "wait" but mean different things: `EVENT_WAIT`
  is a per-tick "condition not met, stay" guard, whereas `EVENT_WAIT_TIME` / `EVENT_WAIT_KILL` are
  routing decisions describing what kind of horde was just spawned.
- **States named as imperative verbs.** `generateHorde`, `killBoss` read as actions rather than
  phases; `bossKilledFinal` leaks the "final" implementation detail into the name even though
  finality is already expressed by `State.isFinal()`.
- **Inconsistent event/state vocabulary.** The event `newHorde` leads to state `generateHorde`
  (new vs generate); `bossGenerated` (past participle) sits alongside `waitTime`/`waitKill`
  (imperative) even though all three are "what kind of horde did I just spawn" outcomes.

## What Changes

A behaviour-preserving rename of the enemy-spawn FSM's five states and six events — the game plays
identically before and after. It touches Java identifiers **and** the three event tokens that also
appear in the level XML, so the level definition and its docs are updated in lockstep.

- **Rename the five state classes, their `GameResources.STATE_*` constants, and the string values:**

  | Current class / constant / value | New class / constant / value |
  |---|---|
  | `StateGenerateHorde` / `STATE_GENERATE_HORDE` / `"generateHorde"` | `StateSpawningHorde` / `STATE_SPAWNING_HORDE` / `"spawningHorde"` |
  | `StateWaitTime` / `STATE_WAIT_TIME` / `"waitTime"` | `StateAwaitingTimer` / `STATE_AWAITING_TIMER` / `"awaitingTimer"` |
  | `StateWaitKill` / `STATE_WAIT_KILL` / `"waitKill"` | `StateAwaitingClear` / `STATE_AWAITING_CLEAR` / `"awaitingClear"` |
  | `StateKillBoss` / `STATE_KILL_BOSS` / `"killBoss"` | `StateAwaitingBossDefeat` / `STATE_AWAITING_BOSS_DEFEAT` / `"awaitingBossDefeat"` |
  | `StateBossKilled` / `STATE_BOSS_KILLED` / `"bossKilledFinal"` | `StateLevelCleared` / `STATE_LEVEL_CLEARED` / `"levelCleared"` |

  The terminal state deliberately keeps a value (`"levelCleared"`) distinct from the boss-death
  event (`EVENT_BOSS_DEFEATED` / `"bossDefeated"`) so no state and event ever share a string — the
  very collision this change removes for the timer/clear pairs.

- **Rename the six `GameResources.EVENT_*` constants and their string values:**

  | Current constant / value | New constant / value | Surface |
  |---|---|---|
  | `EVENT_WAIT` / `"wait"` | `EVENT_PENDING` / `"pending"` | internal (Java only) |
  | `EVENT_NEW_HORDE` / `"newHorde"` | `EVENT_READY` / `"ready"` | internal (Java only) |
  | `EVENT_WAIT_TIME` / `"waitTime"` | `EVENT_HORDE_TIMED` / `"hordeTimed"` | **XML-facing** |
  | `EVENT_WAIT_KILL` / `"waitKill"` | `EVENT_HORDE_CLEARABLE` / `"hordeClearable"` | **XML-facing** |
  | `EVENT_BOSS_GENERATED` / `"bossGenerated"` | `EVENT_BOSS_SPAWNED` / `"bossSpawned"` | **XML-facing** |
  | `EVENT_BOSS_KILLED` / `"bossKilled"` | `EVENT_BOSS_DEFEATED` / `"bossDefeated"` | internal (Java only) |

- **BREAKING (level-definition data contract):** update every `<generateEvent name="...">` token in
  `game/src/main/resources/level/level-1.xml` — `waitTime` → `hordeTimed`, `waitKill` →
  `hordeClearable`, `bossGenerated` → `bossSpawned`. Any external level XML authored against the old
  tokens must be migrated. The `time` attribute and all other XML remains unchanged.
- **Update every consumer** of the renamed identifiers: `EnemyManager` (state construction +
  transition-table wiring), the five state classes, `EnemySpawnContext`, and `HordeSpawner`
  (`validateWaitTimeHordes` / `createHordeEvent` compare against the renamed event token).
- **Sync the Italian subsystem documentation** to the new vocabulary:
  `documentation/subsystems/enemy-spawn-lifecycle/` — `index.md`, `sequenziamento-horde.md`,
  `caricamento-dati-livello.md`, `aggiungere-nuovi-elementi.md` (state/event tables, prose, and the
  Mermaid diagrams). `motore-macchina-a-stati.md` describes the game-agnostic engine and needs no
  name changes.

Explicitly out of scope (intentionally unchanged): the unused, dead `GAME_STATE_*` / `GAME_EVENT_*`
constants in `GameResources` (a separate cleanup); the engine `statemachine` framework
(`State`, `Event`, `TransitionTable`, `StateMachineImpl`), which is name-agnostic and untouched;
the horde-generation behaviour, timing, fail-fast validation semantics, and every other identifier
and resource in the project.

## Capabilities

### New Capabilities

- None. This change introduces no new behaviour.

### Modified Capabilities

- None. This is a behaviour-preserving rename. The `enemy-spawn-lifecycle` spec describes sequencing
  in name-agnostic prose ("a time-gated horde", "its completion event") and never pins the literal
  `waitTime`/`waitKill`/`bossGenerated` tokens or any state name, so every requirement and scenario
  remains accurate verbatim after the rename. The `engine-state-machine` spec is game-agnostic and
  unaffected. This change therefore sets `skip_specs: true` in its `.openspec.yaml` rather than
  inventing a delta to satisfy validation.

## Impact

- **Game module (`it.spaghettisource.tigersupply.game`):** five state classes renamed in
  `game.scene.statemachine`; eleven `GameResources.STATE_*` / `EVENT_*` constants renamed with new
  string values; `EnemyManager.initComponents` (state instances + `TransitionTable` edges),
  `EnemySpawnContext.spawnNextHorde`, and `HordeSpawner` (`validateWaitTimeHordes`,
  `createHordeEvent`) updated to the new tokens.
- **Resources:** `game/src/main/resources/level/level-1.xml` — the three time/kill/boss
  `<generateEvent>` tokens re-spelled; this is the only runtime coupling to the renamed string
  values and MUST change atomically with the constants.
- **Documentation:** four of the five files under
  `documentation/subsystems/enemy-spawn-lifecycle/` updated (names, tables, Mermaid diagrams).
- **No changes** to the engine `statemachine` framework, module boundaries, public gameplay
  behaviour, timing, or any asset catalog.
