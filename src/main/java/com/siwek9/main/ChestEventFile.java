package com.siwek9.main;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

// import java.io.File;
// import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.google.gson.JsonObject;

public class ChestEventFile {
	private static ChestEvent plugin = (ChestEvent)Bukkit.getServer().getPluginManager().getPlugin("ChestEvent");

	private static String STRING_TYPE = "java.lang.String";
	private static String INTEGER_TYPE = "java.lang.Integer";
	private static String BOOLEAN_TYPE = "java.lang.Boolean";
	private static TokenType[] eventVariables = 
	{
		new TokenType("Date", STRING_TYPE),
		new TokenType("Time", STRING_TYPE),
		new TokenType("MainLootTable", STRING_TYPE),
		new TokenType("ExtraLootTable", STRING_TYPE),
		new TokenType("NumberOfMainChests", INTEGER_TYPE),
		new TokenType("NumberOfExtraChests", INTEGER_TYPE),
		new TokenType("Secret", BOOLEAN_TYPE),
		new TokenType("ForceManualStart", BOOLEAN_TYPE),
		new TokenType("TimeOfReminder", INTEGER_TYPE),
		new TokenType("TimeOfLock", INTEGER_TYPE),
		new TokenType("ReminderMessage", STRING_TYPE),
		new TokenType("EventMessage", STRING_TYPE),	
	};

	String Name;
	LocalDateTime dateTime;

	byte eventWasBefore = 0;
	boolean isStarted = false;

	
	ChestEventFile(String Name) {
		this.Name = Name;

		for (TokenType token : eventVariables) {
			plugin.events.set(GetDataDirectory(token.Name), plugin.config.get("Defaults." + token.Name));
		}
		saveEventsFile();
		
		dateTime = GetDateTimeFromStrings(plugin.events.getString(GetDataDirectory("Date")), plugin.events.getString(GetDataDirectory("Time")));
	}

	ChestEventFile(String Name, ConfigurationSection eventSection) {
		this.Name = Name;
		for (TokenType token : eventVariables) {
			if (eventSection.contains(token.Name)) {
				plugin.events.set(GetDataDirectory(token.Name), eventSection.get(token.Name));
			}
			else
				plugin.events.set(GetDataDirectory(token.Name), plugin.config.get("Defaults." + token.Name));
		}
		saveEventsFile();
		
		dateTime = GetDateTimeFromStrings(plugin.events.getString(GetDataDirectory("Date")), plugin.events.getString(GetDataDirectory("Time")));
	}	
	
	ChestEventFile(String Name, JsonObject eventData) {
		this.Name = Name;
		for (TokenType token : eventVariables) {
			if (eventData.has(token.Name)) {
				if (token.Type.getTypeName() == STRING_TYPE) {
					plugin.events.set(GetDataDirectory(token.Name), eventData.get(token.Name).getAsString());
				}
				else if (token.Type.getTypeName() == INTEGER_TYPE) {
					plugin.events.set(GetDataDirectory(token.Name), eventData.get(token.Name).getAsInt());
				}
				else if (token.Type.getTypeName() == BOOLEAN_TYPE) {
					plugin.events.set(GetDataDirectory(token.Name), eventData.get(token.Name).getAsBoolean());
				}
			}
			else
				plugin.events.set(GetDataDirectory(token.Name), plugin.config.get("Defaults." + token.Name));
		}
		saveEventsFile();

		dateTime = GetDateTimeFromStrings(plugin.events.getString(GetDataDirectory("Date")), plugin.events.getString(GetDataDirectory("Time")));
	}
	
	ChestEventFile(ChestEventFile OldCopy, JsonObject NewData) {
		this.Name = OldCopy.Name;
		for (TokenType token : eventVariables) {
			if (NewData.has(token.Name)) {
				if (token.Type.getTypeName() == STRING_TYPE) {
					plugin.events.set(GetDataDirectory(token.Name), NewData.get(token.Name).getAsString());
				}
				else if (token.Type.getTypeName() == INTEGER_TYPE) {
					plugin.events.set(GetDataDirectory(token.Name), NewData.get(token.Name).getAsInt());
				}
				else if (token.Type.getTypeName() == BOOLEAN_TYPE) {
					plugin.events.set(GetDataDirectory(token.Name), NewData.get(token.Name).getAsBoolean());
				}
			}
		}
		saveEventsFile();
		
		dateTime = GetDateTimeFromStrings(plugin.events.getString(GetDataDirectory("Date")), plugin.events.getString(GetDataDirectory("Time")));
		
	}
	
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d.MM.yyyy");

	private LocalDateTime GetDateTimeFromStrings(String Date, String Time) {
		LocalDate tempDate;
		LocalTime tempTime;

		if (Date.equals("now"))
			tempDate = LocalDate.now();
		else
			tempDate = LocalDate.parse(plugin.events.getString(GetDataDirectory("Date")), dateFormatter);

		if (Time.equals("now"))
			tempTime = LocalTime.now();
		else
			tempTime = LocalTime.parse(plugin.events.getString(GetDataDirectory("Time")));

		return LocalDateTime.of(tempDate, tempTime);
	}

	private String GetDataDirectory(String data) {
		return this.Name + "." + data;
	}

	void saveEventsFile() {
		try {
			plugin.events.save(plugin.eventsFile);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		if (plugin.events.getBoolean(GetDataDirectory("ForceManualStart")) == true) {
			sendStartMessage();
		}
		else {
			System.out.println("siema");
			isStarted = true;
		}
	}

	public void sendStartMessage() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.isOp()) {
				player.sendMessage("§eThe event §l§c" + Name + "§r§e should start now!\nType §2§o/event start " + Name + "§r§e to start it.");
				System.out.println("The event " + Name + " should start now!\nType \"event start " + Name + "\" to start it.");
			}
		}
		
	}
}

class TokenType {
	String Name;
	Class<?> Type;
	
	TokenType(String Name, String Type) {
		this.Name = Name;
		try {
			this.Type = Class.forName(Type);
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
