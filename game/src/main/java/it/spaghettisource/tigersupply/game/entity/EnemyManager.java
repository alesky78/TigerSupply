package it.spaghettisource.tigersupply.game.entity;

import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.EntityGroupScreenBound;
import it.spaghettisource.tigersupply.game.scene.statemachine.EnemySpawnContext;
import it.spaghettisource.tigersupply.game.scene.statemachine.EnemySpawnStateMachineFactory;
import it.spaghettisource.tigersupply.game.scene.statemachine.HordeSpawner;
import it.spaghettisource.tigersupply.engine.statemachine.StateMachine;

/**
 * 
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class EnemyManager extends EntityGroupScreenBound<Enemy>{

	
	private EntityGroupScreenBound<Entity> shotManager;
	private EntityGroupScreenBound<Entity> effectManager;	
	private Entity player;
	
	private EnemySpawnContext spawnContext;
	private StateMachine<EnemySpawnContext> stateMachine;
	
	private HordeSpawner hordeSpawner;
	private String levelDataFile;
	
	public EnemyManager(){
	}	
	
	public EnemyManager(String levelDataFile){
		this.levelDataFile = levelDataFile;
	}

	public void reset(){
		entities.clear();
	}
	
	public boolean isBossDead() {
		return stateMachine.isInFinalState();
	}		
	
	public void setLevelDataFile(String levelDataFile){
		this.levelDataFile = levelDataFile;
	}	

	public void initComponents() throws Exception{
		
		//set all the manager
		hordeSpawner = new HordeSpawner(levelDataFile);
		hordeSpawner.setEffectManager(effectManager);
		hordeSpawner.setShotManager(shotManager);
		hordeSpawner.setEnemyManager(this);		
		hordeSpawner.setPlayer(player);
		hordeSpawner.setContext(context);
		hordeSpawner.loadLevelData();	//load the data			
		
		spawnContext = new EnemySpawnContext();
		spawnContext.setHordeSpawner(hordeSpawner);

		//the whole enemy-spawn state machine (states, events, transition graph, initial state)
		//is defined centrally in EnemySpawnStateMachineFactory
		stateMachine = EnemySpawnStateMachineFactory.build(spawnContext);
		
	}
	
	public void updateEntity(float deltaSeconds) throws Exception {
		
		spawnContext.increaseElapsedTime(deltaSeconds);
		stateMachine.tick();
		
		super.updateEntity(deltaSeconds);		
		
		for(Enemy enemy : entities){
			enemy.scanTargetInRange();
		}
			
	}	

	public void setPlayer(Entity player) {
		this.player = player;
	}	

	public void setShotManager(EntityGroupScreenBound<Entity> shotManager) {
		this.shotManager = shotManager;
	}
	
	public void setEffectManager(EntityGroupScreenBound<Entity> effectManager) {
		this.effectManager = effectManager;
	}

	public void setHordeSpawner(HordeSpawner hordeSpawner) {
		this.hordeSpawner = hordeSpawner;
	}


	
}
