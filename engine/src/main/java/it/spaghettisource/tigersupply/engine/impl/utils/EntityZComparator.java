package it.spaghettisource.tigersupply.engine.impl.utils;




import java.util.Comparator;

import it.spaghettisource.tigersupply.engine.entity.Entity;

/**
 * sprite comparato by z coordinate
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class EntityZComparator implements Comparator<Entity> {

	public int compare(Entity o1, Entity o2) {
		return o2.getPosition().getPosZ()-o1.getPosition().getPosZ();
	}

}
