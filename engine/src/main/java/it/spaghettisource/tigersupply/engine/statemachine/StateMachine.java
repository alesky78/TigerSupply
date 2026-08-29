package it.spaghettisource.tigersupply.engine.statemachine;

public interface StateMachine {

	/**
	 * process the event
	 * 
	 * @throws StateMachineException
	 */
	public void event() throws StateMachineException;
	
	/**
	 * used to set the start state
	 * 
	 * @param state
	 */
	public void setState(State state);	
	
	
	public void setTrxManager(TransactionManager trxManager);	
	
}
