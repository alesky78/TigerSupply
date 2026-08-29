package it.spaghettisource.tigersupply.engine.statemachine;

public interface TransactionManager {
		
	public  State findNextState(State state,Event event) throws StateMachineException;

}
