package it.spaghettisource.tigersupply.engine.impl.scene.statemachine;

import it.spaghettisource.tigersupply.engine.statemachine.Event;
import it.spaghettisource.tigersupply.engine.utils.StaticResources;

public class StateGenerateHorde extends StateAbstract {

	public String getStateName() {
		return StaticResources.STATE_GENERATE_HORDE;
	}

	/**
	 * generate the new enemy and return the associate event 
	 * @throws Exception 
	 * 
	 */
	public Event internalProcess() throws Exception {
		
		//process the new horde and advance ready to run the next horde
		return dataModel.newHordeEnterInScene();
		 
	}
	


}
