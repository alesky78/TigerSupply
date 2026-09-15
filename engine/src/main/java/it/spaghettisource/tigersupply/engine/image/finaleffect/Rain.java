package it.spaghettisource.tigersupply.engine.image.finaleffect;



import java.awt.Graphics2D;

import it.spaghettisource.tigersupply.engine.control.GameContext;
import it.spaghettisource.tigersupply.engine.entity.EntityGroupScreenBound;

/**
 * Particle final effect that spawns falling {@link RainEntity} streaks from the
 * top of the screen, following the same spawner model as {@link Star}.
 *
 * @author Alessandro D'Ottavio
 */
public class Rain extends AbstractFinalEffect {

	private int screenWidth;
	private int screenHeight;
	private float secondNextDrop;
	private float periodCounter;


	private EntityGroupScreenBound<RainEntity> manager = new EntityGroupScreenBound<RainEntity>();


	public void configAndStart(float secondNextDrop, GameContext context){
		active = true;
		periodCounter = 0;

		manager.init(context);

		this.screenHeight = context.getScreenHeight();
		this.screenWidth = context.getScreenWidth();
		this.secondNextDrop = secondNextDrop;
	}


	public void reset(){
		periodCounter = 0;
		active = false;
	}


	public void updateEffect(float deltaSeconds) throws Exception {

		periodCounter += deltaSeconds;

		if(periodCounter>=secondNextDrop){
			manager.addEntityToBeManaged(new RainEntity(screenWidth, screenHeight));
			periodCounter = 0;
		}

		manager.updateEntity(deltaSeconds);
	}


	public void renderEffect(Graphics2D dbg, int screenWidth, int screenHeight) throws Exception {
		manager.renderEntity(dbg);
	}

}
