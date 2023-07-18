package com.siwek9.main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChestEvent extends JavaPlugin {

	public FileConfiguration config;

	public String lootTablesFolderName = "LootTables";

	public YamlConfiguration events;
	public File eventsFile;

	List<ChestEventFile> listOfEvents;

	int ChestEventID;

	@Override
	public void onEnable() {
		if (!this.getDataFolder().exists()) {
			this.getDataFolder().mkdir();
		}
		
		config = this.getConfig();
		workWithConfig();
		
		if (!new File(getDataFolder(), lootTablesFolderName).exists()) {
			new File(getDataFolder(), lootTablesFolderName).mkdir();
			getLogger().info("Create \"LootTables\" directory where you should put your custom LootTables.");
			// getLogger().info("You can create custom LootTable in this generator: https://misode.github.io/loot-table/");
		}
		

		listOfEvents = new ArrayList<>();

		eventsFile = new File(this.getDataFolder(), "events.yml");
		try {
			eventsFile.createNewFile();
			events = YamlConfiguration.loadConfiguration(eventsFile);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		for (String eventFromFile : events.getKeys(false)) {
			listOfEvents.add(new ChestEventFile(eventFromFile, events.getConfigurationSection(eventFromFile)));
		}
	
		PluginCommand eventCommand = this.getCommand("event");
		eventCommand.setExecutor(new ChestEventTabExecutor(this));


		ChestEventID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				for (ChestEventFile chestEventFile : listOfEvents) {
					if (chestEventFile.isStarted == true) return;
					
					if (chestEventFile.dateTime.isBefore(LocalDateTime.now())) {

						if (chestEventFile.eventWasBefore == -1)
							chestEventFile.start();
						else if (chestEventFile.eventWasBefore == 0)
							chestEventFile.sendStartMessage();
						
						chestEventFile.eventWasBefore = 1;

					}
					else {
						chestEventFile.eventWasBefore = -1;
					} 
				}
			}
		}, 0, config.getInt("RefreshTime"));

		// if (CommodoreProvider.isSupported()) {
		// 	Commodore commodore = CommodoreProvider.getCommodore(this);
		// 	LiteralCommandNode<?> eventArguments;

		// 	try {
		// 		eventArguments = CommodoreFileReader.INSTANCE.parse(getResource("event.commodore"));
		// 		commodore.register(eventCommand, eventArguments);
		// 	}
		// 	catch (IOException e) {
		// 		System.out.println("time.commodore file not found.");
		// 	}

		// }
	}

	@Override
	public void onDisable() {
		Bukkit.getServer().getScheduler().cancelTask(ChestEventID);
	}

	// JsonObject ReadLootTableFile(String lootTableName) {
	// 	JsonObject toReturn = new JsonObject();

	// 	return toReturn;
	// }

	public void deleteNotUsedEvents() {
		for (int i = 0; i < listOfEvents.size(); i++) {
			if (!this.events.contains(listOfEvents.get(i).Name)) {
				listOfEvents.remove(i);
				i--;
			}
		}
		// for (ChestEventFile chestEventFile : listOfEvents) {
		// 	if (!this.events.contains(chestEventFile.Name))
		// 	list
		// }
	}


	// List<String> getLootTablesFromDirectory(String directoryName) {
	// 	List<String> ListOfLootTables = new ArrayList<>();
		
	// 	File folder = new File(getDataFolder(), directoryName);
	// 	File[] listOfFiles = folder.listFiles();

	// 	for (File file : listOfFiles) {
	// 		String nameOfFile = file.getName();
	// 		nameOfFile = nameOfFile.substring(0, nameOfFile.lastIndexOf("."));
	// 		ListOfLootTables.add(nameOfFile);
	// 	}
		
	// 	return ListOfLootTables;
	// }
	
	void workWithConfig() {

		YamlConfiguration defaultConfigConfiguration;
		InputStream is = getClass().getClassLoader().getResourceAsStream("default-config.yml");

		try (InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
			defaultConfigConfiguration = YamlConfiguration.loadConfiguration(streamReader);	
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return;
		}

		System.out.println(defaultConfigConfiguration.getString("DefaultOptions.Date"));

		if(!new File(getDataFolder(), "config.yml").exists()) {
			if (is == null) {
				getLogger().warning("Cannot Create config.yml");
				return;
			}

			// defaultConfigConfiguration
			// config.addDefaults(defaultConfigConfiguration);
			config.setDefaults(defaultConfigConfiguration);
			// String defaultConfigContent = ExtendedInputStream.convertToString(is);
			getLogger().info("Create \"config.yml\".");
		}
		else {
			if (is == null) {
				getLogger().warning("Cannot validate config.yml");
				return;
			}
		}
		// config.options().header("date pattern \"dd.mm.yyyy\"\n" +
		// 						"time pattern \"hh:mm\"\n" +
		// 						"place your loot tables in \"loottable\" folder\n" +
		// 						"when Secret value is set to true users will not get any message about the event\n" +
		// 						"when ForceManualStart is set to true, the event should start send message to all op players, asking them to start the event\n" +
		// 						"TimeOfReminder is in a minutes (set value to 0 to turn off the reminder)\n" +
		// 						"TimeOfLock is in a seconds (set value to 0 to turn off the reminder)\n");
		// config.addDefault("RadiusOfEvent", 1200);
		// config.addDefault("TicksPerRefresh", 100);
		// config.addDefault("Defaults.Date", "now");
		// config.addDefault("Defaults.Time", "now");
		// config.addDefault("Defaults.MainLootTable", "default_main_loot_table");
		// config.addDefault("Defaults.ExtraLootTable", "default_extra_loot_table");
		// config.addDefault("Defaults.NumberOfMainChests", 1);
		// config.addDefault("Defaults.NumberOfExtraChests", 2);
		// config.addDefault("Defaults.Secret", false);
		// config.addDefault("Defaults.ForceManualStart", false);
		// config.addDefault("Defaults.TimeOfReminder", 30);
		// config.addDefault("Defaults.TimeOfLock", 300);
		// config.addDefault("Defaults.ReminderMessage", "The Event {EventName} will start in {TimeOfReminder} minutes");
		// config.addDefault("Defaults.EventMessage", "The Event {EventName} starts now! Chest are generated at coords {ChestsCords}. Good luck getting them ;)");
		config.options().copyDefaults(true);
		saveConfig();
		// saveConfig();
		// saveDefaultConfig();
	}
}