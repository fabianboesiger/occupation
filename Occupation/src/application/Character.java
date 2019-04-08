package application;

import java.util.Random;

import database.templates.BooleanTemplate;
import database.templates.IntegerTemplate;
import database.templates.ObjectTemplate;
import database.templates.StringTemplate;

public class Character extends ObjectTemplate {
	
	private static Random random = new Random();
	
	public static final String NAME = "characters";
	
	private StringTemplate name;
	private BooleanTemplate male;
	private IntegerTemplate activity;
	
	private IntegerTemplate health;
	private IntegerTemplate hunger;
	private IntegerTemplate fatigue;
	
	private IntegerTemplate strength;
	private IntegerTemplate endurance;
	private IntegerTemplate dexterity;
	private IntegerTemplate focus;
	private IntegerTemplate intelligence;
	
	// private ObjectTemplateReference <>
	
	public Character() {
		name = new StringTemplate("name");
		name.set("Max Muster");
		male = new BooleanTemplate("male");
		male.set(random.nextBoolean());
		activity = new IntegerTemplate("activity", 0, 1);
		activity.set(0);
		
		strength = new IntegerTemplate("strength", 1, 100);
		strength.set(random.nextInt(100) + 1);
		endurance = new IntegerTemplate("endurance", 1, 100);
		endurance.set(random.nextInt(100) + 1);
		dexterity = new IntegerTemplate("dexterity", 1, 100);
		dexterity.set(random.nextInt(100) + 1);
		focus = new IntegerTemplate("focus", 1, 100);
		focus.set(random.nextInt(100) + 1);
		intelligence = new IntegerTemplate("intelligence", 1, 100);
		intelligence.set(random.nextInt(100) + 1);
		
		health = new IntegerTemplate("health", 0, 100);
		health.set(100);
		hunger = new IntegerTemplate("hunger", 0, 100);
		hunger.set(100);
		fatigue = new IntegerTemplate("fatigue", 0, 100);
		fatigue.set(100);
	}

	public void work(int multiplier) {
		
	}
	
}
