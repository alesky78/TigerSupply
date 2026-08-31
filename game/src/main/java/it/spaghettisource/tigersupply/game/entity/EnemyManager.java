package it.spaghettisource.tigersupply.game.entity;

import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.EntityGroupScreenBound;
import it.spaghettisource.tigersupply.game.scene.statemachine.EnemySpawnContext;
import it.spaghettisource.tigersupply.game.scene.statemachine.HordeSpawner;
import it.spaghettisource.tigersupply.game.scene.statemachine.StateLevelCleared;
import it.spaghettisource.tigersupply.game.scene.statemachine.StateSpawningHorde;
import it.spaghettisource.tigersupply.game.scene.statemachine.StateAwaitingBossDefeat;
import it.spaghettisource.tigersupply.game.scene.statemachine.StateAwaitingClear;
import it.spaghettisource.tigersupply.game.scene.statemachine.StateAwaitingTimer;
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

		//build the states once (stateless, reused as singletons) and declare the transition graph
		StateAwaitingTimer awaitingTimer = new StateAwaitingTimer();
		StateAwaitingClear awaitingClear = new StateAwaitingClear();
		StateSpawningHorde spawningHorde = new StateSpawningHorde();
		StateAwaitingBossDefeat awaitingBossDefeat = new StateAwaitingBossDefeat();
		StateLevelCleared levelCleared = new StateLevelCleared();

		TransitionTable<EnemySpawnContext> table = new TransitionTable<EnemySpawnContext>();
		table.selfLoop(awaitingTimer, GameResources.EVENT_PENDING);
		table.add(awaitingTimer, GameResources.EVENT_READY, spawningHorde);
		table.selfLoop(awaitingClear, GameResources.EVENT_PENDING);
		table.add(awaitingClear, GameResources.EVENT_READY, spawningHorde);
		table.add(spawningHorde, GameResources.EVENT_HORDE_TIMED, awaitingTimer);
		table.add(spawningHorde, GameResources.EVENT_HORDE_CLEARABLE, awaitingClear);
		table.add(spawningHorde, GameResources.EVENT_BOSS_SPAWNED, awaitingBossDefeat);
		table.selfLoop(awaitingBossDefeat, GameResources.EVENT_PENDING);
		table.add(awaitingBossDefeat, GameResources.EVENT_BOSS_DEFEATED, levelCleared);

		//state machine for the life cycle of the enemy in a level
		//AWAITING_CLEAR <-> SPAWNING HORDE
		//AWAITING_TIMER <-> SPAWNING HORDE
		//SPAWNING HORDE -> AWAITING BOSS DEFEAT -> LEVEL CLEARED (final)
		stateMachine = new StateMachineImpl<EnemySpawnContext>();
		stateMachine.setTransitionTable(table);
		stateMachine.setContext(spawnContext);
		stateMachine.setState(awaitingTimer);
		
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
