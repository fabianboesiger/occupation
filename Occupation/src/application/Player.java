package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import database.Database;
import database.templates.IntegerTemplate;
import database.templates.ListTemplate;
import database.templates.ObjectTemplate;
import database.templates.ObjectTemplateReference;

public class Player extends ObjectTemplate implements Comparable <Player> {
	
	public static final String NAME = "players";

	private static final int LOGS_SIZE = 32;
	
	private ListTemplate <Character> characters;
	private ListTemplate <IntegerTemplate> inventory;
	private IntegerTemplate space;
	private ListTemplate <Log> logs;
	private ObjectTemplateReference <User> user;
	
	public Player(User user) {
		characters = new ListTemplate <Character> ("characters", Character::new);
		inventory = new ListTemplate <IntegerTemplate> ("inventory", IntegerTemplate::new);
		space = new IntegerTemplate("space", 0, null);
		space.set(1000000);
		this.user = new ObjectTemplateReference <User> ("user", User::new);
		this.user.set(user);
		logs = new ListTemplate <Log> ("logs", Log::new);
	}
	
	public Player() {
		this(null);
	}
	
	public LinkedList <HashMap <String, Object>> getCharacterList() {
		LinkedList <HashMap <String, Object>> output = new LinkedList <HashMap <String, Object>> ();
		for(Character character : characters) {
			output.add(character.renderPrimitivesToMap(new String[]{"name", "activity", "strength", "endurance", "dexterity", "focus", "intelligence", "health", "hunger", "fatigue"}));
			output.get(output.size() - 1).put("id", character.getId());
		}
		return output;
	}
	
	public LinkedList <HashMap <String, Object>> getInventoryList() {
		LinkedList <HashMap <String, Object>> output = new LinkedList <HashMap <String, Object>> ();
		for(int i = 0; i < inventory.size(); i++) {
			output.add(getItemStats(i));
			output.get(output.size() - 1).put("quantity", inventory.get(i).get());
		}
		return output;
	}
	
	public int getSpace() {
		return space.get();
	}
	
	public List <Character> getCharacters() {
		return characters;
	}
	
	public void update() {
		for(Character character : characters) {
			character.update();
		}
	}
	
	public boolean addItems(int type, int amount) {
		while(inventory.size() <= type) {
			IntegerTemplate zero = new IntegerTemplate();
			zero.set(0);
			inventory.add(zero);
		}
		
		HashMap <String, Object> map = getItemStats(type);
		if(map == null) {
			return false;
		}
		
		if((Integer) space.get() < ((Integer) map.get("space")) * amount) {
			return false;
		}
		
		inventory.get(type).set(((Integer) inventory.get(type).get()) + amount);
		space.set((Integer) space.get() - ((Integer) map.get("space")) * amount);
		return true;
	}
	
	public boolean removeItems(int type, int amount) {
		if((Integer) inventory.get(type).get() < amount) {	
			return false;
		}
		
		HashMap <String, Object> map = getItemStats(type);
		if(map == null) {
			return false;
		}
		
		inventory.get(type).set(((Integer) inventory.get(type).get()) - amount);
		space.set((Integer) space.get() + ((Integer) map.get("space")) * amount);
		return true;

	}
	
	private HashMap <String, Object> getItemStats(int type) {
		File file = new File("stats/items/" + type + ".txt");
		if(!file.exists()) {
			return null;
		}
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Database.ENCODING));
			HashMap <String, Object> map = new HashMap <String, Object> ();
			map.put("name", "default");
			map.put("space", 0);
			
			String line = null;
			while((line = bufferedReader.readLine()) != null) {
				int indexOfEquals = line.indexOf("=");
				if(indexOfEquals != -1) {
					String key = line.substring(0, indexOfEquals).trim();
					String value = line.substring(indexOfEquals + 1, line.length()).trim();
					if(map.containsKey(key)) {
						try {
							Object object = map.get(key).getClass().getConstructor(String.class).newInstance(value);
							map.put(key, object);
						} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}
			}
			
			bufferedReader.close();
			
			return map;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	public int getScore() {
		return 0;
	}

	@Override
	public int compareTo(Player other) {
		int thisScore = getScore();
		int otherScore = other.getScore();
		return (thisScore == otherScore) ? 0 : ((thisScore < otherScore) ? -1 : 1);
	}

	public HashMap <String, Object> getInfo() {
		HashMap <String, Object> info = new HashMap <String, Object> ();
		info.put("username", user.get().getUsername());
		info.put("survivors", characters.size());
		info.put("score", getScore());
		return info;
	}
	
	public void addLog(String type, String[] data) {
		logs.add(new Log(type, data));
		while(logs.size() > LOGS_SIZE) {
			Log log = logs.remove(0);
			database.deleteId(Log.class, log.getId());
		}
	}
	
	public void addCharacter() {
		Character character = new Character(this);
		characters.add(character);
		addLog("log-new-character", new String[] {character.getName()});
	}

	public LinkedList <HashMap <String, Object>> getLogs() {
		LinkedList <HashMap <String, Object>> output = new LinkedList <HashMap <String, Object>> ();
		for(Log log : logs) {
			output.add(log.getLog());
		}
		Collections.reverse(output);
		return output;
	}

}
