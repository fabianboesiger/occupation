package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import database.Database;
import database.templates.IntegerTemplate;
import database.templates.ListTemplate;
import database.templates.ObjectTemplate;

public class Player extends ObjectTemplate {
	
	public static final String NAME = "players";
	
	private ListTemplate <Character> characters;
	private ListTemplate <IntegerTemplate> inventory;
	private IntegerTemplate space;
	
	public Player() {
		characters = new ListTemplate <Character> ("characters", Character::new);
		for(int i = 0; i < 3; i++) {
			characters.add(new Character());
		}
		inventory = new ListTemplate <IntegerTemplate> ("inventory", IntegerTemplate::new);
		space = new IntegerTemplate("space", 0, null);
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
	
	public boolean addItems(int type, int amount) {
		while(inventory.size() < type) {
			IntegerTemplate zero = new IntegerTemplate();
			zero.set(0);
			inventory.add(zero);
		}
		
		if(!file.exists()) {
			return false;
		}
		
		
		if(space.get() < ) {
			inventory.get(type).set(((Integer) inventory.get(type).get()) + amount);
		}
	}
	
	public boolean removeItems(int type, int amount) {
		if((Integer) inventory.get(type).get() >= amount) {
			inventory.get(type).set(((Integer) inventory.get(type).get()) - amount);
			return true;
		}
		return false;
	}
	
	public HashMap <String, Object> getItemStats(int type) {
		File file = new File("stats/items/" + type + ".txt");
		if(!file.exists()) {
			return null;
		}
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Database.ENCODING));
			HashMap <String, String> map = new HashMap <String, String> ();
			
			String line = null;
			while((line = bufferedReader.readLine()) != null) {
				int indexOfEquals = line.indexOf("=");
				if(indexOfEquals != -1) {
					String key = line.substring(0, indexOfEquals).trim();
					String value = line.substring(indexOfEquals + 1, line.length()).trim();
					map.put(key, value);
				}
			}
			
			bufferedReader.close();
			
			return map;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
}
