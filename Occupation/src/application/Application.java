package application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import database.Database;
import database.templates.ObjectTemplate;
import database.validator.Validator;
import mailer.Mailer;
import manager.DatabaseSessionManager;
import responder.RenderResponder;
import server.Request;
import server.Server;

public class Application {
	
	private static final boolean PRODUCTION = false;
	
	private Database database;
	private RenderResponder responder;
	private Mailer mailer;
	private Server server;
	
	private HashMap <String, Object> predefined = new HashMap <String, Object>();

	
	public Application() throws IOException {		
		database = new Database();
		responder = new RenderResponder(predefined, new File("views/web"));
		mailer = new Mailer(predefined, new File("views/mail"));
		server = new Server(8000, new File("public"), responder, new DatabaseSessionManager <User> (database, 7 * 24 * 60 * 60, User::new));
		
		Game game = new Game(database);
		game.start();
		
		routes();
	}
	
	private void routes() throws IOException {
		
		indexRoutes();
		serverRoutes();
		signRoutes();
		recoveryRoutes();
		profileRoutes();
		gameRoutes();
		
	}
	
	private void indexRoutes() {
		
		predefined.put("title", "Occupation");
		if(PRODUCTION) {
			predefined.put("url", "http://occupation.ddnss.ch");
		} else {
			predefined.put("url", "http://127.0.0.1:8000");
		}
		predefined.put("email", "occupationgame@gmail.com");

		server.on("ALL", ".*", (Request request) -> {
			User user = (User) request.session.load();
			if(user == null) {
				predefined.put("username", null);
				predefined.put("mining", 0);
				predefined.put("throttle", 100);
			} else {
				predefined.put("username", user.getUsername());
				predefined.put("mining", user.getMiningPerformance());
				predefined.put("throttle", 100 - user.getMiningPerformance());
			}
			return responder.next();
		});
		
		server.on("GET", "/", (Request request) -> {
			return responder.render("index.html", request.languages);
		});
		
	}
	
	private void gameRoutes() {
		
		server.on("ALL", "/game.*", (Request request) -> {
			if(request.session.load() == null) {
				return responder.redirect("/signin");
			} else {
				return responder.next();
			}
		});
		
		server.on("GET", "/game/overview", (Request request) -> {
			return responder.render("game/overview.html", request.languages);
		});
		
		server.on("GET", "/game/log-list", (Request request) -> {
			User user = (User) request.session.load();
			if(user != null) {
				Player player = user.getPlayer();
				
				HashMap <String, Object> variables = new HashMap <String, Object> ();
				variables.put("logs", player.getLogs());
				
				return responder.render("game/log-list.html", request.languages, variables);
			}
			return responder.redirect("/signin");
		});
		
		server.on("GET", "/game/ranking", (Request request) -> {

			List <ObjectTemplate> objects = database.loadAll(Player.class);
			ArrayList <Player> players = new ArrayList <Player> ();
			for(ObjectTemplate object : objects) {
				players.add((Player) object);
			}
			Collections.sort(players);
			
			HashMap <String, Object> variables = new HashMap <String, Object> ();
			LinkedList <HashMap <String, Object>> ranking = new LinkedList <HashMap <String, Object>> ();
			
			int rank = 0;
			int previousScore = -1;
			for(Player player : players) {
				int currentScore = player.getScore();
				if(currentScore != previousScore) {
					previousScore = currentScore;
					rank++;
				}
				ranking.add(player.getInfo());
				ranking.get(ranking.size() - 1).put("rank", rank);
			}
			
			variables.put("ranking", ranking);
			return responder.render("game/ranking.html", request.languages, variables);
		});
		
		server.on("GET", "/game/map", (Request request) -> {
			User user = (User) request.session.load();
			if(user != null) {
				return responder.render("game/map.html", request.languages);
			}
			return responder.redirect("/signin");
		});
		
		server.on("GET", "/game/inventory", (Request request) -> {				
			return responder.render("game/inventory.html", request.languages);
		});
		
		server.on("GET", "/game/inventory-list", (Request request) -> {
			User user = (User) request.session.load();
			if(user != null) {
				Player player = user.getPlayer();
				
				HashMap <String, Object> variables = new HashMap <String, Object> ();
				LinkedList <HashMap <String, Object>> inventory = player.getInventoryList();
				variables.put("inventory", inventory);
				variables.put("space", player.getSpace());

				return responder.render("game/inventory-list.html", request.languages, variables);
			}
			return responder.redirect("/signin");
		});
		
		server.on("GET", "/game/survivors", (Request request) -> {				
			return responder.render("game/survivors.html", request.languages);
		});
		
		server.on("GET", "/game/survivor-list", (Request request) -> {
			User user = (User) request.session.load();
			if(user != null) {
				Player player = user.getPlayer();
				
				HashMap <String, Object> variables = new HashMap <String, Object> ();
				variables.put("characters", player.getCharacterList());
				
				return responder.render("game/survivor-list.html", request.languages, variables);
			}
			return responder.redirect("/signin");
		});
		
		server.on("POST", "/game/survivors/(.*)", (Request request) -> {
			User user = (User) request.session.load();
			if(user != null) {
				Player player = user.getPlayer();
				Character character = null;
				if((character = (Character) database.loadId(Character.class, request.groups.get(0))) != null) {
					if(player.getCharacters().contains(character)) {
						character.parseFromParameters(request.parameters);
						if(character.validate()) {
							database.update(character);
							return responder.text("success");
						}
					}
				}
			}
			return responder.text("error");
		});
		
	}
	
