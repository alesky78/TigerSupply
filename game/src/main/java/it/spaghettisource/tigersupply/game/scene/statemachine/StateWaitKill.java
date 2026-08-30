package it.spaghettisource.tigersupply.game.scene.statemachine;

import it.spaghettisource.tigersupply.engine.statemachine.AbstractState;
import it.spaghettisource.tigersupply.engine.statemachine.Event;
import it.spaghettisource.tigersupply.game.utils.GameResources;

public class StateWaitKill extends AbstractState<EnemySpawnContext> {

	@Override
	public String getStateName() {
		return GameResources.STATE_WAIT_KILL;
	}

	@Override
	public Event internalProcess(EnemySpawnContext context) {
		if (context.areAllEnemiesKilled())
			return new Event(GameResources.EVENT_NEW_HORDE);

		return new Event(GameResources.EVENT_WAIT);
	}

}
