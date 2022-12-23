package com.siwek9.main;

import java.io.IOException;

// import java.io.File;
// import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

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
		new TokenType("Secret", BOOLEAN_TYPE),
		new TokenType("ForceManualStart", BOOLEAN_TYPE),
		new TokenType("TimeOfReminder", INTEGER_TYPE),
		new TokenType("ReminderMessage", STRING_TYPE),
		new TokenType("EventMessage", STRING_TYPE),	
	};

	String Name;

	String Date;
	String Time;

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

	ChestEventFile(String Name) {
		this.Name = Name;
		this.Date = plugin.config.getString("Defaults.Date");
		this.Time = plugin.config.getString("Defaults.Time");
		for (TokenType token : eventVariables) {
			plugin.events.set(GetDataDirectory(token.Name), plugin.config.get("Defaults." + token.Name));
		}
		saveEventsFile();
		System.out.println(plugin.events.get(GetDataDirectory("Secret")).getClass());
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
		this.Date = plugin.events.getString(GetDataDirectory("Date"));
		this.Time = plugin.events.getString(GetDataDirectory("Time"));
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
		this.Date = plugin.events.getString(GetDataDirectory("Date"));
		this.Time = plugin.events.getString(GetDataDirectory("Time"));
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
		this.Date = plugin.events.getString(GetDataDirectory("Date"));
		this.Time = plugin.events.getString(GetDataDirectory("Time"));

	}


	// void deleteEvent() {
	// 	System.out.print("usunieto event");
	// }
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
