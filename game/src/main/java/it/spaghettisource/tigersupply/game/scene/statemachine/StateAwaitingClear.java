package it.spaghettisource.tigersupply.game.scene.statemachine;

import it.spaghettisource.tigersupply.engine.statemachine.AbstractState;
import it.spaghettisource.tigersupply.engine.statemachine.Event;

public class StateAwaitingClear extends AbstractState<EnemySpawnContext> {

	public StateAwaitingClear(String stateName) {
		super(stateName);
	}

	@Override
	public Event internalProcess(EnemySpawnContext context) {
		if (context.areAllEnemiesKilled())
			return EnemySpawnStateMachineFactory.READY;

		return EnemySpawnStateMachineFactory.PENDING;
	}

}
