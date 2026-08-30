package it.spaghettisource.tigersupply.game.scene.statemachine;

import it.spaghettisource.tigersupply.game.builder.HordeSequencer;
import it.spaghettisource.tigersupply.engine.statemachine.Event;

public class EnemyBuilderDataModel {
	
	protected double elapsedTime;
	protected HordeSequencer enemyDataManager;


	public void setEnemyDataManager(HordeSequencer enemyDataManager) {
		this.enemyDataManager = enemyDataManager;
	}

	public boolean isKilledAllEnemiesInScene(){
		return enemyDataManager.isEnemyManagerEmpty();
	}
	
	public void bossKilled(){
		enemyDataManager.markBossAsKilled();
	}
	
	public Event newHordeEnterInScene() throws Exception{
		return enemyDataManager.spawnNextHorde();
	}	
	
	public void increaseElapsedTime(double time){
		elapsedTime += time;
	}
	
	public void resetElapsedTime(){
		elapsedTime = 0;
	}			
	
	
	
}
