package application;

import database.templates.ObjectTemplate;
import database.templates.StringTemplate;

public class Character extends ObjectTemplate {
	
	private StringTemplate name;
	
	public Character() {
		name = new StringTemplate("name");
		name.set("Max Muster");
	}
	
}
