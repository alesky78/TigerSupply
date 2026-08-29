package it.spaghettisource.tigersupply.engine.impl.ui;

import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Size;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.impl.entity.Player;
import it.spaghettisource.tigersupply.engine.impl.weapon.HangarWeapon;
import it.spaghettisource.tigersupply.engine.impl.weapon.Weapon;
import it.spaghettisource.tigersupply.engine.sprite.Sprite;
import it.spaghettisource.tigersupply.engine.ui.RectangleButton;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;

public class WeaponButtonHangar extends RectangleButton{

	public static final int PRIMARY_WEAPON = 0;
	public static final int SECONDARY_WEAPON = 1;	
	
	
	
	private int cx,cy;
	private Position position;
	private Size size;	
	private Sprite sprite;
	
	private Weapon<Player> weapon;
	private HangarDataModel model;
	private int weaponMode;

	private boolean mouseIn = false;

	public WeaponButtonHangar(int x, int y, int w, int h, Paint paint,Weapon<Player> weapon,HangarDataModel model,int weaponMode) {
		super(x, y, w, h, paint);
		this.weapon = weapon; 
		this.model = model;
		this.weaponMode = weaponMode;
		cx = x+w/2;
		cy = y+h/2;
		position = new Position(cx, cy, 0);
		try {
			sprite = ((HangarWeapon)weapon).getSprite();
		} catch (Exception e) {
			e.printStackTrace();
		}
		size = new Size(sprite.getImageWidth(), sprite.getImageHeight());

	}

	public void execute() {
		if(weaponMode==PRIMARY_WEAPON){
			model.setPrimaryWeapon(weapon);
		}else if(weaponMode==SECONDARY_WEAPON){
			model.setSecondaryWeapon(weapon);
		}
			
			
	}

	protected void doMouseOver(int x, int y) {
		mouseIn = true;
		model.setSpriteInfo(sprite);
		model.setDescriptionInfo(((HangarWeapon)weapon).getDescription());
	}


	protected void doMouseOut() {
		mouseIn = false; 
	}	


	public void update(float deltaTimeSeconds) throws Exception {
	}	

	public void render(Graphics2D dbg) throws Exception {
		super.render(dbg);
		sprite.renderSprite(dbg, position, size);

		if(mouseIn){	//draw the border when the mouse is insidet
			original = dbg.getPaint();
			dbg.setPaint(Color.ORANGE);
			dbg.drawRoundRect(x, y, w, h, 15, 15);
			dbg.setPaint(original);			
		}
	}	



}
