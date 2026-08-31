package it.spaghettisource.tigersupply.game.scene.statemachine;

import it.spaghettisource.tigersupply.engine.statemachine.AbstractState;
import it.spaghettisource.tigersupply.engine.statemachine.Event;


public class StateAwaitingBossDefeat extends AbstractState<EnemySpawnContext> {

	public StateAwaitingBossDefeat(String stateName) {
		super(stateName);
	}

	public Event internalProcess(EnemySpawnContext context) {
		
		if(context.areAllEnemiesKilled()){
			return EnemySpawnStateMachineFactory.DEFEATED;
		}

		return EnemySpawnStateMachineFactory.PENDING;
	}

}
