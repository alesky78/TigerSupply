package it.spaghettisource.tigersupply.engine.image;

import java.awt.image.BufferedImage;



/**
 * this image player is base on the concept of central position, it try to keep the image to show as the image defined as central image
 * the central image is the image at the position imCentralPosition in the array dbImages
 * 
 * the image player can be alterate and sey to him to draw the last image in the array dbImages (index array.size) or the find one (index 0)
 * use the methods goToCentralAnimation,goToUpAnimation,goToDownAnimation to control the animation
 * 
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class ImagesPlayerCenterControlled
{
	private boolean ticksIgnored;

	private double 	animPeriod; 			// period used by animation loop (in ms)
	private double 	animTotalTime;
	private double 	showPeriod;    			// period the current image is shown (in ms) 
	private int 	numImages;
	private int 	imActualPosition;     	// position of current displayable image
	private int 	imCentralPosition;     	// central position to manage	

	private BufferedImage[] dbImages;

	//animation direction
	private static final int CENTRAL_ANIMATION = 0;
	private static final int UP_ANIMATION = 1;
	private static final int DOWN_ANIMATION = 2;	
	private int animationDirection;

	/**
	 * 
	 * @param imageName
	 * @param animationPeriod in millisecond
	 * @param sequenceDuration in millisecond
	 * @param repeatImage
	 * @param il
	 */
	public ImagesPlayerCenterControlled(double animationPeriod, double sequenceDuration, int centralPosition, BufferedImage[] images) {
		animPeriod = animationPeriod; 
		animTotalTime = 0L;
		numImages = images.length;
		imActualPosition = centralPosition;
		imCentralPosition = centralPosition;
		ticksIgnored = false;
		animationDirection = CENTRAL_ANIMATION;
		showPeriod =  (sequenceDuration / (numImages));
		dbImages = images;

	} 


	/** 
	 * We assume that this method is called every animPeriod ms
	 */
	public void updateTick(){

		if (!ticksIgnored) {
			// evaluate if skip a new immage
			animTotalTime = animTotalTime + animPeriod;
			if(animTotalTime > showPeriod){	//skyp to a new immage
				animTotalTime = 0;
				calculateNewImageIndex();	
			}
			
		}
	}  

	/**
	 * calcualte the new immage position
	 */
	private void calculateNewImageIndex() {
		if(animationDirection == CENTRAL_ANIMATION){
			if(imActualPosition > imCentralPosition){
				imActualPosition -=1;
			}else if(imActualPosition < imCentralPosition){
				imActualPosition +=1;
			}else{
				ticksIgnored = true;
			}
		}else if(animationDirection == UP_ANIMATION){
			if(atUpSequenceEnd()){
				ticksIgnored = true;
			}else{
				imActualPosition +=1;				
			}
		}else if(animationDirection == DOWN_ANIMATION){
			if(atDownSequenceEnd()){
				ticksIgnored = true;
			}else{
				imActualPosition -=1;				
			}
		}
		
	}


	private boolean atUpSequenceEnd(){  
		return (imActualPosition == numImages-1);  
	}
	
	private boolean atDownSequenceEnd(){  
		return (imActualPosition == 0);  
	}
	

	public BufferedImage getCurrentImage(){ 
		return dbImages[imActualPosition]; 
	} 

	public int getCurrentPosition(){  
		return imActualPosition;  
	}	
	
	public void goToCentralAnimation(){
		animationDirection =CENTRAL_ANIMATION;
		ticksIgnored = false;
	}
	
	public void goToUpAnimation(){
		animationDirection =UP_ANIMATION;
		ticksIgnored = false;
	}

	public void goToDownAnimation(){
		animationDirection =DOWN_ANIMATION;
		ticksIgnored = false;
	}	


}
