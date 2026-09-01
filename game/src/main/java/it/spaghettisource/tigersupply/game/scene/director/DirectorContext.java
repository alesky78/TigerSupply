package it.spaghettisource.tigersupply.game.scene.director;

import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.EntityGroupScreenBound;
import it.spaghettisource.tigersupply.game.entity.Enemy;
import it.spaghettisource.tigersupply.game.scene.builder.LevelDataRepository;
import it.spaghettisource.tigersupply.game.scene.builder.definition.CompletionEvent;
import it.spaghettisource.tigersupply.game.scene.builder.definition.Step;
import it.spaghettisource.tigersupply.game.scene.statemachine.LevelDirectorStateMachineFactory;

/**
 * Shared context threaded to every state of the level-director state machine. It holds the timing
 * bookkeeping (elapsed/wait time) and the current step cursor, and exposes the subsystems that step
 * actions command (the enemy manager today; the base manager, background, audio later). It is the
 * broadened successor of the former {@code EnemySpawnContext}.
 *
 * @author Alessandro D'Ottavio
 */
public class DirectorContext {

	protected float elapsedTime;

	/** Seconds to wait in {@code awaitingTimer} before the next step; defaults to the 1s delay applied
	 * before the very first step, then overwritten by each {@code timed} step's declared time. */
	protected float waitTime = 1;

	private GameContext context;
	private Entity player;
	private EntityGroupScreenBound<Entity> shotManager;
	private EntityGroupScreenBound<Entity> effectManager;
	private EntityGroupScreenBound<Enemy> enemyManager;

	private LevelDataRepository levelData;
	private int stepIndex = 0;

	//---- subsystem access used by level actions ----

	public GameContext getContext() {
		return context;
	}

	public void setContext(GameContext context) {
		this.context = context;
	}

	public Entity getPlayer() {
		return player;
	}

	public void setPlayer(Entity player) {
		this.player = player;
	}

	public EntityGroupScreenBound<Entity> getShotManager() {
		return shotManager;
	}

	public void setShotManager(EntityGroupScreenBound<Entity> shotManager) {
		this.shotManager = shotManager;
	}

	public EntityGroupScreenBound<Entity> getEffectManager() {
		return effectManager;
	}

	public void setEffectManager(EntityGroupScreenBound<Entity> effectManager) {
		this.effectManager = effectManager;
	}

	public EntityGroupScreenBound<Enemy> getEnemyManager() {
		return enemyManager;
	}

	public void setEnemyManager(EntityGroupScreenBound<Enemy> enemyManager) {
		this.enemyManager = enemyManager;
	}

	public LevelDataRepository getLevelData() {
		return levelData;
	}

	public void setLevelData(LevelDataRepository levelData) {
		this.levelData = levelData;
	}

	//---- sequencing helpers used by the states ----

	public boolean areAllEnemiesKilled(){
		return !enemyManager.hasEntities();
	}

	public Step getCurrentStep(){
		return levelData.getStepByIndex(stepIndex);
	}

	public void advanceStep(){
		stepIndex += 1;
	}

	/**
	 * Honors the completion event of the step that just ran. For a {@code timed} completion the
	 * declared {@code time} is copied into {@link #waitTime} so {@code awaitingTimer} can respect it;
	 * every other completion leaves the wait untouched. The value is guaranteed parseable because the
	 * director validates it when the level loads.
	 *
	 * @param completion the completion event of the step that just executed
	 */
	public void honorCompletion(CompletionEvent completion){
		if(LevelDirectorStateMachineFactory.EVENT_TIMED.equals(completion.getName())){
			waitTime = Float.parseFloat(completion.getTime().trim());
		}
	}

	public void increaseElapsedTime(float time){
		elapsedTime += time;
	}

	public void resetElapsedTime(){
		elapsedTime = 0;
	}

	public float getElapsedTime(){
		return elapsedTime;
	}

	public float getWaitTime(){
		return waitTime;
	}

}
