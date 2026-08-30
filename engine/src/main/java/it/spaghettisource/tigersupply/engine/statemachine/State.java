package it.spaghettisource.tigersupply.engine.statemachine;

/**
 * A single node of a {@link StateMachine}, parameterized by the machine's shared context type.
 *
 * <p>A state computes an {@link Event} from the supplied context; the machine uses that event to look
 * up the next state in its {@link TransitionTable}. A state may be marked final (the machine halts on
 * it) and may react to being entered through {@link #onEnter(Object)}.</p>
 *
 * @param <C> the shared context type threaded through the machine to its states
 */
public interface State<C> {

	/**
	 * Computes the event produced by this state for the current tick.
	 *
	 * @param context the machine's shared context to read and update
	 * @return the {@link Event} the machine will use to select the next state
	 * @throws StateMachineException if the state fails to process
	 */
	public Event process(C context) throws StateMachineException;

	/**
	 * @return the unique name identifying this state within its transition table
	 */
	public String getStateName();

	/**
	 * @return {@code true} if this state is terminal; when the machine's current state is final the
	 *         machine halts (ticks become no-ops). Defaults to {@code false}.
	 */
	default boolean isFinal() {
		return false;
	}

	/**
	 * Invoked by the machine when a tick makes this state the current state (not on self-transitions),
	 * so the state can initialize per-entry data. Defaults to a no-op.
	 *
	 * @param context the machine's shared context
	 */
	default void onEnter(C context) {
	}

}
