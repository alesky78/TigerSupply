package it.spaghettisource.tigersupply.engine.impl.scene;

import it.spaghettisource.tigersupply.engine.background.BackGround;
import it.spaghettisource.tigersupply.engine.control.AbstractGameJPanel;
import it.spaghettisource.tigersupply.engine.control.ApplicationContext;
import it.spaghettisource.tigersupply.engine.font.repository.FontRepositoryManager;
import it.spaghettisource.tigersupply.engine.image.finaleffect.FinalEffectManager;
import it.spaghettisource.tigersupply.engine.image.repository.ImageRepositoryManager;
import it.spaghettisource.tigersupply.engine.impl.control.GameFlowController;
import it.spaghettisource.tigersupply.engine.utils.StaticResources;
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

public class PresentationScene extends AbstractGameJPanel {

	private ApplicationContext context;
	private BackGround backGround;	
	private FinalEffectManager finalEffectManager; 
	
	public PresentationScene(ApplicationContext context) throws Exception{
		this.context = context;
		this.pWidth = context.getScreenWidth();
		this.pHeight = context.getScreenHeight();
				
		backGround = new BackGroundFitImage(ImageRepositoryManager.getInstance().getSingleImage(StaticResources.BCKGROUND_PLANET), 0, pWidth, pHeight, false);
		
		finalEffectManager = FinalEffectManager.getInstance();
		
		//activate the stars
		finalEffectManager.activateStar(0.1f);
		
	}
	
	
	public void updateGame(float deltaTimeSeconds) throws Exception{
		finalEffectManager.updateEffect(deltaTimeSeconds);
	}


	public void internalRenderGame(Graphics2D dbg) throws Exception {
		
		Paint original = dbg.getPaint();
		dbg.setPaint(new GradientPaint(pWidth/2, pHeight/2, new Color(0, 0, 0), pWidth, pHeight, new Color(0, 0, 155)));
		dbg.fillRect(0, 0, pWidth, pHeight);		
		dbg.setPaint(original);

	    backGround.renderBackground(dbg);
	    	    
		dbg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		
		Font font = FontRepositoryManager.getInstance().getFont(StaticResources.FONT_TECHNO, 50);
		
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
			GameFlowController.getInstance().doHangar();
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

	public void mousePress(int x, int y) {}

	public void mouseMove(MouseEvent event) {}	

	
}
