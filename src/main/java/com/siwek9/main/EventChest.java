package com.siwek9.main;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.loot.LootTable;
import org.bukkit.scoreboard.Team;

public class EventChest {
    private Location chestLocation;
    private boolean bedrockPlaced;
    private int timeToDestroyBedrockInSeconds;
    private Location[] bedrockLocation;
    private LootTable chestLootTable;
    private String name;
    
    public EventChest(String name, Location chestLocation, int secondsToDestroyBedrock, LootTable chestLootTable) {
        this.chestLocation = chestLocation;
        this.bedrockLocation = new Location[] {
            chestLocation.clone().add(1,0,0),
			chestLocation.clone().add( -1,0,0),
			chestLocation.clone().add(0,1,0),
			chestLocation.clone().add(0, -1,0),
			chestLocation.clone().add(0,0,1),
			chestLocation.clone().add(0,0, -1)
        };

        this.name = name;
        this.timeToDestroyBedrockInSeconds = secondsToDestroyBedrock;
        this.chestLootTable = chestLootTable;

        for(Location bedrockPosition : bedrockLocation) {
            if (!bedrockPosition.getBlock().getType().equals(Material.BEDROCK)) {
                bedrockPlaced = false;
                return;
            }
        }
        bedrockPlaced = true;
    }

    public String getName() {
        return name;
    }

    public Location getChestLocation() {
        return chestLocation;
    }

    public boolean isBedrockPlaced() {
        return bedrockPlaced;
    }

    public int getTimeToDestroyBedrockInSeconds() {
        return timeToDestroyBedrockInSeconds;
    }

    public LootTable getChestLootTable() {
        return chestLootTable;
    }

    public void placeBedrock() {
        for (Location bedrockPosition : bedrockLocation) {
            bedrockPosition.getBlock().setType(Material.BEDROCK);
        }
        this.bedrockPlaced = true;
    }
    
    public void destroyBedrock() {
        for (Location bedrockPosition : bedrockLocation) {
            bedrockPosition.getBlock().setType(Material.AIR);
        }
        this.bedrockPlaced = false;
    }
    
    public void placeChest() {
        Block chestBlock = chestLocation.getBlock();
        chestBlock.setType(Material.CHEST);
        if (chestBlock.getState() instanceof Chest) {
            Chest chest = (Chest)chestBlock.getState();
            chest.setLootTable(chestLootTable);
            chest.update();
        }
    }
    // Bukkit.getServer().getLootTable(NamespacedKey.minecraft("chests/" + plugin.events.getString(GetDataDirectory("MainLootTable"))))
    
    public boolean isChestOpen() {
        return (!bedrockPlaced && this.chestLocation.getBlock().getType() != Material.CHEST) || ((Chest)chestLocation.getBlock().getState()).getLootTable() == null;
    }
    
    public boolean areTeamsAround(int quantity, int radius) {
        Set<Team> teamsAroundChest = new HashSet<Team>();
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			Location playerLocation = player.getLocation();
			if (playerLocation.getWorld().getEnvironment().equals(chestLocation.getWorld().getEnvironment()) && playerLocation.getX() > chestLocation.getX() - radius && playerLocation.getX() < chestLocation.getX() + radius &&
			playerLocation.getY() > chestLocation.getY() - radius/3 && playerLocation.getY() < chestLocation.getY() + radius/3 &&
			playerLocation.getZ() > chestLocation.getZ() - radius && playerLocation.getZ() < chestLocation.getZ() + radius) {
				teamsAroundChest.add(player.getScoreboard().getEntryTeam(player.getName()));
			}
		}
        return teamsAroundChest.size() >= quantity;
	}

    public void setTimeToDestroyBedrockInSeconds(int i) {
        timeToDestroyBedrockInSeconds = i;
    }
}