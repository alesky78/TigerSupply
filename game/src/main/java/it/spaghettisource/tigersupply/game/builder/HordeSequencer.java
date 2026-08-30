package it.spaghettisource.tigersupply.game.builder;


import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.EntityFactory;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithm;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithmFactory;
import it.spaghettisource.tigersupply.engine.entity.EntityGroupScreenBound;
import it.spaghettisource.tigersupply.game.entity.Enemy;
import it.spaghettisource.tigersupply.game.scene.definition.AlgorithmPrototype;
import it.spaghettisource.tigersupply.game.scene.definition.EnemyDefinition;
import it.spaghettisource.tigersupply.game.scene.definition.EnemyPrototype;
import it.spaghettisource.tigersupply.game.scene.definition.GenerateEvent;
import it.spaghettisource.tigersupply.game.scene.definition.Horde;
import it.spaghettisource.tigersupply.game.scene.definition.Image;
import it.spaghettisource.tigersupply.game.scene.definition.LevelDataRepository;
import it.spaghettisource.tigersupply.game.scene.definition.PointDefinition;
import it.spaghettisource.tigersupply.game.scene.definition.Scale;
import it.spaghettisource.tigersupply.game.scene.definition.Speed;
import it.spaghettisource.tigersupply.engine.sprite.Sprite;
import it.spaghettisource.tigersupply.engine.sprite.SpriteFactory;
import it.spaghettisource.tigersupply.engine.statemachine.Event;
import it.spaghettisource.tigersupply.engine.utils.DynaProperties;


/**
 * Manages the creation and lifecycle of enemies for a level by parsing XML definitions and generating hordes on demand.
 *
 * <p>This class acts as the central coordinator for enemy spawning: it loads level data from an XML file using a builder,
 * maintains a repository of enemy prototypes and algorithm configurations, and creates actual enemy instances grouped
 * into hordes as the level progresses. Each horde is associated with a game event (e.g., "wave 1 complete").</p>
 *
 * <p>Clients must inject dependencies via setters ({@link #setContext(GameContext)}, {@link #setPlayer(Entity)},
 * {@link #setEnemyManager(EntityGroupScreenBound)}, etc.) before calling {@link #loadLevelData()} and
 * {@link #spawnNextHorde()}. Boss death is tracked separately via {@link #markBossAsKilled()} and
 * {@link #isBossDead()}.</p>
 *
 * @author Alessandro D'Ottavio
 */
public class HordeSequencer {

	private GameContext context;
	
	private EnemyDataBuilder builder;
	private LevelDataRepository lvlData;
	private int hordeIndex;

	protected Entity player;
	protected EntityGroupScreenBound<Entity> shotManager;
	protected EntityGroupScreenBound<Entity> effectManager;
	protected EntityGroupScreenBound<Enemy> enemyManager;

	
	private boolean bossKilled;
	
	/**
	 * Creates a new horde sequencer for the specified level file.
	 *
	 * <p>The sequencer will parse the XML level definition and prepare horde/enemy/algorithm prototypes
	 * for spawning on demand. Dependencies must be injected via setter methods before calling
	 * {@link #loadLevelData()} or {@link #spawnNextHorde()}.</p>
	 *
	 * @param levelFile the path to the XML level definition file
	 * @see #loadLevelData()
	 * @see #setContext(GameContext)
	 */
	public HordeSequencer(String levelFile){
		builder = new EnemyDataBuilderSaxXml(levelFile);	//substitute hire the builder if want, should be valorized as set if create IOC
		lvlData = new LevelDataRepository();
		hordeIndex = 0;
		bossKilled = false;
	}

	/**
	 * Injects the game context.
	 *
	 * @param context the {@link GameContext} providing access to game state and configuration
	 */
	public void setContext(GameContext context) {
		this.context = context;
	}

	/**
	 * Injects the player entity.
	 *
	 * @param player the player {@link Entity} that enemies will target
	 */
	public void setPlayer(Entity player) {
		this.player = player;
	}

	/**
	 * Injects the shot manager for spawned enemies to emit projectiles.
	 *
	 * @param shotManager the {@link EntityGroupScreenBound} managing player and enemy shots
	 */
	public void setShotManager(EntityGroupScreenBound<Entity> shotManager) {
		this.shotManager = shotManager;
	}

