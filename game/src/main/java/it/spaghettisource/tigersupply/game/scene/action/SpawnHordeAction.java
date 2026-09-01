package it.spaghettisource.tigersupply.game.scene.action;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.spaghettisource.tigersupply.engine.entity.EntityFactory;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithm;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithmFactory;
import it.spaghettisource.tigersupply.engine.sprite.Sprite;
import it.spaghettisource.tigersupply.engine.sprite.SpriteFactory;
import it.spaghettisource.tigersupply.engine.utils.DynaProperties;
import it.spaghettisource.tigersupply.game.entity.Enemy;
import it.spaghettisource.tigersupply.game.scene.builder.LevelDataRepository;
import it.spaghettisource.tigersupply.game.scene.builder.definition.ActionDefinition;
import it.spaghettisource.tigersupply.game.scene.builder.definition.AlgorithmPrototype;
import it.spaghettisource.tigersupply.game.scene.builder.definition.EnemyDefinition;
import it.spaghettisource.tigersupply.game.scene.builder.definition.EnemyPrototype;
import it.spaghettisource.tigersupply.game.scene.builder.definition.Image;
import it.spaghettisource.tigersupply.game.scene.builder.definition.PointDefinition;
import it.spaghettisource.tigersupply.game.scene.builder.definition.Scale;
import it.spaghettisource.tigersupply.game.scene.builder.definition.Speed;
import it.spaghettisource.tigersupply.game.scene.director.DirectorContext;

/**
 * Spawns the wave of enemies declared inside a {@code spawnHorde} action. It instantiates each
 * {@link EnemyDefinition} into a live {@link Enemy} (resolving its prototype, sprite, and movement
 * algorithm through the shared context) and registers the batch with the enemy manager. This is the
 * enemy-instantiation logic formerly hosted by {@code HordeSpawner}, now packaged as a level action.
 *
 * @author Alessandro D'Ottavio
 */
public class SpawnHordeAction implements LevelAction {

	private List<EnemyDefinition> enemies;

	@Override
	public void init(ActionDefinition definition) throws Exception {
		this.enemies = definition.getEnemies();
	}

	@Override
	public void execute(DirectorContext context) throws Exception {
		List<Enemy> created = createEnemies(context);
		context.getEnemyManager().addRequest(created);
	}

	/**
	 * Creates all enemy instances declared by this action.
	 *
	 * <p>For each enemy definition it retrieves the enemy and algorithm prototypes from the level data,
	 * builds a sprite and an entity through the engine factories, and injects the scene-level
	 * dependencies (effects, shots, enemy manager, target, context) taken from the shared context.</p>
	 *
	 * @param context the shared director context providing prototypes and subsystem references
	 * @return the fully-initialized enemies ready to register with the enemy manager
	 * @throws Exception if sprite/entity creation fails or prototypes are missing
	 */
	private List<Enemy> createEnemies(DirectorContext context) throws Exception {

		List<Enemy> entities = new ArrayList<Enemy>();
		LevelDataRepository levelData = context.getLevelData();

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

		for (EnemyDefinition enemy : enemies) {
			enemyDef = levelData.getEnemyPrototypeByName(enemy.getEnemyPrototype());
			algorithmDef = levelData.getAlgorithmPrototypeByName(enemy.getAlgorithmPrototype());

			if(enemyDef.getType().equals("imageSingleSprite")){
				speed = enemyDef.getSpeed();
				image = enemyDef.getImage();
				scale = enemyDef.getScale();
				algorithm = createUpdateAlgorithm(algorithmDef);

				sprite = spriteFactory.createImageSingleSprite(image.getAlias());
				entity = entityFactory.createEntity(enemy.getPosX(), enemy.getPosY(), enemy.getPosZ(), speed.getX(), speed.getY(), scale.getScale(), algorithm, sprite, enemyDef.getClassName());
			}

			entity.setEffectManager(context.getEffectManager());
			entity.setShotManager(context.getShotManager());
			entity.setEnemyManager(context.getEnemyManager());
			entity.setTarget(context.getPlayer());
			entity.setContext(context.getContext());

			entities.add(entity);
		}

		return entities;
	}

	/**
	 * Instantiates an {@link UpdateAlgorithm} from an algorithm prototype, converting its single and
	 * list properties into a {@link DynaProperties} bean before creating the algorithm by reflection.
	 *
	 * @param algorithmDef the prototype defining the algorithm class and its configuration
	 * @return an initialized movement algorithm
	 * @throws Exception if the algorithm class cannot be instantiated or configured
	 */
	private UpdateAlgorithm createUpdateAlgorithm(AlgorithmPrototype algorithmDef) throws Exception {

		DynaProperties prop = new DynaProperties(algorithmDef.getProperties().getSingleProperties());

		HashMap<String, List<PointDefinition>> map = algorithmDef.getProperties().getListProperties();
		List<Point> list;
		for (String key : map.keySet()) {
			list = new ArrayList<Point>();
			for (PointDefinition pointDef : map.get(key)) {
				list.add(new Point(pointDef.getX(), pointDef.getY()));
			}
			prop.setList(key, list);
		}

		try {
			return UpdateAlgorithmFactory.newInstance(algorithmDef.getClassName(), prop);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
