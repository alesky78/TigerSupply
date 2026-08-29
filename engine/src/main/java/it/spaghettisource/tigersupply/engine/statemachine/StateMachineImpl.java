package it.spaghettisource.tigersupply.engine.statemachine;

public class StateMachineImpl implements StateMachine{

	private TransactionManager trxManager;
	private State state;
		
	public void event() throws StateMachineException{
			this.state = state.processState(trxManager); //process the actual state and get the new one if changed
	}

	public void setTrxManager(TransactionManager trxManager) {
		this.trxManager = trxManager;
	}

	public void setState(State state) {
		this.state = state;
	}
	
	

}
