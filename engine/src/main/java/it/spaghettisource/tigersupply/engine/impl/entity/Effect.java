package it.spaghettisource.tigersupply.engine.impl.entity;


/**
 * generic implementation of an effect
 * the particular of an effect is that it can have a  specific time life cycle
 * managing the variable spriteTimeDuration
 * 
 * @author DOttavio
 *
 */
public class Effect extends BaseEntity {

	protected float spriteTimeDuration = 1;	//if set to -1 it is not removed based by time
	private float spriteCounter = 0; 		

	public void updateEntity(float deltaSeconds)  throws Exception  {
		super.updateEntity(deltaSeconds);

		if(spriteTimeDuration >-1){
			spriteCounter+=deltaSeconds;
			if(spriteCounter>=spriteTimeDuration ){
				remove = true;
			}
		}

	}		

}
