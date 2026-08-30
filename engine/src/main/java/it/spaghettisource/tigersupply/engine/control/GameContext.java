package it.spaghettisource.tigersupply.engine.control;


/**
 * Holds the shared runtime state of the game loop: the run/pause flags, the frame period, and the
 * drawable screen size. This is the engine's own lifecycle context, not a dependency-injection
 * container.
 *
 * @author Alessandro D'Ottavio
 *
 */
public class GameContext {

	private volatile boolean running = false;   // used to stop the animation thread, it drastically detory the thread
	private volatile boolean isPaused = false;	// used to don't update the game logic   
	
	private volatile float periodInMilliseconds;		//period of the application in milliseconds
	private volatile int screenWidth,  screenHeight;	//size of the drawable used to render the game	
	
	/** Request the game loop to stop; the animation thread will terminate. */
	public void requestStopGame() {
		running = false;
	}

	/** Request the game logic to pause while rendering continues. */
	public void requestPauseGame() {
		isPaused = true;
	}
	
	/** Request the game logic to resume after a pause. */
	public void requestResumeGame(){
		isPaused = false;
	}

	/** Request the game loop to start running. */
	public void requestStartGame() {
		running = true;
	}

	/**
	 * @return {@code true} when the loop should stop (i.e. it is not running)
	 */
	public boolean isStop() {
		return !running;
	}

	/**
	 * @return {@code true} when the game logic is paused
	 */
	public boolean isPaused() {
		return isPaused;
	}

	/**
	 * Set the frame period.
	 *
	 * @param periodInMilliseconds the target frame period in milliseconds
	 */
	public void setPeriodInMilliseconds(float periodInMilliseconds) {
		this.periodInMilliseconds = periodInMilliseconds;
	}

	/**
	 * Set the drawable width.
	 *
	 * @param screenWidth the drawable width in pixels
	 */
	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	/**
	 * Set the drawable height.
	 *
	 * @param screenHeight the drawable height in pixels
	 */
	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	/**
	 * @return the frame period in milliseconds
	 */
	public float getPeriodMilliseconds() {
		return periodInMilliseconds;
	}

	/**
	 * @return the frame period in seconds
	 */
	public float getPeriodSeconds() {
		return periodInMilliseconds/1000;
	}	
	
	/**
	 * @return the frame period in nanoseconds
	 */
	public long getPeriodNanoseconds() {
		return (long) (periodInMilliseconds*1000000);
	}	
		
	/**
	 * @return the drawable width in pixels
	 */
	public int getScreenWidth() {
		return screenWidth;
	}
	
	/**
	 * @return the drawable height in pixels
	 */
	public int getScreenHeight() {
		return screenHeight;
	}	
	


}
