package it.spaghettisource.tigersupply.game.scene.statemachine;

import it.spaghettisource.tigersupply.game.builder.EnemyDataManager;
import it.spaghettisource.tigersupply.engine.statemachine.Event;

public class EnemyBuilderDataModel {
	
	protected double elapsedTime;
	protected EnemyDataManager enemyDataManager;


	public void setEnemyDataManager(EnemyDataManager enemyDataManager) {
		this.enemyDataManager = enemyDataManager;
	}

	public boolean isKilledAllEnemiesInScene(){
		return enemyDataManager.isEnemyManagerEmpty();
	}
	
	public void bossKilled(){
		enemyDataManager.bossKilled();
	}
	
	public Event newHordeEnterInScene() throws Exception{
		return enemyDataManager.manageActualHordeAndGenerateEvent();
	}	
	
	public void increaseElapsedTime(double time){
		elapsedTime += time;
	}
	
	public void resetElapsedTime(){
		elapsedTime = 0;
	}			
	
	
	
}
