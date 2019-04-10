package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import database.Database;
import database.templates.BooleanTemplate;
import database.templates.DoubleTemplate;
import database.templates.IntegerTemplate;
import database.templates.ObjectTemplate;
import database.templates.StringTemplate;

public class Character extends ObjectTemplate {
	
	private static Random random = new Random();
	
	public static final String NAME = "characters";
	
	private static final double HUNGER_PER_SECOND = 100.0 / (24.0 * 60.0 * 60.0);
	private static final double FATIGUE_REGENERATION_PER_SECOND = 100.0 / (8.0 * 60.0 * 60.0);
	private static final double LOOTER_FATIGUE_DRAW_PER_SECOND = 100.0 / (16.0 * 60.0 * 60.0);
	
	private StringTemplate name;
	private BooleanTemplate male;
	private IntegerTemplate activity;
	
	private DoubleTemplate health;
	private DoubleTemplate hunger;
	private DoubleTemplate fatigue;
	
	private IntegerTemplate strength;
	private IntegerTemplate endurance;
	private IntegerTemplate dexterity;
	private IntegerTemplate focus;
	private IntegerTemplate intelligence;
	
	// private ObjectTemplateReference <>
	
	public Character() {
		male = new BooleanTemplate("male");
		male.set(random.nextBoolean());
		name = new StringTemplate("name");
		String randomName = "";
		if(male.get()) {
			randomName += randomSelect(new File("stats/names/male-forenames.txt"));
		} else {
			randomName += randomSelect(new File("stats/names/female-forenames.txt"));
		}
		randomName += (" " + randomSelect(new File("stats/names/surnames.txt")));
		name.set(randomName);
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
		
		health = new DoubleTemplate("health", 0.0, 100.0);
		health.set(100.0);
		hunger = new DoubleTemplate("hunger", 0.0, 100.0);
		hunger.set(100.0);
		fatigue = new DoubleTemplate("fatigue", 0.0, 100.0);
		fatigue.set(100.0);
	}

	public void update() {
		addToDouble(hunger, -HUNGER_PER_SECOND);
		
		if(activity.get() == 0) {
			// Resting
			addToDouble(fatigue, FATIGUE_REGENERATION_PER_SECOND);
		} else
		if(activity.get() == 1) {
			// Looter
			addToDouble(fatigue, -LOOTER_FATIGUE_DRAW_PER_SECOND);
		} else
		if(activity.get() == 2) {
			// Looter
			addToDouble(fatigue, -LOOTER_FATIGUE_DRAW_PER_SECOND);
		} else
		if(activity.get() == 3) {
			// Looter
			addToDouble(fatigue, -LOOTER_FATIGUE_DRAW_PER_SECOND);
		} else
		if(activity.get() == 4) {
			// Looter
			addToDouble(fatigue, -LOOTER_FATIGUE_DRAW_PER_SECOND);
		}
	}
	
	public void addToDouble(DoubleTemplate template, double summand) {
		if(template.get() + summand > template.getMaximum()) {
			template.set(template.getMaximum());
		} else
		if(template.get() + summand < template.getMinimum()) {
			template.set(template.getMinimum());
		} else {
			template.set(template.get() + summand);
		}
	}
	
	public String randomSelect(File file) {
	
		if(!file.exists()) {
			return null;
		}
		
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Database.ENCODING));
			ArrayList <String> list = new ArrayList <String> ();
			
			String line = null;
			while((line = bufferedReader.readLine()) != null) {
				list.add(line);
			}
			
			bufferedReader.close();
			
			return list.get(random.nextInt(list.size()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
}
