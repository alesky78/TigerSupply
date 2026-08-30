package it.spaghettisource.tigersupply.engine.control;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import javax.swing.JPanel;

/**
 * Base {@link Scene} implementation that renders through a Swing {@link JPanel} using a
 * double-buffered off-screen image.
 *
 * <p>It implements the {@link #render()} / {@link #paintScreen()} plumbing and delegates the
 * scene-specific drawing to the {@link #internalRender(Graphics2D)} and
 * {@link #doFinalEffect(Graphics2D)} template methods.
 *
 * @author Alessandro D'Ottavio
 *
 */
public abstract class AbstractScene implements Scene {

	
	// off screen rendering
	protected Graphics2D dbg = null; 
	protected Image dbImage = null;	
	protected JPanel gamePanel = null;
	protected int pWidth,pHeight;	//size of the panel
	
	
	/**
	 * Set the Swing panel this scene renders into.
	 *
	 * @param gamePanel the hosting panel, never {@code null}
	 */
	public void setGamePanel(JPanel gamePanel){
		this.gamePanel = gamePanel;
	}
	
	
	/**
	 * Lazily creates the off-screen buffer and draws the scene into it by invoking the
	 * {@link #internalRender(Graphics2D)} and {@link #doFinalEffect(Graphics2D)} template methods.
	 *
	 * @throws Exception if rendering fails
	 */
	public void render() throws Exception {

		if (dbImage == null){
			dbImage = gamePanel.createImage(pWidth, pHeight);
			if (dbImage == null) {
				System.out.println("dbImage is null");
				return;
			}
			else
				dbg = (Graphics2D) dbImage.getGraphics();
		}
		
		internalRender(dbg);
		
		doFinalEffect(dbg);
	
	}	
	
	/**
	 * Scene-specific rendering: draw all the entities of the scene.
	 * 
	 * @param dbg the off-screen graphics to draw into
	 * @throws Exception if rendering fails
	 */
	public abstract void internalRender(Graphics2D dbg) throws Exception;

	/**
	 * Scene-specific post-processing: render any final effect over the already drawn image
	 * (for example a screen transition or overlay).
	 *
	 * @param dbg the off-screen graphics to draw into
	 * @throws Exception if the effect rendering fails
	 */
	public abstract void doFinalEffect(Graphics2D dbg) throws Exception;
	
	
	/**
	 * Blits the off-screen buffer onto the panel's on-screen graphics, applying antialiasing and
	 * syncing the toolkit. Graphics errors (common at teardown) are swallowed.
	 */
	public void paintScreen() {
		Graphics2D g;
		try {
			g = (Graphics2D) gamePanel.getGraphics();

			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

			if ((g != null) && (dbImage != null)){
				
				g.drawImage(dbImage, 0, 0,pWidth,pHeight, null);
			}

		
			// Sync the display on some systems.
			// (on Linux, this fixes event queue problems)
			Toolkit.getDefaultToolkit().sync();
			g.dispose();
		}
		catch (Exception e){   // quite commonly seen at applet destruction
			System.out.println("Graphics error: " + e);  
		}
	}
	
}
