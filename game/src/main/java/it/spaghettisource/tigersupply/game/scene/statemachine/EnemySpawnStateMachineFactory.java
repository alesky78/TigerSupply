package it.spaghettisource.tigersupply.game.scene.statemachine;

import it.spaghettisource.tigersupply.engine.statemachine.Event;
import it.spaghettisource.tigersupply.engine.statemachine.State;
import it.spaghettisource.tigersupply.engine.statemachine.StateMachine;
import it.spaghettisource.tigersupply.engine.statemachine.StateMachineImpl;
import it.spaghettisource.tigersupply.engine.statemachine.TransitionTable;

/**
 * Central definition of the enemy-spawn finite-state machine. This single class is the source of
 * truth for the machine's structure: the state-name and event-name constants, the shared
 * {@link Event} singletons the states emit, the transition graph, and the initial state. Reading it
 * top-to-bottom describes the whole machine without opening any {@code State*} class.
 *
 * <p>The event-name constants double as the vocabulary of the level XML ({@code <generateEvent
 * name="..."/>}) that {@code HordeSpawner}/{@code EnemySpawnContext} match against, so they are the
 * canonical names shared between the FSM and the level definition.</p>
 *
 * <p>The transition graph:</p>
 * <pre>
 * awaitingTimer  --ready--> spawningHorde        (pending self-loop)
 * awaitingClear  --ready--> spawningHorde        (pending self-loop)
 * spawningHorde  --hordeTimed-->     awaitingTimer
 * spawningHorde  --hordeClearable--> awaitingClear
 * spawningHorde  --bossSpawned-->    awaitingBossDefeat
 * awaitingBossDefeat --bossDefeated--> levelCleared (final)   (pending self-loop)
 * </pre>
 *
 * @author Alessandro D'Ottavio
 */
public class EnemySpawnStateMachineFactory {

	//state names
	public static final String STATE_AWAITING_TIMER       = "awaitingTimer";
	public static final String STATE_AWAITING_CLEAR       = "awaitingClear";
	public static final String STATE_SPAWNING_HORDE       = "spawningHorde";
	public static final String STATE_AWAITING_BOSS_DEFEAT = "awaitingBossDefeat";
	public static final String STATE_LEVEL_CLEARED        = "levelCleared";	//terminal: boss dead / level won (distinct from EVENT_BOSS_DEFEATED)

	//event names (also the level XML generateEvent vocabulary)
	public static final String EVENT_PENDING         = "pending";
	public static final String EVENT_READY           = "ready";
	public static final String EVENT_HORDE_CLEARABLE = "hordeClearable";
	public static final String EVENT_HORDE_TIMED     = "hordeTimed";
	public static final String EVENT_BOSS_SPAWNED    = "bossSpawned";
	public static final String EVENT_BOSS_DEFEATED   = "bossDefeated";

	//shared, immutable event singletons emitted directly by states (the hordeTimed/hordeClearable/
	//bossSpawned events are data-driven and built at runtime by HordeSpawner, so they are not here)
	public static final Event PENDING  = new Event(EVENT_PENDING);
	public static final Event READY    = new Event(EVENT_READY);
	public static final Event DEFEATED = new Event(EVENT_BOSS_DEFEATED);

	private EnemySpawnStateMachineFactory() {
	}

	/**
	 * Builds the enemy-spawn state machine fully wired to the given context: state instances (named
	 * here), transition graph, context, and the {@code awaitingTimer} initial state.
	 *
	 * @param context the shared context threaded to every state
	 * @return a ready-to-tick {@link StateMachine}
	 */
	public static StateMachine<EnemySpawnContext> build(EnemySpawnContext context) {

		State<EnemySpawnContext> awaitingTimer      = new StateAwaitingTimer(STATE_AWAITING_TIMER);
		State<EnemySpawnContext> awaitingClear      = new StateAwaitingClear(STATE_AWAITING_CLEAR);
		State<EnemySpawnContext> spawningHorde      = new StateSpawningHorde(STATE_SPAWNING_HORDE);
		State<EnemySpawnContext> awaitingBossDefeat = new StateAwaitingBossDefeat(STATE_AWAITING_BOSS_DEFEAT);
		State<EnemySpawnContext> levelCleared       = new StateLevelCleared(STATE_LEVEL_CLEARED);

		TransitionTable<EnemySpawnContext> table = new TransitionTable<EnemySpawnContext>();
		table.selfLoop(awaitingTimer, EVENT_PENDING);
		table.add(awaitingTimer, EVENT_READY, spawningHorde);
		table.selfLoop(awaitingClear, EVENT_PENDING);
		table.add(awaitingClear, EVENT_READY, spawningHorde);
		table.add(spawningHorde, EVENT_HORDE_TIMED, awaitingTimer);
		table.add(spawningHorde, EVENT_HORDE_CLEARABLE, awaitingClear);
		table.add(spawningHorde, EVENT_BOSS_SPAWNED, awaitingBossDefeat);
		table.selfLoop(awaitingBossDefeat, EVENT_PENDING);
		table.add(awaitingBossDefeat, EVENT_BOSS_DEFEATED, levelCleared);

		//state machine for the life cycle of the enemy in a level
		//AWAITING_CLEAR <-> SPAWNING HORDE
		//AWAITING_TIMER <-> SPAWNING HORDE
		//SPAWNING HORDE -> AWAITING BOSS DEFEAT -> LEVEL CLEARED (final)
		StateMachine<EnemySpawnContext> stateMachine = new StateMachineImpl<EnemySpawnContext>();
		stateMachine.setTransitionTable(table);
		stateMachine.setContext(context);
		stateMachine.setState(awaitingTimer);

		return stateMachine;
	}

}
