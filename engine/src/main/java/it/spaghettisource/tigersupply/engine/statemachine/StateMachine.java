package it.spaghettisource.tigersupply.engine.statemachine;

/**
 * A finite-state machine that advances at most one transition per {@link #event() tick}, threading a
 * shared context to its states and halting once it reaches a final state.
 *
 * @param <C> the shared context type threaded through the machine to its states
 */
public interface StateMachine<C> {

	/**
	 * Runs one tick: unless the current state is final, it computes an event, looks up the next state
	 * in the transition table, invokes {@code onEnter} when the state changes, and adopts it.
	 *
	 * @throws StateMachineException if the current state fails or the transition is unsupported
	 */
	public void event() throws StateMachineException;

	/**
	 * used to set the start state
	 *
	 * @param state the initial state
	 */
	public void setState(State<C> state);

	/**
	 * @param table the transition table used to resolve the next state from a (state, event) pair
	 */
	public void setTransitionTable(TransitionTable<C> table);

	/**
	 * @param context the shared context provided to every state
	 */
	public void setContext(C context);

	/**
	 * @return {@code true} if the current state is final (the machine has halted)
	 */
	public boolean isInFinalState();

}
