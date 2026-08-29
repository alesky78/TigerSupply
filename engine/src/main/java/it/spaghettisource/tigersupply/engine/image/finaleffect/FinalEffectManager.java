package it.spaghettisource.tigersupply.engine.image.finaleffect;



import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

import it.spaghettisource.tigersupply.engine.control.ApplicationContext;


/**
 * the final effect manager is used to manage the {@link FinalEffect} 
 * for example screen darkness or lightness, rain etc...
 * 
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class FinalEffectManager {

	private static FinalEffectManager instance;
	private ApplicationContext context;	

	Map<String,FinalEffect> registeredEffect; 
	
	private FinalEffectManager(ApplicationContext context){
		this.context = context;
		
		//register hire the effect to use
		registeredEffect = new HashMap<String, FinalEffect>();
		registeredEffect.put("darkness", new Darkness());
		registeredEffect.put("star", new Star());		
	}
	
	public static void init(ApplicationContext context) throws Exception{
		if(instance==null){
			synchronized (FinalEffectManager.class) {
				if(instance==null){
					instance = new FinalEffectManager(context);
				}
			}
		}
	}	
	
	public static FinalEffectManager getInstance() throws Exception{
		if(instance==null){
			Exception ex = new Exception("FinalEffectManager class must by initialized before to use it");
			throw ex;
		}
		return instance;
	}	


	public void updateEffect(float deltaSeconds) throws Exception{
		for (FinalEffect effect : registeredEffect.values()) {
			if(effect.isActive()){
				effect.updateEffect(deltaSeconds);
			}
		}
		
	}
	
	
	public void renderEffect(Graphics2D dbg) throws Exception{
		for (FinalEffect effect : registeredEffect.values()) {
			if(effect.isActive()){
				effect.renderEffect(dbg,context.getScreenWidth(),context.getScreenHeight());
			}
		}
	}
	
	
	public boolean allEffectCompleted(){
		for (FinalEffect effect : registeredEffect.values()) {
			if(effect.isActive()){
				return false;
			}
		}
		return true;
	}
	
	
	public void activateDarkness(float secondToDark){
		Darkness efx = (Darkness) registeredEffect.get("darkness");
		efx.configAndStart(secondToDark, context.getPeriodSeconds());
	}
	
	public void stopDarkness(){
		Darkness efx = (Darkness) registeredEffect.get("darkness");
		efx.reset();
	}	
	
	public boolean isDarknessActive(){
		return registeredEffect.get("darkness").isActive();
	}
	
	public boolean isDarknessFinish(){
		Darkness efx = (Darkness) registeredEffect.get("darkness");
		return efx.isFinish();
	}	
	
	public void activateStar(float nextStarFrequency){
		Star efx = (Star) registeredEffect.get("star");
		efx.configAndStart(nextStarFrequency, context);
	}	

	public void stopStar(){
		Star efx = (Star) registeredEffect.get("star");
		efx.reset();
	}	
	
	public boolean isStarActive(){
		return registeredEffect.get("star").isActive();
	}
	
}
