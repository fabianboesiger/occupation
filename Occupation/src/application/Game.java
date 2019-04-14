package application;

import java.util.LinkedList;

import database.Database;
import database.templates.ObjectTemplate;

public class Game extends Thread {
	
	private static final int LOOP_TIME = 5000;
	private static final int MULTIPLIER = 5;
	private boolean paused;
	private long nextLoop;
	private Database database;
	
	public Game(Database database) {
		nextLoop =  System.currentTimeMillis();
		paused = false;
		this.database = database;
		
		System.out.println("Starting Game with Loop Time " + LOOP_TIME + "ms and Multiplier " + MULTIPLIER + ", Speed is " + (double) MULTIPLIER / LOOP_TIME * 1000.0);
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
		System.out.println("Executing Game Loop");
		
		LinkedList <ObjectTemplate> users = database.loadAll(User.class);
		for(ObjectTemplate object : users) {
			Player player = ((User) object).getPlayer();
			for(int i = 0; i < MULTIPLIER; i++) {
				player.update();
			}
			database.update(player);
		}
	}

}
