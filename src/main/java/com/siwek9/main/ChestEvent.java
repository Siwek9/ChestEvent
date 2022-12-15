package com.siwek9.main;

import org.bukkit.plugin.java.JavaPlugin;

public final class ChestEvent extends JavaPlugin {
	@Override
	public void onEnable() {
		getLogger().info("Witaj, świecie!");
		this.getCommand("event").setExecutor(new ChestEventTabExecutor(this));
	}
}