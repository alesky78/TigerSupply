package it.spaghettisource.tigersupply.engine.statemachine;

public class StateMachineException extends RuntimeException {

	public StateMachineException(String message, Throwable exception){
		super(message, exception);
	}
	
	public StateMachineException(String message){
		super(message);
	}
	
}
