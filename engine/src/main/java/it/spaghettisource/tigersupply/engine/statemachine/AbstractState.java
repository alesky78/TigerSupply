package it.spaghettisource.tigersupply.engine.statemachine;

public abstract class AbstractState implements State {

	public State processState(TransactionManager txManager) throws StateMachineException{
		Event event;
		try {
			event = internalProcess();
		} catch (Exception e) {
			e.printStackTrace();
			throw new StateMachineException("error in the execution of the state",e);
		}
		return txManager.findNextState(this, event);
	}
	

	public abstract Event internalProcess() throws Exception;

}