	/**
	 * Injects the effect manager for visual effects (explosions, etc).
	 *
	 * @param effectManager the {@link EntityGroupScreenBound} managing visual effects
	 */
	public void setEffectManager(EntityGroupScreenBound<Entity> effectManager) {
		this.effectManager = effectManager;
	}

	/**
	 * Injects the enemy manager for adding spawned enemies to the scene.
	 *
	 * @param enemyManager the {@link EntityGroupScreenBound} managing active enemies
	 */
	public void setEnemyManager(EntityGroupScreenBound<Enemy> enemyManager) {
		this.enemyManager = enemyManager;
	}

	/**
	 * Checks if there are no active enemies in the scene.
	 *
	 * @return {@code true} if the enemy manager contains no entities; {@code false} otherwise
	 */
	public boolean isEnemyManagerEmpty(){
		return !enemyManager.hasEntities();
	}	
	
	
	/**
	 * Checks if the boss of this level has been defeated.
	 *
	 * @return {@code true} if the boss has been killed; {@code false} otherwise
	 */
	public boolean isBossDead() {
		return bossKilled;
	}
	
	/**
	 * Marks the boss as defeated. This state is typically set when the boss entity is destroyed.
	 */
	public void markBossAsKilled() {
		bossKilled = true;
	}	
	
	/**
	 * Parses and loads the level XML file, populating the repository with horde, enemy, and algorithm definitions.
	 *
	 * <p>This method must be called exactly once before {@link #spawnNextHorde()} to initialize the sequencer
	 * with level data. It parses the XML file and builds three main artifacts:
	 * <ul>
	 * <li>Hordes: sequence of enemy waves and their timing events</li>
	 * <li>Enemy prototypes: reusable enemy type definitions (sprite, speed, scale, weapon class)</li>
	 * <li>Algorithm prototypes: reusable enemy movement algorithms with parameterized configurations</li>
	 * </ul></p>
	 *
	 * @throws Exception if the XML file cannot be parsed or required resources are missing
	 * @see #spawnNextHorde()
	 */
	public void loadLevelData() throws Exception{

		builder.parse();

		List<Horde> horde =  builder.buildHordes();
		List<EnemyPrototype> enemy =  builder.buildEnemyPrototypes();
		List<AlgorithmPrototype> algorithm =  builder.buildAlgorithmPrototypes();

		lvlData.setHordes(horde);
		lvlData.setAlgoithmPrototypes(algorithm);
		lvlData.setEnemyPrototypes(enemy);

		System.out.println(lvlData);	

	}

	/**
	 * Spawns the next horde of enemies and returns the associated game event.
	 *
	 * <p>This method orchestrates the complete horde spawning cycle:
	 * <ol>
	 * <li>Creates enemy instances from the current horde definition</li>
	 * <li>Generates and returns the {@link Event} associated with this horde (e.g., event trigger for next level phase)</li>
	 * <li>Registers all enemies with the enemy manager</li>
	 * <li>Advances to the next horde in sequence</li>
	 * </ol>
	 * Call this repeatedly throughout the level to spawn hordes in order.</p>
	 *
	 * @return the {@link Event} associated with the spawned horde (may trigger level state changes)
	 * @throws Exception if enemy instantiation fails or required prototypes are missing
	 * @see #loadLevelData()
	 */
	public Event spawnNextHorde() throws Exception{
		
		//generate the order and the event of this stage
		List<Enemy> horde = createHordeEnemies();
		
		Event event = createHordeEvent();
		
		//manage the new enemy
		enemyManager.addRquest(horde);
		
		//advance to next horde
		advanceHorde();
		
		return event;
		
	}
	
	/**
	 * Advances the horde sequence to the next horde.
	 */
	private void advanceHorde(){
		hordeIndex+=1;
	}	
	
	/**
	 * Creates the {@link Event} associated with the current horde.
	 *
	 * @return the event that will be triggered when this horde completes
	 */
	private Event createHordeEvent(){
		GenerateEvent desc = lvlData.getEventByIndex(hordeIndex); 
		return new Event(desc.getName());
	}	

