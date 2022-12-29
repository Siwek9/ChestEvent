package com.siwek9.main;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// import java.io.File;
// import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
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
	// private final static int[] eventRemindersTime = {120, 60, 30, 15, 10, 5, 4, 3, 2, 1};

	String Name;
	LocalDateTime dateTime;

	// 0 - listener just start working
	// -1 - listener was working
	// 1 - the time for event is happening
	byte eventWasBefore = 0;

	boolean isStarted = false;
	boolean isBedrockPlaced = false;
	Location[] chestsLocation;

	
	ChestEventFile(String Name) {
		this.Name = Name;

		for (TokenType token : eventVariables) {
			plugin.events.set(GetDataDirectory(token.Name), plugin.config.get("Defaults." + token.Name));
		}
		saveEventsFile();

		
		dateTime = GetDateTimeFromStrings(plugin.events.getString(GetDataDirectory("Date")), plugin.events.getString(GetDataDirectory("Time")));

		if (dateTime.isEqual(LocalDateTime.now())) {
			eventWasBefore = -1;
		}
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

		if (dateTime.isEqual(LocalDateTime.now())) {
			eventWasBefore = -1;
		}

		if (plugin.events.contains(GetDataDirectory("isStarted"))) {
			// messageToAllPlayers("siema");
			isStarted = plugin.events.getBoolean(GetDataDirectory("isStarted"));
			if (!plugin.events.contains(GetDataDirectory("isBedrockPlaced"))) {
				startEventTimer();
			}
			else {
				isBedrockPlaced = plugin.events.getBoolean(GetDataDirectory("isBedrockPlaced"));
				var locationStrings = plugin.events.getStringList(GetDataDirectory("chestsLocation"));
				chestsLocation = new Location[locationStrings.size()];
	
				for (int i = 0; i < locationStrings.size(); i++) {
					String[] coord = locationStrings.get(i).split(" ");
					// messageToAllPlayers(coord[0] + "/" + coord[1] + "/" + coord[2]);
					chestsLocation[i] = new Location(Bukkit.getServer().getWorld("world"), Double.parseDouble(coord[0]), Double.parseDouble(coord[1]), Double.parseDouble(coord[2]));
				}

				if (isBedrockPlaced) {
					startBedrockTimer();
				}
				else {
					startChestListener();
				}
			}
		}
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
			else {
				// FIXME nie używaj "now" w event'ach jak na razie
				plugin.events.set(GetDataDirectory(token.Name), plugin.config.get("Defaults." + token.Name));
			}
		}
		saveEventsFile();

		
		dateTime = GetDateTimeFromStrings(plugin.events.getString(GetDataDirectory("Date")), plugin.events.getString(GetDataDirectory("Time")));

		if (dateTime.isEqual(LocalDateTime.now())) {
			eventWasBefore = -1;
		}
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
		
		if (dateTime.isEqual(LocalDateTime.now())) {
			eventWasBefore = -1;
		}
	}
	
	private void startChestListener() {
		chestOpenListener = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				int numberOfChestDeleted = 0;
				int numberOfChests = plugin.events.getInt(GetDataDirectory("NumberOfMainChests")) + plugin.events.getInt(GetDataDirectory("NumberOfExtraChests"));

				for (int chestIndex = 0; chestIndex < chestsLocation.length; chestIndex++) {
					if (chestsLocation[chestIndex] == null) {
						numberOfChestDeleted++;
						continue;
					}

					if (chestsLocation[chestIndex].getBlock().getType() != Material.CHEST) {
						if (chestIndex + 1 <= plugin.events.getInt(GetDataDirectory("NumberOfMainChests"))) {
							messageToAllPlayers("§9Skrzynia na kordach §6" + (int)chestsLocation[chestIndex].getX() + " " + (int)chestsLocation[chestIndex].getY() + " " + (int)chestsLocation[chestIndex].getZ() + "§9 została otworzona! Niech zdobywcy itemów długo one służą.");
						}
						else {
							messageToAllPlayers("§9Skrzynia na kordach §a" + (int)chestsLocation[chestIndex].getX() + " " + (int)chestsLocation[chestIndex].getY() + " " + (int)chestsLocation[chestIndex].getZ() + "§9 została otworzona! Niech zdobywcy itemów długo one służą.");
						}
						soundToAllPlayers(Sound.ENTITY_LIGHTNING_BOLT_THUNDER);
						chestsLocation[chestIndex] = null;
						plugin.events.set(GetDataDirectory("chestsLocation"), locationsToString(chestsLocation));
						saveEventsFile();
					}
					else {
						Chest tempChest = (Chest) chestsLocation[chestIndex].getBlock().getState();
						if (tempChest.getLootTable() == null) {
							if (chestIndex + 1 <= plugin.events.getInt(GetDataDirectory("NumberOfMainChests"))) {
								messageToAllPlayers("§9Skrzynia na kordach §6" + (int)chestsLocation[chestIndex].getX() + " " + (int)chestsLocation[chestIndex].getY() + " " + (int)chestsLocation[chestIndex].getZ() + "§9 została otworzona! Niech zdobywcy itemów długo one służą.");
							}
							else {
								messageToAllPlayers("§9Skrzynia na kordach §a" + (int)chestsLocation[chestIndex].getX() + " " + (int)chestsLocation[chestIndex].getY() + " " + (int)chestsLocation[chestIndex].getZ() + "§9 została otworzona! Niech zdobywcy itemów długo one służą.");
							}
							soundToAllPlayers(Sound.ENTITY_LIGHTNING_BOLT_THUNDER);
							chestsLocation[chestIndex] = null;
							plugin.events.set(GetDataDirectory("chestsLocation"), locationsToString(chestsLocation));
							saveEventsFile();
						}
					}
					if (chestsLocation[chestIndex] == null) {
						numberOfChestDeleted++;
					}
				}

				if (numberOfChestDeleted >= numberOfChests) {
					messageToAllPlayers("§6Została otworzona ostatnia skrzynia!\nWydarzenie §C" + Name + "§6 uważam za skończone");
					soundToAllPlayers(Sound.UI_TOAST_CHALLENGE_COMPLETE);
					plugin.events.set(Name, null);
					saveEventsFile();
					isStarted = false;
					plugin.deleteNotUsedEvents();
					Bukkit.getServer().getScheduler().cancelTask(chestOpenListener);
				}
			}
		}, 0, 20);
	}

	private void startBedrockTimer() {
		timeToDestroyBedrock = plugin.events.getInt(GetDataDirectory("TimeOfLock"));
		bedrockTimer = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				if (timeToDestroyBedrock == 0) {
					createChests();
					isBedrockPlaced = false;
					plugin.events.set(GetDataDirectory("isBedrockPlaced"), isBedrockPlaced);
					saveEventsFile();
					messageToAllPlayers("§9Bedrock§5 wokół skrzyń zniknął\nUmrzyjcie za itemy lub żyjcie w biedzie...");
					soundToAllPlayers(Sound.ENTITY_LIGHTNING_BOLT_IMPACT);
					Bukkit.getServer().getScheduler().cancelTask(bedrockTimer);
					startChestListener();
					return;
				}
				if (timeToDestroyBedrock % 60 == 0) {
					messageToAllPlayers("§9Bedrock§5 zniknie za §9" + timeToDestroyBedrock + "§5 sekund");
					soundToAllPlayers(Sound.UI_BUTTON_CLICK);
				}
				timeToDestroyBedrock--;
			}
		}, 0, 20);
	}

	private void startEventTimer() {
		setTimer = 10;
		timerStart = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				if (setTimer == 0) {
					chestsLocation = createChestsLocation();
					plugin.events.set(GetDataDirectory("chestsLocation"), locationsToString(chestsLocation));
					saveEventsFile();
					messageToAllPlayers("§2§lWydarzenie §c\"" + Name + "\"§2§l właśnie się zaczęło\n" +
										"§r§9Kordy skrzynek:\n");
					soundToAllPlayers(Sound.ENTITY_ZOMBIE_VILLAGER_CURE);

					int chestIndex = 1;
					for (String location : locationsToString(chestsLocation)) {
						if (chestIndex <= plugin.events.getInt(GetDataDirectory("NumberOfMainChests"))) {
							messageToAllPlayers("§6§o" + location);
						}
						else {
							messageToAllPlayers("§a§o" + location);
						}
						chestIndex++;
					}
						// TODO dodaj ze jak jest bedrock na 0 w ustawieniach to nie stawiaj go w ogóle
					createBedrock();
					plugin.events.set(GetDataDirectory("isBedrockPlaced"), isBedrockPlaced);
					saveEventsFile();
					startBedrockTimer();

					Bukkit.getServer().getScheduler().cancelTask(timerStart);
					return;
				}
				messageToAllPlayers("§eWydarzenie §l§c" + Name + "§r§e rozpocznie się za " + setTimer);
				setTimer--;
			}

		}, 0, 20);
	}
	
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d.MM.yyyy");

	private int setTimer;

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

	int timerStart;
	int bedrockTimer;
	int chestOpenListener;
	int timeToDestroyBedrock;

	public void start() {
		start(false);
	}

	public void start(boolean ManualStart) {
		if (plugin.events.getBoolean(GetDataDirectory("ForceManualStart")) == true && ManualStart == false) {
			sendStartMessage();
		}
		else {
			// System.out.println("siema");
			isStarted = true;
			plugin.events.set(GetDataDirectory("isStarted"), isStarted);
			saveEventsFile();
			startEventTimer();
		}
	}

	protected void soundToAllPlayers(Sound soundToPlay) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.playSound(player.getLocation(), soundToPlay, SoundCategory.MASTER, 1, 1);
		}
	}

	private String[] locationsToString(Location[] chestsLocation) {
		List<String> arrayToReturn = new ArrayList<>();
		for (int i = 0; i < chestsLocation.length; i++) {
			if (chestsLocation[i] == null) {
				// i--;
				continue;
			}
			arrayToReturn.add((int)chestsLocation[i].getX() + " " + (int)chestsLocation[i].getY() + " " + (int)chestsLocation[i].getZ());
		}

		String[] toReturn = new String[arrayToReturn.size()];
		for (int i = 0; i < toReturn.length; i++) {
			toReturn[i] = arrayToReturn.get(i);
		}

		return toReturn;
	}
	
	private void messageToAllPlayers(String message) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(message);
		}
	}

	// private void messageToAllOps(String message) {
	// 	for (Player player : Bukkit.getOnlinePlayers()) {
	// 		if (player.isOp()) {
	// 			player.sendMessage(message);
	// 		}
	// 	}
	// }

	void createChests() {
		int numberOfChest = 1;
		isBedrockPlaced = false;
		for (Location location : chestsLocation) {
			location.clone().add(1,0,0).getBlock().setType(Material.AIR);
			location.clone().add(-1,0,0).getBlock().setType(Material.AIR);
			location.clone().add(0,1,0).getBlock().setType(Material.AIR);
			location.clone().add(0,-1,0).getBlock().setType(Material.AIR);
			location.clone().add(0,0,1).getBlock().setType(Material.AIR);
			location.clone().add(0,0,-1).getBlock().setType(Material.AIR);	
			Block chestBlock = location.getBlock();
			chestBlock.setType(Material.CHEST);
			if (chestBlock.getState() instanceof Chest) {
				Chest chest = (Chest) chestBlock.getState();
				if (numberOfChest <= plugin.events.getInt(GetDataDirectory("NumberOfMainChests"))) {
					chest.setLootTable(Bukkit.getServer().getLootTable(NamespacedKey.minecraft("chests/" + plugin.events.getString(GetDataDirectory("MainLootTable")))));
					// chest.setLootTable(LootTables.SIMPLE_DUNGEON.getLootTable());
				}
				else {
					chest.setLootTable(Bukkit.getServer().getLootTable(NamespacedKey.minecraft("chests/" + plugin.events.getString(GetDataDirectory("ExtraLootTable")))));
					// chest.setLootTable(LootTables.SIMPLE_DUNGEON.getLootTable());
				}
				chest.update();
			}
			numberOfChest++;
		}
	}

	void createBedrock() {
		for (Location location : chestsLocation) {
			location.clone().add(1,0,0).getBlock().setType(Material.BEDROCK);
			location.clone().add(-1,0,0).getBlock().setType(Material.BEDROCK);
			location.clone().add(0,1,0).getBlock().setType(Material.BEDROCK);
			location.clone().add(0,-1,0).getBlock().setType(Material.BEDROCK);
			location.clone().add(0,0,1).getBlock().setType(Material.BEDROCK);
			location.clone().add(0,0,-1).getBlock().setType(Material.BEDROCK);	
		}
		isBedrockPlaced = true;
	}

	Location[] createChestsLocation() {
		int mainChestsNumber = plugin.events.getInt(GetDataDirectory("NumberOfMainChests"));
		int extraChestsNumber = plugin.events.getInt(GetDataDirectory("NumberOfExtraChests"));
		Random random = new Random();
		int xCoord, yCoord, zCoord;
		int EventRange = plugin.config.getInt("RadiusOfEvent");

		Location[] toReturn = new Location[mainChestsNumber + extraChestsNumber];
		
		for (int i = 0; i < mainChestsNumber; i++) {
			Boolean isChestOk = false;
			while(true) {
				xCoord = random.nextInt(-EventRange , EventRange);
				zCoord = random.nextInt(-EventRange , EventRange);
				for (int y = 317; y >= -63; y--) {
					if (!(getLocation(xCoord, y, zCoord).getBlock().getType()).equals(Material.AIR)) {
						yCoord = y + 2;
						if (!getLocation(xCoord + 1, yCoord, zCoord).getBlock().getType().equals(Material.AIR) ||
							!getLocation(xCoord - 1, yCoord, zCoord).getBlock().getType().equals(Material.AIR) ||
							!getLocation(xCoord, yCoord, zCoord + 1).getBlock().getType().equals(Material.AIR) ||
							!getLocation(xCoord, yCoord, zCoord - 1).getBlock().getType().equals(Material.AIR)) {
								break;
							}
						
							toReturn[i] = new Location(Bukkit.getServer().getWorld("world"), xCoord, yCoord, zCoord);
							isChestOk = true;
						break;
					}
				}
				if (isChestOk) {
					break;
				}
			}
		}

		for (int i = mainChestsNumber; i < mainChestsNumber + extraChestsNumber; i++) {
			Boolean isChestOk = false;
			while(true) {
				xCoord = random.nextInt(-EventRange , EventRange);
				zCoord = random.nextInt(-EventRange , EventRange);
				for (int y = 317; y >= -63; y--) {
					if (!(getLocation(xCoord, y, zCoord).getBlock().getType()).equals(Material.AIR)) {
						yCoord = y + 2;
						if (!getLocation(xCoord + 1, yCoord, zCoord).getBlock().getType().equals(Material.AIR) ||
							!getLocation(xCoord - 1, yCoord, zCoord).getBlock().getType().equals(Material.AIR) ||
							!getLocation(xCoord, yCoord, zCoord + 1).getBlock().getType().equals(Material.AIR) ||
							!getLocation(xCoord, yCoord, zCoord - 1).getBlock().getType().equals(Material.AIR)) {
								break;
							}
						plugin.getLogger().info("Kordy: " + xCoord + " " + yCoord + " " + zCoord);
						toReturn[i] = new Location(Bukkit.getServer().getWorld("world"), xCoord, yCoord, zCoord);
						isChestOk = true;
						break;
					}
				}
				if (isChestOk) {
					break;
				}
			}
		}

		return toReturn;
	}

	Location getLocation(int x, int y, int z) {
		return new Location(Bukkit.getServer().getWorld("world"), x, y, z);
	}

	public void sendStartMessage() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.isOp()) {
				player.sendMessage("§eThe event §l§c" + Name + "§r§e should start now!\nType §2§o/event start " + Name + "§r§e to start it.");
			}
		}
		plugin.getLogger().info("The event " + Name + " should start now!");
		plugin.getLogger().info("Type \"event start " + Name + "\" to start it.");
		
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
