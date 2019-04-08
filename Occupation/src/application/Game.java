package application;

import java.util.LinkedList;

import database.Database;
import database.templates.ObjectTemplate;

public class Game extends Thread {
	
	private static final int LOOP_TIME = 5000;
	private static final int MULTIPLIER = 1;
	private boolean paused;
	private long nextLoop;
	private Database database;
	
	public Game(Database database) {
		nextLoop =  System.currentTimeMillis();
		paused = false;
		this.database = database;
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
		LinkedList <ObjectTemplate> users = database.loadAll(User.class);
		for(ObjectTemplate object : users) {
			Player player = ((User) object).getPlayer();
			player.update(MULTIPLIER);
			database.update(player);
		}
	}

}
