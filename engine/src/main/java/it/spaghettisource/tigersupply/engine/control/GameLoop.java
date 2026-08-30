package it.spaghettisource.tigersupply.engine.control;



/**
 * The animation loop of the game. Its responsibilities are:
 *
 * <ul>
 *   <li>drive the application loop {@code update, render, sleep};</li>
 *   <li>start and terminate the animation thread;</li>
 *   <li>keep the progression of FPS and UPS at a consistent rate.</li>
 * </ul>
 *
 * <p>On every iteration the loop asks the {@link SceneHost} for the active {@link Scene} and
 * calls {@link Scene#update(float)}, {@link Scene#render()} and {@link Scene#paintScreen()}; when
 * a frame runs long it may skip rendering (up to {@code MAX_FRAME_SKIPS}) while still updating the
 * game state, to keep the update rate close to the target.
 *
 * @author Alessandro D'Ottavio
 *
 */
public class GameLoop extends Thread {

	private static final int NO_DELAYS_PER_YIELD = 16;
	/* Number of frames with a delay of 0 ms before the animation thread yields
	     to other running threads. */

	private static int MAX_FRAME_SKIPS = 5;   // was 2;
	// no. of frames that can be skipped in any one animation loop
	// i.e the games state is updated but not rendered


	private GameContext context;
	private SceneHost sceneHost;
	private long nanosecondPeriod;
	private float secondPeriod;	

	/**
	 * Create the loop.
	 *
	 * @param context the shared game context, never {@code null}
	 * @param sceneHost the scene host that supplies the active scene, never {@code null}
	 */
	public GameLoop(GameContext context,SceneHost sceneHost){
		this.context = context;
		this.nanosecondPeriod = context.getPeriodNanoseconds();	// ms -> nano
		this.secondPeriod = context.getPeriodSeconds();			// ms -> sec	
		this.sceneHost = sceneHost;

	}


	/**
	 * Run the animation loop until {@link GameContext#isStop()} becomes {@code true}, then
	 * terminate the JVM. Each iteration updates, renders and paints the active scene at the
	 * configured frame period, catching up missed updates when frames overrun.
	 */
	public void run(){

		long beforeTime, afterTime, timeDiff, sleepTime;
		long overSleepTime = 0L;
		int noDelays = 0;
		long excess = 0L;

		beforeTime = System.nanoTime();

		Scene scene = null;
		
		while(!context.isStop()){
						
			try{
				//get the scene to render
				scene = sceneHost.getActiveScene();
				
				scene.update(secondPeriod);
				scene.render();
				scene.paintScreen();
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
					scene.update(secondPeriod);    // update state but don't render
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
