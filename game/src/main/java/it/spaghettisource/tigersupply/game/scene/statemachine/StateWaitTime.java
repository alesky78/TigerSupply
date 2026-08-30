package it.spaghettisource.tigersupply.game.scene.statemachine;

import it.spaghettisource.tigersupply.engine.statemachine.AbstractState;
import it.spaghettisource.tigersupply.engine.statemachine.Event;
import it.spaghettisource.tigersupply.game.utils.GameResources;

public class StateWaitTime extends AbstractState<EnemySpawnContext> {

	public String getStateName() {
		return GameResources.STATE_WAIT_TIME;
	}

	@Override
	public void onEnter(EnemySpawnContext context) {
		context.resetElapsedTime();
	}

	public Event internalProcess(EnemySpawnContext context) {
		if (context.elapsedTime > 1)
			return new Event(GameResources.EVENT_NEW_HORDE);
		return new Event(GameResources.EVENT_WAIT);
	}

}
