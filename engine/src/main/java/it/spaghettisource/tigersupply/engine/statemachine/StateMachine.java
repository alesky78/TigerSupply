package it.spaghettisource.tigersupply.engine.statemachine;

/**
 * A finite-state machine that advances at most one transition per {@link #tick() tick}, threading a
 * shared context to its states and halting once it reaches a final state.
 *
 * @param <C> the shared context type threaded through the machine to its states
 */
public interface StateMachine<C> {

	/**
	 * Advances the machine by one tick. If the current state is final the call is a no-op; otherwise it
	 * processes the current state to obtain the {@link Event} it produces, resolves the next state from
	 * the transition table using that event, invokes {@code onEnter} on the next state only when the
	 * state actually changes (self-transitions are skipped), and adopts it as the new current state.
	 *
	 * @throws StateMachineException if processing the current state fails, or if the (state, event) pair
	 *         is not declared in the transition table
	 */
	public void tick() throws StateMachineException;

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
