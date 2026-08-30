package it.spaghettisource.tigersupply.game.scene.statemachine;

import it.spaghettisource.tigersupply.game.builder.HordeSequencer;
import it.spaghettisource.tigersupply.engine.statemachine.Event;

/**
 * Shared context of the enemy-spawn state machine: it holds the running elapsed time and delegates
 * horde/scene queries to the {@link HordeSequencer}. It is threaded to every state by the machine.
 *
 * @author Alessandro D'Ottavio
 */
public class EnemySpawnContext {

	protected double elapsedTime;
	protected HordeSequencer horderSequencer;


	public void setHorderSequencer(HordeSequencer horderSequencer) {
		this.horderSequencer = horderSequencer;
	}

	public boolean isKilledAllEnemiesInScene(){
		return horderSequencer.isEnemyManagerEmpty();
	}

	public Event newHordeEnterInScene() throws Exception{
		return horderSequencer.spawnNextHorde();
	}

	public void increaseElapsedTime(double time){
		elapsedTime += time;
	}

	public void resetElapsedTime(){
		elapsedTime = 0;
	}

}
