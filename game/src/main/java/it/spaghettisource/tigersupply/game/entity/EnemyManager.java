package it.spaghettisource.tigersupply.game.entity;

import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.manager.EntityManagerEntityRequest;
import it.spaghettisource.tigersupply.game.builder.EnemyDataManager;
import it.spaghettisource.tigersupply.game.scene.statemachine.EnemyBuilderDataModel;
import it.spaghettisource.tigersupply.game.scene.statemachine.EnemyTxManager;
import it.spaghettisource.tigersupply.game.scene.statemachine.StateWaitTime;
import it.spaghettisource.tigersupply.engine.statemachine.StateMachine;
import it.spaghettisource.tigersupply.engine.statemachine.StateMachineImpl;
import it.spaghettisource.tigersupply.game.scene.statemachine.StateAbstract;

/**
 * 
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class EnemyManager extends EntityManagerEntityRequest<Enemy>{

	
	private EntityManagerEntityRequest<Entity> shotManager;
	private EntityManagerEntityRequest<Entity> effectManager;	
	private Entity player;
	
	private EnemyBuilderDataModel dataModel;
	private EnemyTxManager txManager;
	private StateMachine stateMachine;
	
	private EnemyDataManager dataManager;
	private String levelDataFile;
	
	public EnemyManager(){
	}	
	
	public EnemyManager(String levelDataFile){
		this.levelDataFile = levelDataFile;
	}

	public void reset(){
		entities.clear();
	}
	
	public boolean isBossDeath() {
		return dataManager.isBossDeath();
	}		
	
	public void setLevelDataFile(String levelDataFile){
		this.levelDataFile = levelDataFile;
	}	

	public void initComponents() throws Exception{
		
		//set all the manager
		dataManager = new EnemyDataManager(levelDataFile);
		dataManager.setEffectManager(effectManager);
		dataManager.setShotManager(shotManager);
		dataManager.setEnemyManager(this);		
		dataManager.setPlayer(player);
		dataManager.setContext(context);
		dataManager.loadEnemyData();	//load the data			
		
		dataModel = new EnemyBuilderDataModel();
		dataModel.setEnemyDataManager(dataManager);
		
		txManager = new EnemyTxManager();
		txManager.setDataModel(dataModel);
		
		//state machine fo the management of the life cicle of the enemy in a level
		//WAIT_FOR_KILL <-> GENERATE ORDE
		//WAIT_TIME <-> GENERATE ORDE
		//GENERATE ORDE -> KILL BOSS	
		stateMachine = new StateMachineImpl();
		stateMachine.setTrxManager(txManager);
		StateAbstract state = new StateWaitTime();
		state.setDataModel(dataModel);
		stateMachine.setState(state);
		
	}
	
	public void updateEntity(float deltaSeconds) throws Exception {
		
		dataModel.increaseElapsedTime(deltaSeconds);
		stateMachine.event();
		
		super.updateEntity(deltaSeconds);		
		
		for(Enemy enemy : entities){
			enemy.scanTargetInRange();
		}
			
	}	

	public void setPlayer(Entity player) {
		this.player = player;
	}	

	public void setShotManager(EntityManagerEntityRequest<Entity> shotManager) {
		this.shotManager = shotManager;
	}
	
	public void setEffectManager(EntityManagerEntityRequest<Entity> effectManager) {
		this.effectManager = effectManager;
	}

	public void setDataManager(EnemyDataManager dataManager) {
		this.dataManager = dataManager;
	}


	
}
