package com.siwek9.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.loot.LootTable;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.JsonObject;

public final class ChestEvent extends JavaPlugin {
	
	public FileConfiguration config;
	public File eventsFile;
	public String lootTablesDirectory = "LootTables";
	public List<String> allLootTables;
	JsonObject LootTableFileContent;

	LootTable betterChest;
	LootTable worseChest;


	@Override
	public void onEnable() {
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}

		// LootTable lootTable2 = getServer().getLootTable(null);
		// lootTable2.load(new File("jol"));

		config = this.getConfig();

		eventsFile = new File(getDataFolder(), "event.yml");
		try {
			if (eventsFile.createNewFile()) {
				getLogger().info("Create \"event.yml\" .");
			}
		}
		catch(IOException e) {
			System.out.println("An error occurred");
			e.printStackTrace();
		}

		if (!new File(getDataFolder(), lootTablesDirectory).exists()) {
			new File(getDataFolder(), lootTablesDirectory).mkdir();
			getLogger().info("Create \"LootTables\" directory where you should put your custom LootTables.");
			getLogger().info("You can create custom LootTable in this generator: https://misode.github.io/loot-table/");
		}
		
		setDefaultConfig();
		allLootTables = getLootTablesFromDirectory(lootTablesDirectory);

		
		
		this.getCommand("event").setExecutor(new ChestEventTabExecutor(this));
	}

	JsonObject ReadLootTableFile(String lootTableName) {
		JsonObject toReturn = new JsonObject();

		return toReturn;
	}


	List<String> getLootTablesFromDirectory(String directoryName) {
		List<String> ListOfLootTables = new ArrayList<>();
		
		File folder = new File(getDataFolder(), directoryName);
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
			String nameOfFile = file.getName();
			nameOfFile = nameOfFile.substring(0, nameOfFile.lastIndexOf("."));
			ListOfLootTables.add(nameOfFile);
		}
		
		return ListOfLootTables;
	}
	
	void setDefaultConfig() {
		if(!config.contains("Default_AlwaysManualStartEvent", false)) {
			getLogger().info("Create \"config.yml\".");
		}
		config.options().header("date pattern \"dd.mm.yyyy\"\n" +
								"time pattern \"hh:mm\"\n" +
								"place your loot tables in \"loottable\" folder\n" +
								"when Secret value is set to true users will not get any message about the event\n" +
								"when ForceManualStart is set to true, the event should start send message to all op players, asking them to start the event\n" +
								"TimeOfReminder is in a minutes (set value to 0 to turn off the reminder\n");

		config.addDefault("Defaults.Date", "now");
		config.addDefault("Defaults.Time", "now");
		config.addDefault("Defaults.MainLootTable", "default_main_loot_table");
		config.addDefault("Defaults.ExtraLootTable", "default_extra_loot_table");
		config.addDefault("Defaults.NumberOfMainChests", 1);
		config.addDefault("Defaults.NumberOfExtraChests", 2);
		config.addDefault("Defaults.Secret", false);
		config.addDefault("Defaults.ForceManualStart", false);
		config.addDefault("Defaults.TimeOfReminder", 30);
		config.addDefault("Defaults.ReminderMessage", "The Event {EventName} will start in {TimeOfReminder} minutes");
		config.addDefault("Defaults.EventMessage", "The Event {EventName} starts now! Chest are generated at coords {ChestsCords}. Good luck getting them ;)");
		config.options().copyDefaults(true);
		saveConfig();
	}
}