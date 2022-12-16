package com.siwek9.main;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChestEvent extends JavaPlugin {

	public FileConfiguration config;
	public File eventsFile;

	@Override
	public void onEnable() {
		getLogger().info("Witaj, świecie!");

		config = this.getConfig();
		eventsFile = new File(getDataFolder(), "events.yml");
		
		config.addDefault("youAreAwesome", true);
        config.options().copyDefaults(true);
        saveConfig();
		
		this.getCommand("event").setExecutor(new ChestEventTabExecutor(this));
	}
}