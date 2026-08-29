package it.spaghettisource.tigersupply.engine.statemachine;



public interface State {

	public State processState(TransactionManager txManager) throws StateMachineException;
	
	public String getStateName();
	
}
