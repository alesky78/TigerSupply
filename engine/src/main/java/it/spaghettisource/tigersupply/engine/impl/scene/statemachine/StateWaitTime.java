package it.spaghettisource.tigersupply.engine.impl.scene.statemachine;

import it.spaghettisource.tigersupply.engine.statemachine.Event;
import it.spaghettisource.tigersupply.engine.utils.StaticResources;

public class StateWaitTime extends StateAbstract {

	private boolean init = false;
	
	public String getStateName() {
		return StaticResources.STATE_WAIT_TIME;
	}

	public Event internalProcess() {
		
		if(!init){
			dataModel.resetElapsedTime();
			init = true;
		}
		
		if(dataModel.elapsedTime >1)
			return new Event(StaticResources.EVENT_NEW_HORDE);
		return new Event(StaticResources.EVENT_WAIT);
	}

}
