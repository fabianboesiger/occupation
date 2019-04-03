package application;

import java.util.HashMap;
import java.util.LinkedList;

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
	
	public LinkedList <HashMap <String, Object>> getCharacters() {
		LinkedList <HashMap <String, Object>> output = new LinkedList <HashMap <String, Object>> ();
		for(Character character : characters) {
			output.add(character.renderPrimitivesToMap(new String[]{"name"}));
		}
		return output;
	}
	
}
