package it.spaghettisource.tigersupply.game.scene.director;

import java.util.List;

import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.EntityGroupScreenBound;
import it.spaghettisource.tigersupply.engine.statemachine.StateMachine;
import it.spaghettisource.tigersupply.game.entity.Enemy;
import it.spaghettisource.tigersupply.game.scene.builder.EnemyDataBuilder;
import it.spaghettisource.tigersupply.game.scene.builder.EnemyDataBuilderSaxXml;
import it.spaghettisource.tigersupply.game.scene.builder.LevelDataRepository;
import it.spaghettisource.tigersupply.game.scene.builder.definition.AlgorithmPrototype;
import it.spaghettisource.tigersupply.game.scene.builder.definition.CompletionEvent;
import it.spaghettisource.tigersupply.game.scene.builder.definition.EnemyPrototype;
import it.spaghettisource.tigersupply.game.scene.builder.definition.Step;
import it.spaghettisource.tigersupply.game.scene.statemachine.LevelDirectorStateMachineFactory;

/**
 * Owns and drives the level-director state machine. It loads the level definition, wires the shared
 * {@link DirectorContext} to the game subsystems the step actions command, builds the machine, and
 * advances it once per frame. It is the single coordinator that replaces the enemy-spawn machine that
 * used to live inside {@code EnemyManager}, and is the point where the level timeline can command more
 * than just enemies.
 *
 * @author Alessandro D'Ottavio
 */
public class LevelDirector {

	private GameContext context;
	private Entity player;
	private EntityGroupScreenBound<Entity> shotManager;
	private EntityGroupScreenBound<Entity> effectManager;
	private EntityGroupScreenBound<Enemy> enemyManager;
	private String levelDataFile;

	private DirectorContext directorContext;
	private StateMachine<DirectorContext> stateMachine;

	public void setContext(GameContext context) {
		this.context = context;
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

	public void setEnemyManager(EntityGroupScreenBound<Enemy> enemyManager) {
		this.enemyManager = enemyManager;
	}

	public void setLevelDataFile(String levelDataFile) {
		this.levelDataFile = levelDataFile;
	}

	/**
	 * Loads the level definition, validates it, wires the shared context, and builds the state machine.
	 * Must be called once, after the subsystem references have been injected, before {@link #tick(float)}.
	 *
	 * @throws Exception if the level cannot be parsed or a {@code timed} step declares an invalid time
	 */
	public void init() throws Exception {

		EnemyDataBuilder builder = new EnemyDataBuilderSaxXml(levelDataFile);
		builder.parse();

		List<Step> steps = builder.buildSteps();
		List<EnemyPrototype> enemies = builder.buildEnemyPrototypes();
		List<AlgorithmPrototype> algorithms = builder.buildAlgorithmPrototypes();

		validateTimedSteps(steps);

		LevelDataRepository levelData = new LevelDataRepository();
		levelData.setSteps(steps);
		levelData.setEnemyPrototypes(enemies);
		levelData.setAlgorithmPrototypes(algorithms);

		System.out.println(levelData);

		directorContext = new DirectorContext();
		directorContext.setContext(context);
		directorContext.setPlayer(player);
		directorContext.setShotManager(shotManager);
		directorContext.setEffectManager(effectManager);
		directorContext.setEnemyManager(enemyManager);
		directorContext.setLevelData(levelData);

		//the whole level-director state machine (states, events, transition graph, initial state)
		//is defined centrally in LevelDirectorStateMachineFactory
		stateMachine = LevelDirectorStateMachineFactory.build(directorContext);
	}

	/**
	 * Advances the level timeline by one tick, accumulating the elapsed time first.
	 *
	 * @param deltaSeconds the time elapsed since the previous frame, in seconds
	 * @throws Exception if a step action fails during execution
	 */
	public void tick(float deltaSeconds) throws Exception {
		directorContext.increaseElapsedTime(deltaSeconds);
		stateMachine.tick();
	}

	/**
	 * @return {@code true} once the machine has reached its terminal state (boss defeated, level won)
	 */
	public boolean isLevelCleared() {
		return stateMachine.isInFinalState();
	}

	/**
	 * Fails fast when a {@code timed} step declares no valid delay, forcing every such step to carry an
	 * explicit, human-readable {@code time} in seconds. A {@code time} on any other completion type is
	 * ignored and does not fail validation.
	 *
	 * @param steps the steps parsed from the level definition, in declaration order
	 * @throws Exception if a {@code timed} step has a missing, blank, or unparseable {@code time},
	 *                   naming the offending step by its zero-based index
	 */
	private void validateTimedSteps(List<Step> steps) throws Exception {
		for (int i = 0; i < steps.size(); i++) {
			CompletionEvent completion = steps.get(i).getCompletion();
			if (LevelDirectorStateMachineFactory.EVENT_TIMED.equals(completion.getName())) {
				String time = completion.getTime();
				if (time == null || time.trim().isEmpty()) {
					throw new Exception("step " + i + " uses a 'timed' completion event without a 'time' attribute; every timed step must declare an explicit time in seconds");
				}
				try {
					Float.parseFloat(time.trim());
				} catch (NumberFormatException e) {
					throw new Exception("step " + i + " uses a 'timed' completion event with an invalid 'time' value '" + time + "'; it must be a number of seconds", e);
				}
			}
		}
	}

}
