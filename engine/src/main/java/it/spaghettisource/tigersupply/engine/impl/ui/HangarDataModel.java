package it.spaghettisource.tigersupply.engine.impl.ui;

import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.impl.entity.Player;
import it.spaghettisource.tigersupply.engine.impl.weapon.Weapon;
import it.spaghettisource.tigersupply.engine.sprite.Sprite;


/**
 * this class is used by the user interfaces of the model to pass data 
 * it is a common model for all the user interfaces
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class HangarDataModel {

	private Sprite spriteInfo;		//sprite to use in the description area listener
	private String descriptionInfo;	//string to use in the description area listener	
	
	
	private Sprite ship;	//sprite to use as sheep
	private Speed speed;	//speed of the player
	
	private Weapon<Player> primaryWeapon;		//primary weapon
	private Weapon<Player> secondaryWeapon;	//secondary weapon	
	
	
	public Sprite getShip() {
		return ship;
	}
	public void setShip(Sprite ship) {
		this.ship = ship;
	}
	public Speed getSpeed() {
		return speed;
	}
	public void setSpeed(Speed speed) {
		this.speed = speed;
	}
	public Weapon<Player> getPrimaryWeapon() {
		return primaryWeapon;
	}
	public void setPrimaryWeapon(Weapon<Player> primaryWeapon) {
		this.primaryWeapon = primaryWeapon;
	}
	public Weapon<Player> getSecondaryWeapon() {
		return secondaryWeapon;
	}
	public void setSecondaryWeapon(Weapon<Player> secondaryWeapon) {
		this.secondaryWeapon = secondaryWeapon;
	}
	public Sprite getSpriteInfo() {
		return spriteInfo;
	}
	public void setSpriteInfo(Sprite spriteInfo) {
		this.spriteInfo = spriteInfo;
	}
	public String getDescriptionInfo() {
		return descriptionInfo;
	}
	public void setDescriptionInfo(String descriptionInfo) {
		this.descriptionInfo = descriptionInfo;
	}	

}
