package it.spaghettisource.tigersupply.game.ui;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;

import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Size;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.sprite.ImagePlayerCenterControllerSprite;
import it.spaghettisource.tigersupply.engine.ui.RectangleButton;

public class ShipButtonHangar extends RectangleButton{


	private int cx,cy;
	private Position position;
	private Size size;	
	private HangarDataModel model;

	private ImagePlayerCenterControllerSprite sprite;
	private Speed speed;

	private boolean mouseIn = false;

	public ShipButtonHangar(int x, int y, int w, int h, Paint paint,ImagePlayerCenterControllerSprite sprite,Speed speed,HangarDataModel model) {
		super(x, y, w, h, paint);
		this.sprite = sprite;
		this.speed = speed;
		this.model = model;

		cx = x+w/2;
		cy = y+h/2;
		position = new Position(cx, cy, 0);
		size = new Size(sprite.getImageWidth(), sprite.getImageHeight());

	}

	public void execute() {
		model.setSpeed(speed);
		model.setShip(sprite);
	}

	protected void doMouseOver(int x, int y) {
		if(y<cy-sprite.getImageHeight()/2){
			sprite.goToUpAnimation();
		}else if(y>cy+sprite.getImageHeight()/2){
			sprite.goToDownAnimation();
		}else{
			sprite.goToCentralAnimation();
		}

		model.setSpriteInfo(sprite);
		model.setDescriptionInfo("ship");		
		mouseIn = true;
	}


	protected void doMouseOut() {
		sprite.goToCentralAnimation();
		mouseIn = false; 
	}	


	public void update(float deltaTimeSeconds) throws Exception {
		sprite.updateSprite(deltaTimeSeconds);
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
