package it.spaghettisource.tigersupply.game.scene.statemachine;

import it.spaghettisource.tigersupply.engine.statemachine.AbstractState;
import it.spaghettisource.tigersupply.engine.statemachine.Event;
import it.spaghettisource.tigersupply.game.utils.GameResources;


public class StateAwaitingBossDefeat extends AbstractState<EnemySpawnContext> {

	private final static Event DEFEATED = new Event(GameResources.EVENT_BOSS_DEFEATED);
	private final static Event PENDING = new Event(GameResources.EVENT_PENDING);	
	
	public String getStateName() {
		return GameResources.STATE_AWAITING_BOSS_DEFEAT;
	}

	public Event internalProcess(EnemySpawnContext context) {
		
		if(context.areAllEnemiesKilled()){
			return DEFEATED;
		}

		return PENDING;
	}

}
