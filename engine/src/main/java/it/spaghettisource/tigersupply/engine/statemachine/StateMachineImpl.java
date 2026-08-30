package it.spaghettisource.tigersupply.engine.statemachine;

public class StateMachineImpl<C> implements StateMachine<C> {

	private TransitionTable<C> table;
	private State<C> state;
	private C context;

	public void event() throws StateMachineException {
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
