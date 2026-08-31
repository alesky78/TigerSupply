package it.spaghettisource.tigersupply.game.scene.statemachine;

import it.spaghettisource.tigersupply.engine.statemachine.Event;

/**
 * Shared context of the enemy-spawn state machine: it holds the running elapsed time, the wait delay
 * to honor before the next horde, and delegates horde/scene queries to the {@link HordeSpawner}. It
 * is threaded to every state by the machine.
 *
 * @author Alessandro D'Ottavio
 */
public class EnemySpawnContext {

	protected float elapsedTime;

	/** Seconds to wait in {@code awaitingTimer} before spawning the next horde; defaults to the 1s delay
	 * applied before the very first horde, then overwritten by each time-gated horde's declared time. */
	protected float waitTime = 1;
	
	protected HordeSpawner hordeSpawner;


	public void setHordeSpawner(HordeSpawner hordeSpawner) {
		this.hordeSpawner = hordeSpawner;
	}

	public boolean areAllEnemiesKilled(){
		return hordeSpawner.isEnemyManagerEmpty();
	}

	public Event spawnNextHorde() throws Exception{
		Event event = hordeSpawner.spawnNextHorde();
		if(EnemySpawnStateMachineFactory.EVENT_HORDE_TIMED.equals(event.getName())){
			waitTime = hordeSpawner.getCurrentWaitTime();
		}
		return event;
	}

	public void increaseElapsedTime(float time){
		elapsedTime += time;
	}

	public void resetElapsedTime(){
		elapsedTime = 0;
	}

}
