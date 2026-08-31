package it.spaghettisource.tigersupply.game.scene.statemachine;

import it.spaghettisource.tigersupply.engine.statemachine.AbstractState;
import it.spaghettisource.tigersupply.engine.statemachine.Event;
import it.spaghettisource.tigersupply.game.utils.GameResources;

public class StateAwaitingClear extends AbstractState<EnemySpawnContext> {

	@Override
	public String getStateName() {
		return GameResources.STATE_AWAITING_CLEAR;
	}

	@Override
	public Event internalProcess(EnemySpawnContext context) {
		if (context.areAllEnemiesKilled())
			return new Event(GameResources.EVENT_READY);

		return new Event(GameResources.EVENT_PENDING);
	}

}
