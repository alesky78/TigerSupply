package it.spaghettisource.tigersupply.engine.impl.scene.statemachine;


import it.spaghettisource.tigersupply.engine.statemachine.Event;
import it.spaghettisource.tigersupply.engine.statemachine.State;
import it.spaghettisource.tigersupply.engine.statemachine.StateMachineException;
import it.spaghettisource.tigersupply.engine.statemachine.StateMachineUnsupportedEvent;
import it.spaghettisource.tigersupply.engine.statemachine.StateMachineUnsupportedState;
import it.spaghettisource.tigersupply.engine.statemachine.TransactionManager;
import it.spaghettisource.tigersupply.engine.utils.StaticResources;


/**
 * 
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class EnemyTxManager implements TransactionManager {

	protected EnemyBuilderDataModel dataModel;
	
	public void setDataModel(EnemyBuilderDataModel dataModel) {
		this.dataModel = dataModel;
	}

	public State findNextState(State state, Event event) throws StateMachineException {
		StateAbstract newState = null;
		String stateName = state.getStateName();
		String EventType = event.getName();
		if(stateName.equals(StaticResources.STATE_GENERATE_HORDE)){	//GENERA UNA NUOVA ORDA
			if(EventType.equals(StaticResources.EVENT_WAIT_KILL)){
				newState = new StateWaitKill();
				newState.setDataModel(dataModel);
				return newState;
			}else if(EventType.equals(StaticResources.EVENT_WAIT_TIME)){
				newState = new StateWaitTime();
				newState.setDataModel(dataModel);
				return newState;				
			}else if(EventType.equals(StaticResources.EVENT_BOSS_GENERATED)){
				newState = new StateKillBoss();
				newState.setDataModel(dataModel);
				return newState;	
			}else{
				throw new StateMachineUnsupportedEvent("transaction manager not configure to support state:"+stateName +" generate event:"+EventType);
			}
		}else if(stateName.equals(StaticResources.STATE_WAIT_TIME)){	//ATTENDI TEMPO
			if(EventType.equals(StaticResources.EVENT_WAIT)){
				return state;
			}else if(EventType.equals(StaticResources.EVENT_NEW_HORDE)){
				newState = new StateGenerateHorde();
				newState.setDataModel(dataModel);
				return newState;				
			}else{
				throw new StateMachineUnsupportedEvent("transaction manager not configure to support state:"+stateName +" generate event:"+EventType);
			}
		}else if(stateName.equals(StaticResources.STATE_WAIT_KILL)){	//ATTENDI MORTE NEMICI
			if(EventType.equals(StaticResources.EVENT_WAIT)){
				return state;
			}else if(EventType.equals(StaticResources.EVENT_NEW_HORDE)){
				newState = new StateGenerateHorde();
				newState.setDataModel(dataModel);
				return newState;				
			}else{
				throw new StateMachineUnsupportedEvent("transaction manager not configure to support state:"+stateName +" generate event:"+EventType);
			}
		}else if(stateName.equals(StaticResources.STATE_KILL_BOSS)){	//ATTENDI MORTE BOSS
			if(EventType.equals(StaticResources.EVENT_WAIT)){
				return state;
			}else if(EventType.equals(StaticResources.EVENT_BOSS_KILLED)){
				return state;	//RITORNA SEMPRE QUI TANTO C'E ALTRA MACCHINA A STATI
			}else{
				throw new StateMachineUnsupportedEvent("transaction manager not configure to support state:"+stateName +" generate event:"+EventType);
			}
		}else{
			throw new StateMachineUnsupportedState("transaction manager not configure to support state:"+stateName);
		}
	}



}