	private void serverRoutes() {
		
		server.on("GET", "/server", (Request request) -> {
			return responder.render("server.html", request.languages);
		});
		
		server.on("GET", "/stats", (Request request) -> {
			long uptimeMillis = server.uptime();
			
			HashMap <String, Object> variables = new HashMap <String, Object> ();
			variables.put("uptime", /*uptimeMillis/1000/60/60 + "h " + uptimeMillis/1000/60%60 + "m " + uptimeMillis/1000%60 + "s"*/ uptimeMillis);
			variables.put("sessions", "" + server.sessionsCount());
			variables.put("active-sessions", "" + server.activeCount());
			variables.put("handles-per-day", "" + Math.round(server.handlesPerDay()));
			double bytesPerDay = server.bytesPerDay();
			String formatted = null;
			for(int i = 3; i >= 0; i--) {
				double power = Math.pow(1000, i);
				if(bytesPerDay >= power) {
					formatted = String.format("%.3f", (bytesPerDay / power)) + " ";
					switch(i) {
					case 0:
						formatted += "B";
						break;
					case 1:
						formatted += "KB";
						break;
					case 2:
						formatted += "MB";
						break;
					case 3:
						formatted += "GB";
						break;
					}
					break;
				}
			}
			if(formatted == null) {
				formatted = String.format("%.3f", bytesPerDay) + " B";
			}
			
			variables.put("bytes-per-day", "" + formatted);
			
			return responder.render("stats.html", request.languages, variables);
		});
		
	}
	
