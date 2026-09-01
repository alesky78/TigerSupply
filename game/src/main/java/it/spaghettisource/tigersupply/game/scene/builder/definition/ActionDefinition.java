package it.spaghettisource.tigersupply.game.scene.builder.definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parsed definition of a single action inside a {@link Step}: its {@code type} discriminator (which
 * concrete {@code LevelAction} to build) plus its data. A {@code spawnHorde} action carries a nested
 * list of {@link EnemyDefinition}s; any other action carries a bag of string properties read from the
 * action's XML attributes. This is the open extension point of the step model.
 *
 * @author Alessandro D'Ottavio
 */
public class ActionDefinition {

	private String type;
	private List<EnemyDefinition> enemies;
	private Map<String, String> properties;

	public ActionDefinition(String type) {
		this.type = type;
		this.enemies = new ArrayList<EnemyDefinition>();
		this.properties = new HashMap<String, String>();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<EnemyDefinition> getEnemies() {
		return enemies;
	}

	public void addEnemy(EnemyDefinition enemy) {
		enemies.add(enemy);
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public String getProperty(String name) {
		return properties.get(name);
	}

	public void setProperty(String name, String value) {
		properties.put(name, value);
	}

	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("action-> type:"+type);
		for(EnemyDefinition enemy : enemies){
			buffer.append("\n  "+enemy);
		}
		if(!properties.isEmpty()){
			buffer.append(" props:"+properties);
		}
		return buffer.toString();
	}

}
