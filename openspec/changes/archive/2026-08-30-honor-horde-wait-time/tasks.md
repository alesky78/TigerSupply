## 1. Spawn timer on a uniform float base

- [x] 1.1 In `EnemySpawnContext`, change `elapsedTime` from `double` to `float` and
      `increaseElapsedTime(double)` to `increaseElapsedTime(float)`; verify the module compiles
      (`mvn -pl game -am compile`).
- [x] 1.2 Add a `float waitTime` field to `EnemySpawnContext` with a setter (and getter/visibility
      matching the existing `elapsedTime` access from `StateWaitTime`); verify `game` compiles.

## 2. Drive and validate the delay from the level XML

- [x] 2.1 In `HordeSpawner`, parse the current horde's `generateEvent` `time` with `Float.parseFloat`
      when spawning a time-gated (`waitTime`) horde and store it into the context's `waitTime`;
      verify `game` compiles.
- [x] 2.2 In `HordeSpawner` level loading, fail fast with an `Exception` naming the offending horde
      when a `waitTime` horde has a missing/blank/unparseable `time`; a stray `time` on a
      non-`waitTime` horde is ignored. Verify by temporarily removing a `time` from a `waitTime`
      horde and confirming the level fails to load with the horde identified, then reverting.

## 3. Consume the configured delay

- [x] 3.1 In `StateWaitTime.internalProcess`, replace the hard-coded `elapsedTime > 1` with
      `elapsedTime > context.waitTime`; verify `game` compiles.

## 4. Clean the level content

- [x] 4.1 In `level-1.xml`, add `time="1"` to every bare `<generateEvent name="waitTime" />` (27
      occurrences); verify no bare `waitTime` events remain (search returns zero matches).
- [x] 4.2 In `level-1.xml`, remove the `time` attribute from every `<generateEvent name="waitKill" .../>`;
      verify no `waitKill` event carries a `time` attribute.

## 5. Verify end to end

- [ ] 5.1 Build the reactor (`mvn install`) and run the game (`mvn -pl launcher exec:java`); confirm
      the level loads and hordes still pace correctly with the authored `time` values.
- [ ] 5.2 Author a distinct `time` (e.g. `time="3"`) on one `waitTime` horde and confirm the observed
      pause before the next wave changes accordingly, then revert to the intended value.
