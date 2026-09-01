package it.spaghettisource.tigersupply.game.entity;

import it.spaghettisource.tigersupply.engine.entity.EntityGroupScreenBound;

/**
 * Screen-bound group of the active enemy entities of a level. Sequencing (spawning waves, gating on
 * timers/clear, ending the level) is no longer its concern — that now lives in
 * {@code game.scene.director.LevelDirector}; this class only holds and updates the live enemies.
 *
 * @author Alessandro D'Ottavio
 *
 */
public class EnemyGroup extends EntityGroupScreenBound<Enemy>{

	public void reset(){
		entities.clear();
	}

	public void updateEntity(float deltaSeconds) throws Exception {

		super.updateEntity(deltaSeconds);

		for(Enemy enemy : entities){
			enemy.scanTargetInRange();
		}

	}

}
