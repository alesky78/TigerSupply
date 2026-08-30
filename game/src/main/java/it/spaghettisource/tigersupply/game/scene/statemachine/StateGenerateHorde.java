package it.spaghettisource.tigersupply.game.scene.statemachine;

import it.spaghettisource.tigersupply.engine.statemachine.AbstractState;
import it.spaghettisource.tigersupply.engine.statemachine.Event;
import it.spaghettisource.tigersupply.game.utils.GameResources;

public class StateGenerateHorde extends AbstractState<EnemySpawnContext> {

	public String getStateName() {
		return GameResources.STATE_GENERATE_HORDE;
	}

	/**
	 * generate the new enemy and return the associate event 
	 * @throws Exception 
	 * 
	 */
	public Event internalProcess(EnemySpawnContext context) throws Exception {
		
		//process the new horde and advance ready to run the next horde
		return context.newHordeEnterInScene();
		 
	}

}
