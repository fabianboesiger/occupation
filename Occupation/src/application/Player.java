package application;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import database.templates.IntegerTemplate;
import database.templates.ListTemplate;
import database.templates.ObjectTemplate;

public class Player extends ObjectTemplate {
	
	public static final String NAME = "players";
	
	private ListTemplate <Character> characters;
	private ListTemplate <IntegerTemplate> inventory;
	
	public Player() {
		characters = new ListTemplate <Character> ("characters", Character::new);
		for(int i = 0; i < 3; i++) {
			characters.add(new Character());
		}
		inventory = new ListTemplate <IntegerTemplate> ("inventory", IntegerTemplate::new);
	}
	
	public LinkedList <HashMap <String, Object>> getCharacterList() {
		LinkedList <HashMap <String, Object>> output = new LinkedList <HashMap <String, Object>> ();
		for(Character character : characters) {
			output.add(character.renderPrimitivesToMap(new String[]{"name", "activity", "strength", "endurance", "dexterity", "focus", "intelligence", "health", "hunger", "fatigue"}));
			output.get(output.size() - 1).put("id", character.getId());
		}
		return output;
	}

	public List <Character> getCharacters() {
		return characters;
	}
	
	public void update(int multiplier) {
		for(Character character : characters) {
			character.work(multiplier);
		}
	}
	
}
