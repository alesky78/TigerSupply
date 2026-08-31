package it.spaghettisource.tigersupply.engine.background;

import java.awt.Graphics2D;
import java.util.ArrayList;


/**
 * Composite background that layers several {@link BackGround} instances to produce a
 * parallax effect.
 *
 * <p>The layers are stored in insertion order and are both updated and rendered in
 * that same order, so the first background added is drawn first (at the back) and the
 * last one is drawn on top. Giving each layer a different scroll speed creates the
 * illusion of depth. As it implements {@link BackGround}, a {@code ParallaxBackGround}
 * can itself be nested inside another one.
 *
 * @author Alessandro D'Ottavio
 *
 */
public class ParallaxBackGround implements BackGround {

	ArrayList<BackGround> backGrounds = new ArrayList<BackGround>();
	
	/**
	 * Appends a background layer on top of the existing ones.
	 *
	 * <p>The layer is added last, so it is rendered above the layers added before it.
	 *
	 * @param backGround the layer to add; must not be {@code null}
	 */
	public void addBackGround(BackGround backGround){
		backGrounds.add(backGround);
	}
	
	
	/**
	 * {@inheritDoc}
	 *
	 * <p>Forwards the update to every layer, in insertion order.
	 */
	public void updateBackground(float deltaSeconds) {
		for (BackGround backGround : backGrounds) {
			backGround.updateBackground(deltaSeconds);
		}
		
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Renders every layer in insertion order, so earlier layers are painted behind
	 * later ones.
	 */
	public void renderBackground(Graphics2D dbg) {
		for (BackGround backGround : backGrounds) {
			backGround.renderBackground(dbg);
		}
		
	}

}
