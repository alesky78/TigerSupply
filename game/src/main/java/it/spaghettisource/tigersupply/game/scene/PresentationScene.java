package it.spaghettisource.tigersupply.game.scene;

import it.spaghettisource.tigersupply.engine.background.BackGround;
import it.spaghettisource.tigersupply.engine.control.AbstractSceneJPanel;
import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.font.repository.FontRepositoryManager;
import it.spaghettisource.tigersupply.engine.image.finaleffect.FinalEffectManager;
import it.spaghettisource.tigersupply.engine.image.repository.ImageRepositoryManager;
import it.spaghettisource.tigersupply.game.control.SceneFlowController;
import it.spaghettisource.tigersupply.game.utils.GameResources;
import it.spaghettisource.tigersupply.engine.background.BackGroundFitImage;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;

public class PresentationScene extends AbstractSceneJPanel {

	private GameContext context;
	private BackGround backGround;	
	private FinalEffectManager finalEffectManager; 
	
	public PresentationScene(GameContext context) throws Exception{
		this.context = context;
		this.pWidth = context.getScreenWidth();
		this.pHeight = context.getScreenHeight();
				
		backGround = new BackGroundFitImage(ImageRepositoryManager.getInstance().getSingleImage(GameResources.BCKGROUND_PLANET), 0, pWidth, pHeight, false);
		
		finalEffectManager = FinalEffectManager.getInstance();
		
		//activate the stars
		finalEffectManager.activateStar(0.1f);
		
	}
	
	
	public void update(float deltaTimeSeconds) throws Exception{
		finalEffectManager.updateEffect(deltaTimeSeconds);
	}


	public void internalRender(Graphics2D dbg) throws Exception {
		
		Paint original = dbg.getPaint();
		dbg.setPaint(new GradientPaint(pWidth/2, pHeight/2, new Color(0, 0, 0), pWidth, pHeight, new Color(0, 0, 155)));
		dbg.fillRect(0, 0, pWidth, pHeight);		
		dbg.setPaint(original);

	    backGround.renderBackground(dbg);
	    	    
		dbg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		
		Font font = FontRepositoryManager.getInstance().getFont(GameResources.FONT_TECHNO, 50);
		
	    dbg.setFont(font);
		dbg.setColor(new Color(255, 0, 0));
		
		GlyphVector gv = font.createGlyphVector(dbg.getFontRenderContext(),"Tiger Supply");
		
	    AffineTransform shadowTransform = AffineTransform.getShearInstance(-1.0, 0.0); // Shear to the right
	    shadowTransform.scale(1.0, 0.5); // Scale height by 1/2
		
	    
	    for (int i = 0; i < 12; i++) {
	    	dbg.translate(300+i*25, pHeight/2);
		    dbg.fill(shadowTransform.createTransformedShape(gv.getGlyphOutline(i)));
		    dbg.fill(gv.getGlyphOutline(i));
		    dbg.translate(-300-i*25, -pHeight/2);	
		}
	    
		
		dbg.drawString("press fire to start", pWidth/2-350, pHeight/2+100);
		
	}

	public void doFinalEffect(Graphics2D dbg) throws Exception {
		finalEffectManager.renderEffect(dbg);
		if(finalEffectManager.isDarknessActive() && finalEffectManager.isDarknessFinish()){	//render and stop for the last time
			finalEffectManager.stopDarkness();
			finalEffectManager.stopStar();
			SceneFlowController.getInstance().doHangar();
		}
	}	

	public void keyPressed(KeyEvent event) {
		if ((event.getKeyCode() == KeyEvent.VK_SPACE) ) {
			if(!finalEffectManager.isDarknessActive()){
				finalEffectManager.activateDarkness(2);
			}
				
		}
	}

	public void keyReleased(KeyEvent event){}

	public void mousePressed(int x, int y) {}

	public void mouseMoved(MouseEvent event) {}	

	
}
