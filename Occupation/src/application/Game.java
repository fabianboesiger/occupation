package application;

public class Game extends Thread {
	
	private static final int LOOP_TIME = 1000;
	private boolean paused;
	private long nextLoop;
	
	public Game() {
		nextLoop =  System.currentTimeMillis();
		paused = false;
	}
	
	@Override
	public void run() {
		
		while(!paused) {
			loop();
			nextLoop += LOOP_TIME;
			try {
				sleep(Math.max(0, nextLoop - System.currentTimeMillis()));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void loop() {
	}

}