	/**
	 * Creates all enemy instances defined in the current horde.
	 *
	 * <p>For each enemy definition in the current horde:
	 * <ol>
	 * <li>Retrieves the enemy prototype and movement algorithm prototype</li>
	 * <li>Creates a sprite using {@link SpriteFactory}</li>
	 * <li>Creates an entity using {@link EntityFactory} with the sprite, speed, scale, and algorithm</li>
	 * <li>Injects dependencies (effects, shots, enemies, target, context)</li>
	 * </ol>
	 * The returned list is ready to be registered with the enemy manager.</p>
	 *
	 * @return a list of fully-initialized {@link Enemy} instances for this horde
	 * @throws Exception if sprite/entity creation fails or prototypes are missing
	 */
	private List<Enemy> createHordeEnemies() throws Exception{

		List<Enemy> entities = new ArrayList<Enemy>();
		try{

			List<EnemyDefinition> enemies = lvlData.getHordeByIndex(hordeIndex).getEnemies();

			SpriteFactory spriteFactory = SpriteFactory.getInstance();
			EntityFactory entityFactory = EntityFactory.getInstance();			
			EnemyPrototype enemyDef;
			AlgorithmPrototype algorithmDef;
			Speed speed;
			Image image;
			Scale scale;

			Enemy entity = null;
			Sprite sprite = null;
			UpdateAlgorithm algorithm;
			
			//TODO qui usando il context si possono sovrascrivere i valori come con variabili
			for (EnemyDefinition enemy : enemies) {	
				enemyDef = lvlData.getEnemyPrototypeByName(enemy.getEnemyPrototype());
				algorithmDef = lvlData.getAlgorithmPrototypeByName(enemy.getAlgorithmPrototype());

				if(enemyDef.getType().equals("imageSingleSprite")){
					speed = enemyDef.getSpeed();
					image = enemyDef.getImage();
					scale = enemyDef.getScale();
					algorithm = createUpdateAlgorithm(algorithmDef);
					
					sprite = spriteFactory.createImageSingleSprite(image.getAlias());
					entity = entityFactory.createEntity(enemy.getPosX(), enemy.getPosY(),enemy.getPosZ(), speed.getX() , speed.getY(),scale.getScale(), algorithm, sprite, enemyDef.getClassName());
				}	

				entity.setEffectManager(effectManager);
				entity.setShotManager(shotManager);
				entity.setEnemyManager(enemyManager);			
				entity.setTarget(player);
				entity.setContext(context);

				entities.add(entity);

			}

		}catch (Exception e) {
			throw e;
		}

		return entities;
	}


	/**
	 * Instantiates an {@link UpdateAlgorithm} from an algorithm prototype.
	 *
	 * <p>Converts the prototype's configuration (single properties and list properties like waypoints)
	 * into a {@link DynaProperties} dynamic bean, then uses {@link UpdateAlgorithmFactory} to instantiate
	 * the algorithm class by reflection.</p>
	 *
	 * @param algorithmDef the prototype defining the algorithm class and its configuration
	 * @return an initialized {@link UpdateAlgorithm} instance ready for entity movement
	 * @throws Exception if the algorithm class cannot be instantiated or required properties are invalid
	 */
	private UpdateAlgorithm createUpdateAlgorithm(AlgorithmPrototype algorithmDef) throws Exception{
		
		//create a dyna bean configuration for the algorithm
		DynaProperties prop = new DynaProperties(algorithmDef.getProperties().getSingleProperties());
		
		//create the list of point properties if they exsists
		HashMap<String, List<PointDefinition>> map = algorithmDef.getProperties().getListProperties();
		List<Point> list;
		for (String key : map.keySet()) {
			list = new ArrayList<Point>();	//there is a list of point create and set to the dynamic bean
			for (PointDefinition pointDef : map.get(key)) {
				list.add(new Point(pointDef.getX(), pointDef.getY()));
			}
			prop.setList(key, list);
		}
		
		//now create the algorithm and return
		try {
			return UpdateAlgorithmFactory.newInstance(algorithmDef.getClassName(), prop);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}


	
}
