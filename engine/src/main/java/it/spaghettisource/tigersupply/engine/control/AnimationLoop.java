package it.spaghettisource.tigersupply.engine.control;



/**
 * this is the animation loop it has this targets:
 * 
 *  - manage the application loop {update, render, sleep}
 *  - starting and terminating animation
 *  - ensure that the progerssion of FPS and UPS is manage at consistent rate 
 * 
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class AnimationLoop extends Thread {

	private static final int NO_DELAYS_PER_YIELD = 16;
	/* Number of frames with a delay of 0 ms before the animation thread yields
	     to other running threads. */

	private static int MAX_FRAME_SKIPS = 5;   // was 2;
	// no. of frames that can be skipped in any one animation loop
	// i.e the games state is updated but not rendered


	private ApplicationContext context;
	private GameManager gameManager;
	private long nanosecondPeriod;
	private float secondPeriod;	

	/**
	 * 
	 * @param context
	 * @param game
	 * @param period in milliseconds
	 */
	public AnimationLoop(ApplicationContext context,GameManager manager){
		this.context = context;
		this.nanosecondPeriod = context.getPeriodNanoseconds();	// ms -> nano
		this.secondPeriod = context.getPeriodSeconds();			// ms -> sec	
		this.gameManager = manager;

	}


	public void run(){

		long beforeTime, afterTime, timeDiff, sleepTime;
		long overSleepTime = 0L;
		int noDelays = 0;
		long excess = 0L;

		beforeTime = System.nanoTime();

		Game game = null;
		
		while(!context.isStop()){
						
			try{
				//get the scene to render
				game = gameManager.getActualGame();
				
				game.updateGame(secondPeriod);
				game.renderGame();
				game.paintScreen();
			}catch (Exception e) {
				e.printStackTrace();
				context.requestStopGame();
			}

			afterTime = System.nanoTime();
			timeDiff = afterTime - beforeTime;
			sleepTime = (nanosecondPeriod - timeDiff) - overSleepTime;  


			if (sleepTime > 0) {   // some time left in this cycle
				try {
					//System.out.println("cicle: required sleep "+sleepTime/1000000L + " time diff"+timeDiff + " Period" +period);
					Thread.sleep(sleepTime/1000000L);  // nano -> ms
				}
				catch(InterruptedException ex){}
				overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
			}else {    // sleepTime <= 0; the frame took longer than the period
				excess -= sleepTime;  // store excess time value, i.e. excess is positive because excess = excess - (- value) = excess + value
				overSleepTime = 0L;

				if (++noDelays >= NO_DELAYS_PER_YIELD) {
					Thread.yield();   // give another thread a chance to run
					noDelays = 0;
				}
			}

			beforeTime = System.nanoTime();

			/* If frame animation is taking too long, update the game state
	         without rendering it, to get the updates/sec nearer to
	         the required FPS. */
			int skips = 0;
			while((excess > nanosecondPeriod) && (skips < MAX_FRAME_SKIPS)) {
				excess -= nanosecondPeriod;
				try{
					game.updateGame(secondPeriod);    // update state but don't render
				}catch (Exception e) {
					e.printStackTrace();
					context.requestStopGame();
				}
				skips++;
			}
		}

		//exit from the game in this way or the thread on windows event is handle
		System.exit(0);

	}


}
