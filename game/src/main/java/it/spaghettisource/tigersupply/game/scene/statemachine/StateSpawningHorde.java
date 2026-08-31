package it.spaghettisource.tigersupply.game.scene.statemachine;

import it.spaghettisource.tigersupply.engine.statemachine.AbstractState;
import it.spaghettisource.tigersupply.engine.statemachine.Event;

public class StateSpawningHorde extends AbstractState<EnemySpawnContext> {

	public StateSpawningHorde(String stateName) {
		super(stateName);
	}

	/**
	 * generate the new enemy and return the associate event 
	 * @throws Exception 
	 * 
	 */
	public Event internalProcess(EnemySpawnContext context) throws Exception {
		
		//process the new horde and advance ready to run the next horde
		return context.spawnNextHorde();
		 
	}

}
