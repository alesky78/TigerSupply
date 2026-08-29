package it.spaghettisource.tigersupply.engine.image;

import java.awt.image.BufferedImage;


public class ImagesPlayer
{
	private boolean isRepeating;
	private boolean ticksIgnored;

	private double animPeriod; 		// period used by animation loop (in ms)
	private long animTotalTime;
	private double showPeriod;    		// period the current image is shown (in ms) example 12.5  tipical period for 90 FPS
	private double seqDuration; 	// total duration of the entire image sequence (in millisecond)  ex 1000 = 1 second  
	private int numImages;
	private int imPosition;     	// position of current displayable image

	private BufferedImage[] dbImages;

	private ImagesPlayerWatcher watcher = null;

	/**
	 * 
	 * @param imageName
	 * @param animationPeriod in millisecond
	 * @param sequenceDuration in millisecond
	 * @param repeatImage
	 * @param il
	 */
	public ImagesPlayer(double animationPeriod, double sequenceDuration, boolean repeatImage, BufferedImage[] images) {
		animPeriod = animationPeriod; 
		seqDuration = sequenceDuration;
		isRepeating = repeatImage;
		animTotalTime = 0L;
		numImages = images.length;
		imPosition = 0;
		ticksIgnored = false;
		showPeriod =  (seqDuration / (numImages));
		dbImages = images;

	} 


	/** 
	 * We assume that this method is called every animPeriod ms
	 */
	public void updateTick(){

		if (!ticksIgnored) {
			// update total animation time, modulo the animation sequence duration
			animTotalTime = (long) ((animTotalTime + animPeriod) % (long)(seqDuration));

			// calculate current displayable image position
			imPosition = (int) (animTotalTime / showPeriod);   // in range 0 to num-1

			if ((imPosition == numImages-1) && (!isRepeating)) {  // at end of sequence
				ticksIgnored = true;   // stop at this image
				if (watcher != null)
					watcher.sequenceEnded();   // call callback
			}
		}
	}  


	public BufferedImage getCurrentImage(){ 
		return dbImages[imPosition]; 
	} 

	public int getCurrentPosition(){  
		return imPosition;  
	}

	public void setWatcher(ImagesPlayerWatcher w){  
		watcher = w;  
	}


	/** 
	 * updateTick() calls will no longer update the
	 * total animation time or imPosition.
	 */ 
	public void stop(){  
		ticksIgnored = true;  
	}


	public boolean isStopped(){  
		return ticksIgnored;  
	}

	/**
	 * are we at the last image and not cycling through them?
	 * 
	 * @return
	 */
	public boolean atSequenceEnd(){  
		return ((imPosition == numImages-1) && (!isRepeating));  
	}



	public void restartAt(int imPosn){
		if (numImages != 0) {
			if ((imPosn < 0) || (imPosn > numImages-1)) {
				System.out.println("Out of range restart, starting at 0");
				imPosn = 0;
			}

			imPosition = imPosn;
			// calculate a suitable animation time
			animTotalTime = (long) ((long) imPosition * showPeriod);
			ticksIgnored = false;
		}
	}


	
	public void resume(){ 
		if (numImages != 0)
			ticksIgnored = false;
	} 


}
