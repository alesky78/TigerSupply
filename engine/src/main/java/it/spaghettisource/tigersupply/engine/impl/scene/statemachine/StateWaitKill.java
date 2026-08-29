package it.spaghettisource.tigersupply.engine.impl.scene.statemachine;

import it.spaghettisource.tigersupply.engine.statemachine.Event;
import it.spaghettisource.tigersupply.engine.utils.StaticResources;

public class StateWaitKill extends StateAbstract {

	@Override
	public String getStateName() {
		return StaticResources.STATE_WAIT_KILL;
	}

	@Override
	public Event internalProcess() {
		if(dataModel.isKilledAllEnemiesInScene())
			return new Event(StaticResources.EVENT_NEW_HORDE);

		return new Event(StaticResources.EVENT_WAIT);
	}

}
