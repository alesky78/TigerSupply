package it.spaghettisource.tigersupply.game.scene.statemachine;

import it.spaghettisource.tigersupply.engine.statemachine.AbstractState;
import it.spaghettisource.tigersupply.engine.statemachine.Event;
import it.spaghettisource.tigersupply.game.utils.GameResources;

public class StateAwaitingTimer extends AbstractState<EnemySpawnContext> {

	public String getStateName() {
		return GameResources.STATE_AWAITING_TIMER;
	}

	@Override
	public void onEnter(EnemySpawnContext context) {
		context.resetElapsedTime();
	}

	public Event internalProcess(EnemySpawnContext context) {
		if (context.elapsedTime > context.waitTime)
			return new Event(GameResources.EVENT_READY);
		return new Event(GameResources.EVENT_PENDING);
	}

}