	private void signRoutes() {
		
		server.on("GET", "/signin", (Request request) -> {
			HashMap <String, Object> variables = new HashMap <String, Object> ();
			addMessagesFlashToVariables(request, "errors", variables);
			return responder.render("signin.html", request.languages, variables);
		});
		
		server.on("POST", "/signin", (Request request) -> {
			Validator validator = new Validator("errors");
			User user = null;
			if((user = (User) database.load(User.class, request.parameters.get("username"))) != null) {
				if(user.authenticate(request.parameters.get("password"))) {
					user.setLanguages(request.languages);
					database.update(user);					
					request.session.save(user);
					return responder.redirect("/");
				} else {
					validator.addMessage("password", "does-not-match");
				}
			} else {
				validator.addMessage("user", "does-not-exist");
			}
			request.session.addFlash(validator);
			return responder.redirect("/signin");
		});
		
		server.on("GET", "/signup", (Request request) -> {
			HashMap <String, Object> variables = new HashMap <String, Object> ();
			addMessagesFlashToVariables(request, "errors", variables);
			return responder.render("signup.html", request.languages, variables);
		});
		
		server.on("POST", "/signup", (Request request) -> {

			User user = new User();
			user.parseFromParameters(request.parameters);
			user.setLanguages(request.languages);
			
			user.getPlayer().addLog("log-welcome", new String[] {user.getUsername()});
			user.getPlayer().addCharacter();

			Validator validator = new Validator("errors");
			
			if(user.validate(validator)) {
				if(database.save(user)) {
					request.session.save(user);
					
					sendActivationMail(user);
					
					return responder.redirect("/"); 
				} else {
					validator.addMessage("username", "in-use");
				}
			}

			request.session.addFlash(validator);
			return responder.redirect("/signup");
		});
		
		server.on("GET", "/signout", (Request request) -> {
			request.session.delete();
			return responder.redirect("/");
		});
		
	}
	
	private void recoveryRoutes() {
		
		server.on("GET", "/activate", (Request request) -> {	
			User user = null;
			if((user = (User) database.loadId(User.class, request.parameters.get("id"))) != null) {
				if(user.keyEquals(request.parameters.get("key"))) {
					request.session.save(user);
					user.setActivated(true);
					database.update(user);
					return responder.redirect("/profile");
				}
			}
			return responder.render("recover/activate.html", request.languages);
		});
		
		server.on("GET", "/recover", (Request request) -> {
			HashMap <String, Object> variables = new HashMap <String, Object> ();
			addMessagesFlashToVariables(request, "errors", variables);
			return responder.render("recover/index.html", request.languages, variables);
		});
		
		server.on("POST", "/recover", (Request request) -> {
			Validator validator = new Validator("errors");
			User user = null;
			if((user = (User) database.load(User.class, request.parameters.get("username"))) != null) {
				if(user.isActivated()) {
					sendRecoverMail(user);
					return responder.redirect("/recover/confirm");
				} else {
					validator.addMessage("user", "not-activated");
				}
			} else {
				validator.addMessage("user", "does-not-exist");
			}
			
			request.session.addFlash(validator);
			return responder.redirect("/recover");
		});
		
		server.on("GET", "/recover/confirm", (Request request) -> {
			return responder.render("recover/confirm.html", request.languages);
		});
		
		server.on("GET", "/unlock", (Request request) -> {	
			User user = null;
			if((user = (User) database.loadId(User.class, request.parameters.get("id"))) != null) {
				if(user.keyEquals(request.parameters.get("key"))) {
					request.session.save(user);
					return responder.redirect("/profile/password");
				}
			}
			return responder.render("recover/unlock.html", request.languages);
		});
		
	}
	
