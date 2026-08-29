package it.spaghettisource.tigersupply.game.weapon;

import it.spaghettisource.tigersupply.engine.sprite.Sprite;

/**
 * this interface is implemented from the weapon that are selected in the hangar
 * for the selection of the weapon see {@link HangarScene}
 * 
 * @author Alessandro D'Ottavio
 *
 */
public interface HangarWeapon {

	public Sprite getSprite() throws Exception;
	
	public String getDescription();	
	
}
