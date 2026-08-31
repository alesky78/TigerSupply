package it.spaghettisource.tigersupply.game.scene.statemachine;

import it.spaghettisource.tigersupply.engine.statemachine.AbstractState;
import it.spaghettisource.tigersupply.engine.statemachine.Event;

public class StateAwaitingTimer extends AbstractState<EnemySpawnContext> {

	public StateAwaitingTimer(String stateName) {
		super(stateName);
	}

	@Override
	public void onEnter(EnemySpawnContext context) {
		context.resetElapsedTime();
	}

	public Event internalProcess(EnemySpawnContext context) {
		if (context.elapsedTime > context.waitTime)
			return EnemySpawnStateMachineFactory.READY;
		return EnemySpawnStateMachineFactory.PENDING;
	}

}