	private void profileRoutes() {
		
		server.on("ALL", "/profile.*", (Request request) -> {
			if(request.session.load() == null) {
				return responder.redirect("/signin");
			} else {
				return responder.next();
			}
		});
		
		server.on("GET", "/profile", (Request request) -> {
			User user = (User) request.session.load();
			if(user != null) {
				HashMap <String, Object> variables = new HashMap <String, Object> ();
				variables.put("activated", user.isActivated());
				variables.put("notifications", user.notificationsEnabled());
				return responder.render("profile/index.html", request.languages, variables);
			}
			return responder.redirect("/signin");
		});
		
		server.on("GET", "/profile/email", (Request request) -> {
			User user = (User) request.session.load();
			if(user != null) {
				HashMap <String, Object> variables = new HashMap <String, Object> ();
				addMessagesFlashToVariables(request, "errors", variables);
				variables.put("email", user.getMail());
				return responder.render("profile/email.html", request.languages, variables);
			}
			return responder.redirect("/signin");
		});
		
		server.on("POST", "/profile/email", (Request request) -> {
			Validator validator = new Validator("errors");
			User user = (User) request.session.load();
			if(user != null) {
				user.setMail(request.parameters.get("email"));
				user.setActivated(false);
				if(user.validate(validator)) {
					if(database.update(user)) {
						sendActivationMail(user);
						return responder.redirect("/profile");
					}
				}
			} else {
				validator.addMessage("user", "does-not-exist");
			}
			
			request.session.addFlash(validator);
			return responder.redirect("/profile/email");
		});
		
		server.on("GET", "/profile/password", (Request request) -> {
			HashMap <String, Object> variables = new HashMap <String, Object> ();
			addMessagesFlashToVariables(request, "errors", variables);
			return responder.render("profile/password.html", request.languages, variables);
		});
		
		server.on("POST", "/profile/password", (Request request) -> {
			Validator validator = new Validator("errors");
			User user = (User) request.session.load();
			if(user != null) {
				user.setPassword(request.parameters.get("password"));
				if(user.validate(validator)) {
					if(database.update(user)) {
						return responder.redirect("/profile");
					}
				}
			} else {
				validator.addMessage("user", "does-not-exist");
			}
			
			request.session.addFlash(validator);
			return responder.redirect("/profile/password");
		});
		
		server.on("GET", "/profile/notifications", (Request request) -> {
			User user = (User) request.session.load();
			if(user != null) {
				user.toggleNotifications();
				if(user.validate()) {
					database.update(user);
				}
			} else {
				return responder.redirect("/signin");
			}
			
			return responder.redirect("/profile");
		});
		
		server.on("GET", "/profile/delete", (Request request) -> {
			HashMap <String, Object> variables = new HashMap <String, Object> ();
			addMessagesFlashToVariables(request, "errors", variables);
			return responder.render("profile/delete.html", request.languages, variables);
		});
		
		server.on("GET", "/profile/delete/confirm", (Request request) -> {
			if(request.session.load() != null) {
				if(database.delete(User.class, ((String) request.session.load()))) {
					request.session.delete();
					return responder.redirect("/");
				}
			}
			Validator validator = new Validator("errors");
			validator.addMessage("user", "deletion-error");
			request.session.addFlash(validator);
			return responder.redirect("/profile/delete");
		});
		
		server.on("POST", "/profile/mining", (Request request) -> {
			User user = (User) request.session.load();
			if(user != null) {
				user.setMiningPerformance(Double.parseDouble(request.parameters.get("mining")));
				if(user.validate()) {
					database.update(user);
					return responder.text("success");
				}
			}
			return responder.text("error");
		});
		
	}
	
	private void sendActivationMail(User user) {
		if(!PRODUCTION) {
			return;
		}
		HashMap <String, Object> variables = new HashMap <String, Object> ();
		variables.put("username", user.getUsername());
		variables.put("encrypted-username", Database.encrypt(user.getUsername()));
		variables.put("key", user.getKey());
		mailer.send(user.getMail(), "{{print translate \"activate-account\"}}", "activate.html", user.getLanguages(), variables);
	}
	
	private void sendRecoverMail(User user) {
		if(!PRODUCTION) {
			return;
		}
		HashMap <String, Object> variables = new HashMap <String, Object> ();
		variables.put("username", user.getUsername());
		variables.put("encrypted-username", Database.encrypt(user.getUsername()));
		variables.put("key", user.getKey());
		mailer.send(user.getMail(), "{{print translate \"recover-account\"}}", "recover.html", user.getLanguages(), variables);
	}

	private void addMessagesFlashToVariables(Request request, String name, HashMap <String, Object> variables) {
		Validator validator = (Validator) request.session.getFlash(name);
		if(validator != null) {
			validator.addToVariables(variables);
		}
	}
	
}
