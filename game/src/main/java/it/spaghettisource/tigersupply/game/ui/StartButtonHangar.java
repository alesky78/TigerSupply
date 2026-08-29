package it.spaghettisource.tigersupply.game.ui;



import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.font.GlyphVector;

import it.spaghettisource.tigersupply.engine.font.repository.FontRepositoryManager;
import it.spaghettisource.tigersupply.game.control.GameFlowController;
import it.spaghettisource.tigersupply.game.entity.Player;
import it.spaghettisource.tigersupply.engine.ui.RectangleButton;
import it.spaghettisource.tigersupply.game.utils.GameResources;

public class StartButtonHangar extends RectangleButton{
	
	private int cx,cy;	
	private HangarDataModel model;
	private Player player;
	
	private boolean mouseIn = false;

	public StartButtonHangar(int x, int y, int w, int h, Paint paint,HangarDataModel model,Player player) {
		super(x, y, w, h, paint); 
		this.model = model;
		this.player = player;
		
		cx = x+w/2;
		cy = y+h/2;

	}

	public void execute() {
		player.setSprite(model.getShip());
		player.setSpeed(model.getSpeed());
		player.addWeapon(model.getPrimaryWeapon());
		player.addWeapon(model.getSecondaryWeapon());		
		try {
			GameFlowController.getInstance().doNextLevel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doMouseOver(int x, int y) {
		mouseIn = true;
	}


	protected void doMouseOut() {
		mouseIn = false; 
	}	


	public void update(float deltaTimeSeconds) throws Exception {
	}	

	public void render(Graphics2D dbg) throws Exception {
		super.render(dbg);

		
		Font font = FontRepositoryManager.getInstance().getFont(GameResources.FONT_TECHNO, 20);
		GlyphVector gv = font.createGlyphVector(dbg.getFontRenderContext(),"START");

		dbg.setPaint(Color.ORANGE);
		dbg.drawGlyphVector(gv, cx-33, cy+4);
		
		
		if(mouseIn){	//draw the border when the mouse is insidet
			original = dbg.getPaint();
			dbg.setPaint(Color.ORANGE);
			dbg.drawRoundRect(x, y, w, h, 15, 15);
			dbg.setPaint(original);			
		}
	}	



}
