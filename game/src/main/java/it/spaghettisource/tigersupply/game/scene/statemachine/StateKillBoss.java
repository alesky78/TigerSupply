package it.spaghettisource.tigersupply.game.scene.statemachine;

import it.spaghettisource.tigersupply.engine.statemachine.AbstractState;
import it.spaghettisource.tigersupply.engine.statemachine.Event;
import it.spaghettisource.tigersupply.game.utils.GameResources;


public class StateKillBoss extends AbstractState<EnemySpawnContext> {

	private final static Event KILLED = new Event(GameResources.EVENT_BOSS_KILLED);
	private final static Event WAIT = new Event(GameResources.EVENT_WAIT);	
	
	public String getStateName() {
		return GameResources.STATE_KILL_BOSS;
	}

	public Event internalProcess(EnemySpawnContext context) {
		
		if(context.areAllEnemiesKilled()){
			return KILLED;
		}

		return WAIT;
	}

}
