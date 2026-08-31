package it.spaghettisource.tigersupply.engine.statemachine;

/**
 * Default {@link StateMachine} implementation that holds the current state, the transition table and the
 * shared context, advancing at most one transition per {@link #tick() tick}. It keeps no history and is
 * not thread-safe: a single caller is expected to drive it (in TigerSupply, once per frame).
 *
 * @param <C> the shared context type threaded through the machine to its states
 */
public class StateMachineImpl<C> implements StateMachine<C> {

	private TransitionTable<C> table;
	private State<C> state;
	private C context;

	public void tick() throws StateMachineException {
		if (state.isFinal()) {
			return; //final state: the machine has halted, nothing more to process
		}
		Event event = state.process(context);	//process the current state and get the event it produced
		State<C> next = table.next(state, event);	//resolve the next state from the (state, event) pair
		if (next != state) {
			next.onEnter(context);	//notify the state it has been entered (skip self-transitions)
		}
		this.state = next;
	}

	public void setTransitionTable(TransitionTable<C> table) {
		this.table = table;
	}

	public void setState(State<C> state) {
		this.state = state;
	}

	public void setContext(C context) {
		this.context = context;
	}

	public boolean isInFinalState() {
		return state.isFinal();
	}

}
