package it.spaghettisource.tigersupply.game.scene.statemachine;

import it.spaghettisource.tigersupply.engine.statemachine.Event;
import it.spaghettisource.tigersupply.engine.statemachine.State;
import it.spaghettisource.tigersupply.engine.statemachine.StateMachine;
import it.spaghettisource.tigersupply.engine.statemachine.StateMachineImpl;
import it.spaghettisource.tigersupply.engine.statemachine.TransitionTable;
import it.spaghettisource.tigersupply.game.scene.director.DirectorContext;

/**
 * Central definition of the level-director finite-state machine. This single class is the source of
 * truth for the machine's structure: the state-name and event-name constants, the shared
 * {@link Event} singletons the states emit, the transition graph, and the initial state. Reading it
 * top-to-bottom describes the whole machine without opening any {@code State*} class.
 *
 * <p>The event-name constants double as the vocabulary of the level XML ({@code <completionEvent
 * name="..."/>}) that a step's {@code CompletionEvent} carries, so they are the canonical names shared
 * between the FSM and the level definition.</p>
 *
 * <p>The transition graph:</p>
 * <pre>
 * awaitingTimer  --ready--> executingStep        (pending self-loop)
 * awaitingClear  --ready--> executingStep        (pending self-loop)
 * executingStep  --timed-->        awaitingTimer
 * executingStep  --cleared-->      awaitingClear
 * executingStep  --bossSpawned-->  awaitingBossDefeat
 * awaitingBossDefeat --bossDefeated--> levelCleared (final)   (pending self-loop)
 * </pre>
 *
 * @author Alessandro D'Ottavio
 */
public class LevelDirectorStateMachineFactory {

	//state names
	public static final String STATE_AWAITING_TIMER       = "awaitingTimer";
	public static final String STATE_AWAITING_CLEAR       = "awaitingClear";
	public static final String STATE_EXECUTING_STEP       = "executingStep";
	public static final String STATE_AWAITING_BOSS_DEFEAT = "awaitingBossDefeat";
	public static final String STATE_LEVEL_CLEARED        = "levelCleared";	//terminal: boss dead / level won (distinct from EVENT_BOSS_DEFEATED)

	//event names (also the level XML completionEvent vocabulary)
	public static final String EVENT_PENDING       = "pending";
	public static final String EVENT_READY         = "ready";
	public static final String EVENT_CLEARED       = "cleared";
	public static final String EVENT_TIMED         = "timed";
	public static final String EVENT_BOSS_SPAWNED  = "bossSpawned";
	public static final String EVENT_BOSS_DEFEATED = "bossDefeated";

	//shared, immutable event singletons emitted directly by states (the timed/cleared/bossSpawned
	//events are data-driven and built at runtime from each step's completion event, so not here)
	public static final Event PENDING  = new Event(EVENT_PENDING);
	public static final Event READY    = new Event(EVENT_READY);
	public static final Event DEFEATED = new Event(EVENT_BOSS_DEFEATED);

	private LevelDirectorStateMachineFactory() {
	}

	/**
	 * Builds the level-director state machine fully wired to the given context: state instances (named
	 * here), transition graph, context, and the {@code awaitingTimer} initial state.
	 *
	 * @param context the shared context threaded to every state
	 * @return a ready-to-tick {@link StateMachine}
	 */
	public static StateMachine<DirectorContext> build(DirectorContext context) {

		State<DirectorContext> awaitingTimer      = new StateAwaitingTimer(STATE_AWAITING_TIMER);
		State<DirectorContext> awaitingClear      = new StateAwaitingClear(STATE_AWAITING_CLEAR);
		State<DirectorContext> executingStep      = new StateExecutingStep(STATE_EXECUTING_STEP);
		State<DirectorContext> awaitingBossDefeat = new StateAwaitingBossDefeat(STATE_AWAITING_BOSS_DEFEAT);
		State<DirectorContext> levelCleared       = new StateLevelCleared(STATE_LEVEL_CLEARED);

		TransitionTable<DirectorContext> table = new TransitionTable<DirectorContext>();
		table.selfLoop(awaitingTimer, EVENT_PENDING);
		table.add(awaitingTimer, EVENT_READY, executingStep);
		table.selfLoop(awaitingClear, EVENT_PENDING);
		table.add(awaitingClear, EVENT_READY, executingStep);
		table.add(executingStep, EVENT_TIMED, awaitingTimer);
		table.add(executingStep, EVENT_CLEARED, awaitingClear);
		table.add(executingStep, EVENT_BOSS_SPAWNED, awaitingBossDefeat);
		table.selfLoop(awaitingBossDefeat, EVENT_PENDING);
		table.add(awaitingBossDefeat, EVENT_BOSS_DEFEATED, levelCleared);

		//state machine for the life cycle of a level
		//AWAITING_CLEAR <-> EXECUTING STEP
		//AWAITING_TIMER <-> EXECUTING STEP
		//EXECUTING STEP -> AWAITING BOSS DEFEAT -> LEVEL CLEARED (final)
		StateMachine<DirectorContext> stateMachine = new StateMachineImpl<DirectorContext>();
		stateMachine.setTransitionTable(table);
		stateMachine.setContext(context);
		stateMachine.setState(awaitingTimer);

		return stateMachine;
	}

}
