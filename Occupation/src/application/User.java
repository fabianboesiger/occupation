package application;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import database.templates.BooleanTemplate;
import database.templates.IdentifiableStringTemplate;
import database.templates.ListTemplate;
import database.templates.ObjectTemplate;
import database.templates.ObjectTemplateReference;
import database.templates.StringTemplate;

public class User extends ObjectTemplate {
	
	public static final String NAME = "users";
	
	public static Random random = new Random();
	
	private IdentifiableStringTemplate username;
	private StringTemplate password;
	private StringTemplate email;
	private BooleanTemplate activated;
	private StringTemplate key;
	private BooleanTemplate notifications;
	private ObjectTemplateReference <Player> player;
	private ListTemplate <StringTemplate> languages;
	
	public User() {
		username = new IdentifiableStringTemplate("username", 2, 16);
		password = new StringTemplate("password", 4, 32, (Object value) -> {
			return hash((String) value);
		});
		email = new StringTemplate("email", 0, 64);
		activated = new BooleanTemplate("activated");
		activated.set(false);
		key = new StringTemplate("key", 64, 64);
		key.set(generateKey(64));
		notifications = new BooleanTemplate("notifications");
		notifications.set(true);
		player = new ObjectTemplateReference <Player> ("player", Player::new);
		player.set(new Player());
		languages = new ListTemplate <StringTemplate> ("languages", StringTemplate::new);
		StringTemplate defaultLanguage = new StringTemplate();
		defaultLanguage.set("en");
		languages.add(defaultLanguage);
		setIdentifier(username);
	}
	
	public boolean authenticate(String password) {
		if(password != null) {
			try { 
				if(hash(password).equals(this.password.get())) {
					return true;
				}
			} catch(NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public String getUsername() {
		return (String) username.get();
	}
	
	public static String hash (String input) throws NoSuchAlgorithmException {
		if(input == null) {
			return null;
		}
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
		messageDigest.update(input.getBytes());
		byte[] bytes = messageDigest.digest();
		StringBuilder stringBuilder = new StringBuilder();
		for(int i = 0; i < bytes.length ; i++) {
			stringBuilder.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		return stringBuilder.toString();
	}

	public String getMail() {
		return (String) email.get();
	}

	public void setMail(String value) {
		email.set(value);
		key.set(generateKey(64));
	}

	public void setPassword(String value) {
		password.set(value);
	}
	
	public boolean isActivated() {
		return (Boolean) activated.get();
	}
	
	public void setActivated(boolean value) {
		activated.set(value);
	}

	public String getKey() {
		return (String) key.get();
	}
	
	public void toggleNotifications() {
		notifications.set(!((Boolean) notifications.get()));
	}

	public boolean notificationsEnabled() {
		return (Boolean) notifications.get();
	}
	
	public void setLanguages(List <String> languages) {
		this.languages.clear();
		for(String language : languages) {
			StringTemplate stringTemplate = new StringTemplate();
			stringTemplate.set(language);
			this.languages.add(stringTemplate);
		}
	}

	public List<String> getLanguages() {
		LinkedList <String> languages = new LinkedList <String> ();
		for(StringTemplate language : this.languages) {
			languages.add((String) language.get());
		}
		return languages;
	}
	
	public static String generateKey(int length) {
    	String output = "";
    	for(int i = 0; i < length; i++) {
    		int r = random.nextInt(26*2+10)+48;
    		if(r > 57) {
    			r += 7;
    		}
    		if(r > 90) {
    			r += 6;
    		}
    		output += (char) r;
    	}
    	return output;
    }

}
