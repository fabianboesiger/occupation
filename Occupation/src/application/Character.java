package application;

import database.templates.ObjectTemplate;
import database.templates.StringTemplate;

public class Character extends ObjectTemplate {
	
	public static final String NAME = "characters";
	
	private StringTemplate name;
	
	public Character() {
		name = new StringTemplate("name");
		name.set("Max Muster");
	}
	
}
