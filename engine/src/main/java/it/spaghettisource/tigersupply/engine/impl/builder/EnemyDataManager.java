package it.spaghettisource.tigersupply.engine.impl.builder;


import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.spaghettisource.tigersupply.engine.control.ApplicationContext;
import it.spaghettisource.tigersupply.engine.entity.Entity;
import it.spaghettisource.tigersupply.engine.entity.EntityFactory;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithm;
import it.spaghettisource.tigersupply.engine.entity.logic.UpdateAlgorithmFactory;
import it.spaghettisource.tigersupply.engine.entity.manager.EntityManagerEntityRequest;
import it.spaghettisource.tigersupply.engine.impl.entity.Enemy;
import it.spaghettisource.tigersupply.engine.impl.scene.definition.AlgorithmPrototype;
import it.spaghettisource.tigersupply.engine.impl.scene.definition.EnemyDefinition;
import it.spaghettisource.tigersupply.engine.impl.scene.definition.EnemyPrototype;
import it.spaghettisource.tigersupply.engine.impl.scene.definition.GenerateEvent;
import it.spaghettisource.tigersupply.engine.impl.scene.definition.Horde;
import it.spaghettisource.tigersupply.engine.impl.scene.definition.Image;
import it.spaghettisource.tigersupply.engine.impl.scene.definition.LevelDataRepository;
import it.spaghettisource.tigersupply.engine.impl.scene.definition.PointDefinition;
import it.spaghettisource.tigersupply.engine.impl.scene.definition.Scale;
import it.spaghettisource.tigersupply.engine.impl.scene.definition.Speed;
import it.spaghettisource.tigersupply.engine.sprite.Sprite;
import it.spaghettisource.tigersupply.engine.sprite.SpriteFactory;
import it.spaghettisource.tigersupply.engine.statemachine.Event;
import it.spaghettisource.tigersupply.engine.utils.DynaProperties;


/**
 * this class has the duty to create the correct builder to create the repository of data
 * and use the data in the repository to create and return new sprite when required
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class EnemyDataManager {

	private ApplicationContext context;
	
	private EnemyDataBuilder builder;
	private LevelDataRepository lvlData;
	private int hordeIndex;

	protected Entity player;
	protected EntityManagerEntityRequest<Entity> shotManager;
	protected EntityManagerEntityRequest<Entity> effectManager;	
	protected EntityManagerEntityRequest<Enemy> enemyManager;	

	
	private boolean bossKilled;
	
	public EnemyDataManager(String levelFile){
		builder = new EnemyDataBuilderSaxXml(levelFile);	//substitute hire the builder if want, should be valorized as set if create IOC
		lvlData = new LevelDataRepository();
		hordeIndex = 0;
		bossKilled = false;
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
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

	public void setEnemyManager(EntityManagerEntityRequest<Enemy> enemyManager) {
		this.enemyManager = enemyManager;
	}

	public boolean isEnemyManagerEmpty(){
		return !enemyManager.hasEntities();
	}	
	
	
	public boolean isBossDeath() {
		return bossKilled;
	}
	
	public void bossKilled() {
		bossKilled = true;
	}	
	
	public void loadEnemyData() throws Exception{

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
	 * create the actual horde in the configuration and 
	 * return the event associate to this horde
	 * finally advance to the next horde
	 * 
	 * @return
	 * @throws Exception 
	 */
	public Event manageActualHordeAndGenerateEvent() throws Exception{
		
		//generate the order and the event of this stage
		List<Enemy> horde = generateNewEnemyHorde();
		
		Event event = generateEvent();
		
		//manage the new enemy
		enemyManager.addRquest(horde);
		
		//advance to next horde
		goToNextHorde();
		
		return event;
		
	}
	
	private void goToNextHorde(){
		hordeIndex+=1;
	}	
	
	private Event generateEvent(){		
		GenerateEvent desc = lvlData.getEventByIndex(hordeIndex); 
		return new Event(desc.getName());
	}	

	private List<Enemy> generateNewEnemyHorde() throws Exception{

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
				algorithmDef = lvlData.getAlgorithmPrototypeByName(enemy.getAlgoritmPrototype());

				if(enemyDef.getType().equals("imageSingleSprite")){
					speed = enemyDef.getSpeed();
					image = enemyDef.getImage();
					scale = enemyDef.getScale();
					algorithm = buildAlgorithm(algorithmDef);
					
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


	private UpdateAlgorithm buildAlgorithm(AlgorithmPrototype algorithmDef) throws Exception{
		
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
