package it.spaghettisource.tigersupply.game.scene.statemachine;

import it.spaghettisource.tigersupply.engine.statemachine.AbstractState;
import it.spaghettisource.tigersupply.engine.statemachine.Event;
import it.spaghettisource.tigersupply.game.scene.director.DirectorContext;


public class StateAwaitingBossDefeat extends AbstractState<DirectorContext> {

	public StateAwaitingBossDefeat(String stateName) {
		super(stateName);
	}

	public Event internalProcess(DirectorContext context) {
		
		if(context.areAllEnemiesKilled()){
			return LevelDirectorStateMachineFactory.DEFEATED;
		}

		return LevelDirectorStateMachineFactory.PENDING;
	}

}
