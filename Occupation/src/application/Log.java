package application;

import java.util.HashMap;
import java.util.LinkedList;

import database.templates.ListTemplate;
import database.templates.ObjectTemplate;
import database.templates.StringTemplate;

public class Log extends ObjectTemplate {

	public static final String NAME = "logs";
	
	StringTemplate type;
	ListTemplate <StringTemplate> data;
	
	public Log(String type, String[] data) {
		this.type = new StringTemplate("type");
		this.type.set(type);
		this.data = new ListTemplate <StringTemplate> ("data", StringTemplate::new);
		if(data != null) {
			for(String bit : data) {
				StringTemplate template = new StringTemplate();
				template.set(bit);
				this.data.add(template);
			}
		}
	}
	
	public Log() {
		this(null, null);
	}
	
	public HashMap <String, Object> getLog() {
		HashMap <String, Object> output = new HashMap <String, Object> ();
		output.put("type", type.get());
		LinkedList <String> data = new LinkedList <String> ();
		for(StringTemplate bit : this.data) {
			data.add(bit.get());
		}
		output.put("data", data);
		output.put("time", Long.valueOf(System.currentTimeMillis() - timestamp.get()));
		return output;
	}
	
}
