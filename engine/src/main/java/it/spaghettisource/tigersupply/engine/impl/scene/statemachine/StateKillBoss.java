package it.spaghettisource.tigersupply.engine.impl.scene.statemachine;

import it.spaghettisource.tigersupply.engine.statemachine.Event;
import it.spaghettisource.tigersupply.engine.utils.StaticResources;


public class StateKillBoss extends StateAbstract {

	private final static Event KILLED = new Event(StaticResources.EVENT_BOSS_KILLED);
	private final static Event WAIT = new Event(StaticResources.EVENT_WAIT);	
	
	public String getStateName() {
		return StaticResources.STATE_KILL_BOSS;
	}

	public Event internalProcess() {
		
		if(dataModel.isKilledAllEnemiesInScene()){
			dataModel.bossKilled();
			return KILLED;
		}

		return WAIT;
	}

}
