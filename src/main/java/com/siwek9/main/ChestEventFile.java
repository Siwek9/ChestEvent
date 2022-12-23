package com.siwek9.main;

// import java.io.File;
// import java.io.IOException;

import org.bukkit.Bukkit;

public class ChestEventFile {
	public static ChestEvent plugin = (ChestEvent)Bukkit.getServer().getPluginManager().getPlugin("ChestEvent");

	String Name;
	// String Date;
	// String Time;
	// String MainLootTable;
	// String ExtraLootTable;
	// int NumberOfMainChests;
	// int NumberOfExtraChests;
	// boolean Secret;
	// boolean ForceManualStart;
	// int TimeOfReminder;
	// String ReminderMessage;
	// String EventMessage;

	// private String GetDataDirectory(String data) {
	// 	return this.Name + "." + data;
	// }

	ChestEventFile(String Name) {
		
		this.Name = Name;

		// plugin.eventsFile.addDefault(GetDataDirectory("Date"), plugin.config.getString("Defaults.Date"));
		// plugin.eventsFile.addDefault(GetDataDirectory("Date"), plugin.config.getString("Defaults.Time"));
		// plugin.eventsFile.addDefault(GetDataDirectory("Date"), plugin.config.getString("Defaults.MainLootTable"));
		// plugin.eventsFile.addDefault(GetDataDirectory("Date"), plugin.config.getString("Defaults.ExtraLootTable"));
		// plugin.eventsFile.addDefault(GetDataDirectory("Date"), plugin.config.getInt("Defaults.NumberOfMainChests"));
		// plugin.eventsFile.addDefault(GetDataDirectory("Date"), plugin.config.getInt("Defaults.NumberOfExtraChests"));
		// plugin.eventsFile.addDefault(GetDataDirectory("Date"), plugin.config.getBoolean("Defaults.Secret"));
		// plugin.eventsFile.addDefault(GetDataDirectory("Date"), plugin.config.getBoolean("Defaults.ForceManualStart"));
		// plugin.eventsFile.addDefault(GetDataDirectory("Date"), plugin.config.getInt("Defaults.TimeOfReminder"));
		// plugin.eventsFile.addDefault(GetDataDirectory("Date"), plugin.config.getString("Defaults.ReminderMessage"));
		// plugin.eventsFile.addDefault(GetDataDirectory("Date"), plugin.config.getString("Defaults.EventMessage"));

		// try {
		// 	plugin.eventsFile.save(plugin.tempEventsFile);
		// }
		// catch(IOException e) {
		// 	e.printStackTrace();
		// }
		// this.Date = plugin.config.getString("Defaults.Date");
		// this.Time = plugin.config.getString("Defaults.Time");
		// this.MainLootTable = plugin.config.getString("Defaults.MainLootTable");
		// this.ExtraLootTable = plugin.config.getString("Defaults.ExtraLootTable");
		// this.NumberOfMainChests = plugin.config.getInt("Defaults.NumberOfMainChests");
		// this.NumberOfExtraChests = plugin.config.getInt("Defaults.NumberOfExtraChests");
		// this.Secret = plugin.config.getBoolean("Defaults.Secret");
		// this.ForceManualStart = plugin.config.getBoolean("Defaults.ForceManualStart");
		// this.TimeOfReminder = plugin.config.getInt("Defaults.TimeOfReminder");
		// this.ReminderMessage = plugin.config.getString("Defaults.Time");
		// this.EventMessage = plugin.config.getString("Defaults.Time");

		// plugin.saveResource(Name).
	}
}
