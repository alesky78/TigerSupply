package it.spaghettisource.tigersupply.game.entity;

import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.EntityGroupScreenBound;
import it.spaghettisource.tigersupply.game.scene.statemachine.EnemySpawnContext;
import it.spaghettisource.tigersupply.game.scene.statemachine.HordeSpawner;
import it.spaghettisource.tigersupply.game.scene.statemachine.StateBossKilled;
import it.spaghettisource.tigersupply.game.scene.statemachine.StateGenerateHorde;
import it.spaghettisource.tigersupply.game.scene.statemachine.StateKillBoss;
import it.spaghettisource.tigersupply.game.scene.statemachine.StateWaitKill;
import it.spaghettisource.tigersupply.game.scene.statemachine.StateWaitTime;
import it.spaghettisource.tigersupply.engine.statemachine.StateMachine;
import it.spaghettisource.tigersupply.engine.statemachine.StateMachineImpl;
import it.spaghettisource.tigersupply.engine.statemachine.TransitionTable;
import it.spaghettisource.tigersupply.game.utils.GameResources;

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
	
	private EnemySpawnContext dataModel;
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
		
		dataModel = new EnemySpawnContext();
		dataModel.setHordeSpawner(hordeSpawner);

		//build the states once (stateless, reused as singletons) and declare the transition graph
		StateWaitTime waitTime = new StateWaitTime();
		StateWaitKill waitKill = new StateWaitKill();
		StateGenerateHorde generateHorde = new StateGenerateHorde();
		StateKillBoss killBoss = new StateKillBoss();
		StateBossKilled bossKilled = new StateBossKilled();

		TransitionTable<EnemySpawnContext> table = new TransitionTable<EnemySpawnContext>();
		table.selfLoop(waitTime, GameResources.EVENT_WAIT);
		table.add(waitTime, GameResources.EVENT_NEW_HORDE, generateHorde);
		table.selfLoop(waitKill, GameResources.EVENT_WAIT);
		table.add(waitKill, GameResources.EVENT_NEW_HORDE, generateHorde);
		table.add(generateHorde, GameResources.EVENT_WAIT_TIME, waitTime);
		table.add(generateHorde, GameResources.EVENT_WAIT_KILL, waitKill);
		table.add(generateHorde, GameResources.EVENT_BOSS_GENERATED, killBoss);
		table.selfLoop(killBoss, GameResources.EVENT_WAIT);
		table.add(killBoss, GameResources.EVENT_BOSS_KILLED, bossKilled);

		//state machine for the life cycle of the enemy in a level
		//WAIT_FOR_KILL <-> GENERATE HORDE
		//WAIT_TIME <-> GENERATE HORDE
		//GENERATE HORDE -> KILL BOSS -> BOSS KILLED (final)
		stateMachine = new StateMachineImpl<EnemySpawnContext>();
		stateMachine.setTransitionTable(table);
		stateMachine.setContext(dataModel);
		stateMachine.setState(waitTime);
		
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
