## 1. Move exhaust emission to PlayerEngine

- [x] 1.1 Add thrust state, an effect-group setter, and the existing smoke counter to `PlayerEngine`; verify `mvn -pl game compile` succeeds.
- [x] 1.2 Make `PlayerEngine.updateEntity` retain the existing counter condition and rear-nozzle offset, create `Smoke`, and queue it in its configured effect group only while thrust is active; verify smoke appears when the engine receives active thrust and does not appear when it receives inactive thrust.
- [ ] 1.3 Preserve the engine's immortal and non-screen-removable behaviour while adding emission; verify the player engine remains visible and linked to the player after it enters and respawns.

## 2. Reduce Player to input coordination

- [x] 2.1 Retain the created `PlayerEngine` as a typed field, configure its effect group before queueing it, and remove the obsolete engine-created flag; verify `mvn -pl game compile` succeeds.
- [x] 2.2 Pass the current `right` input state to the engine each player update, then remove player-owned smoke timer, smoke-position, smoke-construction, and effect-submission code; verify `Player` no longer imports or constructs `Smoke`.

## 3. Verify preserved gameplay behaviour

- [x] 3.1 Run the Maven reactor compile or package build with JDK 17 and confirm all modules succeed.
- [x] 3.2 Launch the game and confirm that holding `L` emits exhaust smoke, releasing `L` stops new emission, and holding the forward input through entry or respawn retains the current smoke behaviour.
- [x] 3.3 Run `openspec validate move-player-smoke-to-engine --type change` and confirm the completed change artifacts validate.