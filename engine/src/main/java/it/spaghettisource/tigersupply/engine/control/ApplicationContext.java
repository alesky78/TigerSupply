package it.spaghettisource.tigersupply.engine.control;


/**
 * this class has the scope to hold the life cycle of the game
 * 
 * 
 * @author DOttavio
 *
 */
public class ApplicationContext {

	private volatile boolean running = false;   // used to stop the animation thread, it drastically detory the thread
	private volatile boolean isPaused = false;	// used to don't update the game logic   
	
	private volatile float periodInMilliseconds;		//period of the application in milliseconds
	private volatile int screenWidth,  screenHeight;	//size of the drawable used to render the game	
	
	public void requestStopGame() {
		running = false;
	}

	public void requestPauseGame() {
		isPaused = true;
	}
	
	public void requestResumeGame(){
		isPaused = false;
	}

	public void requierdStart() {
		running = true;
	}

	public boolean isStop() {
		return !running;
	}

	public boolean isPaused() {
		return isPaused;
	}

	public void setPeriodInMilliseconds(float periodInMilliseconds) {
		this.periodInMilliseconds = periodInMilliseconds;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public float getPeriodMilliseconds() {
		return periodInMilliseconds;
	}

	public float getPeriodSeconds() {
		return periodInMilliseconds/1000;
	}	
	
	public long getPeriodNanoseconds() {
		return (long) (periodInMilliseconds*1000000);
	}	
		
	public int getScreenWidth() {
		return screenWidth;
	}
	
	public int getScreenHeight() {
		return screenHeight;
	}	
	


}
