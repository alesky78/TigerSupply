package it.spaghettisource.tigersupply.engine.statemachine;

/**
 * Base {@link State} that wraps the concrete state logic in a uniform try/catch, translating any
 * failure into a {@link StateMachineException}.
 *
 * @param <C> the shared context type threaded through the machine to its states
 */
public abstract class AbstractState<C> implements State<C> {

	public Event process(C context) throws StateMachineException {
		try {
			return internalProcess(context);
		} catch (Exception e) {
			e.printStackTrace();
			throw new StateMachineException("error in the execution of the state", e);
		}
	}

	public abstract Event internalProcess(C context) throws Exception;

}
