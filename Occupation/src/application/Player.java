package application;

import database.templates.ListTemplate;
import database.templates.ObjectTemplate;

public class Player extends ObjectTemplate {
	
	public static final String NAME = "players";
	
	private ListTemplate <Character> characters;
	
	public Player() {
		characters = new ListTemplate <Character> ("characters", Character::new);
		for(int i = 0; i < 3; i++) {
			characters.add(new Character());
		}
	}
	
}
