package it.spaghettisource.tigersupply.game.scene.builder.definition;

import java.util.ArrayList;
import java.util.List;

public class Horde {

	private List<EnemyDefinition> enemies;
	private GenerateEvent event;
	
	public Horde() {
		enemies = new ArrayList<EnemyDefinition>();
	}

	public Horde(List<EnemyDefinition> enemies, GenerateEvent event) {
		this.enemies = enemies;
		this.event = event;
	}

	public List<EnemyDefinition> getEnemies() {
		return enemies;
	}

	public void setEnemies(List<EnemyDefinition> enemies) {
		this.enemies = enemies;
	}
	
	public void addEnemy(EnemyDefinition enemy) {
		enemies.add(enemy);
	}	

	public GenerateEvent getEvent() {
		return event;
	}

	public void setEvent(GenerateEvent event) {
		this.event = event;
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("horde\n");
		for(EnemyDefinition enemy : enemies){
			buffer.append(enemy+"\n");
		}
		buffer.append(event+"\n");		
		
		return buffer.toString();
	}

	
}
