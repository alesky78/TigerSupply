package it.spaghettisource.tigersupply.engine.statemachine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A declarative transition table for a {@link StateMachine}: transitions are declared with
 * {@link #add(State, String, State)} / {@link #selfLoop(State, String)} and resolved by the
 * {@code (stateName, eventName)} pair through {@link #next(State, Event)}.
 *
 * <p>Finality is a property of the {@link State} itself ({@link State#isFinal()}); a final state has
 * no outgoing transitions because the machine halts on it, so it never needs an entry here.</p>
 *
 * @param <C> the shared context type of the states held by this table
 */
public class TransitionTable<C> {

	private final Map<String, State<C>> transitions = new HashMap<String, State<C>>();
	private final Set<String> knownStates = new HashSet<String>();

	/**
	 * Declares that, in {@code from}, the given event leads to {@code to}.
	 *
	 * @param from  the source state
	 * @param event the event name emitted by {@code from}
	 * @param to    the resulting state
	 * @return this table, for chaining
	 */
	public TransitionTable<C> add(State<C> from, String event, State<C> to) {
		knownStates.add(from.getStateName());
		transitions.put(key(from.getStateName(), event), to);
		return this;
	}

	/**
	 * Declares a self-transition: in {@code state}, the given event keeps the machine in {@code state}.
	 *
	 * @param state the state to loop on
	 * @param event the event name that keeps the machine in {@code state}
	 * @return this table, for chaining
	 */
	public TransitionTable<C> selfLoop(State<C> state, String event) {
		return add(state, event, state);
	}

	/**
	 * Resolves the next state for the given current state and event.
	 *
	 * @param current the current state
	 * @param event   the event produced by the current state
	 * @return the declared target state
	 * @throws StateMachineUnsupportedState if the table has no transition declared from {@code current}
	 * @throws StateMachineUnsupportedEvent if no transition is declared for this state/event pair
	 */
	public State<C> next(State<C> current, Event event) throws StateMachineException {
		String stateName = current.getStateName();
		if (!knownStates.contains(stateName)) {
			throw new StateMachineUnsupportedState("transition table not configured to support state:" + stateName);
		}
		State<C> target = transitions.get(key(stateName, event.getName()));
		if (target == null) {
			throw new StateMachineUnsupportedEvent("transition table not configured to support state:" + stateName + " event:" + event.getName());
		}
		return target;
	}

	private String key(String stateName, String eventName) {
		return stateName + '\u0000' + eventName;
	}

}
