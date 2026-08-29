package it.spaghettisource.tigersupply.game.scene.statemachine;

import it.spaghettisource.tigersupply.engine.statemachine.Event;
import it.spaghettisource.tigersupply.game.utils.GameResources;

public class StateWaitTime extends StateAbstract {

	private boolean init = false;
	
	public String getStateName() {
		return GameResources.STATE_WAIT_TIME;
	}

	public Event internalProcess() {
		
		if(!init){
			dataModel.resetElapsedTime();
			init = true;
		}
		
		if(dataModel.elapsedTime >1)
			return new Event(GameResources.EVENT_NEW_HORDE);
		return new Event(GameResources.EVENT_WAIT);
	}

}
