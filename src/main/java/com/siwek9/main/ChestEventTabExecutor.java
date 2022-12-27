package com.siwek9.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
// import org.bukkit.command.TabExecutor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ChestEventTabExecutor implements CommandExecutor {

	ChestEventFile allEvents;
	private final ChestEvent plugin = (ChestEvent)Bukkit.getServer().getPluginManager().getPlugin("ChestEvent");

	public ChestEventTabExecutor(ChestEvent plugin) {
		// this.plugin = plugin;
	}
	
	// /event create NowyEvent {Date:"19.12.2022",Time:"19:00",MainLootTable:"dobry_loot_table",ExtraLootTable:"custom_loot_table"}

	String[] opCommands = {"create", "edit", "remove", "start"};
	String[] playerCommands = {"info"};
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!cmd.getName().equals("event")) return false;
		
		String[] commandsToChoose;
		// create a array with first arguments that sender can use 
		if (sender.isOp()) {
			commandsToChoose = ArraysConcat(opCommands, playerCommands);
		}
		else {
			commandsToChoose = playerCommands.clone();
		}
		
		// command should always get a sub command
		if (args.length < 1) {
			sender.sendMessage("§cUnknown or incomplete command, see below for error\n§7" + label + "§c§o<--[HERE]");
			return true;
		}
		
		String subCommand = args[0];
		
		// check if user typed valid subcommand
		if (!Arrays.asList(commandsToChoose).contains(subCommand)) {
			// different info for player when tries to use op command.
			if (Arrays.asList(opCommands).contains(subCommand)) {
				sender.sendMessage("§cYou have no permission to use this command!");
				return true;
			}
			else {
				String allArguments = new String();
				for (String arg : args) {
					allArguments += arg + " ";
				} 
				allArguments = allArguments.substring(0, allArguments.length() - 1);
				sender.sendMessage("§cUnknown or incomplete command, see below for error\n§7" + label + " §c§n" + allArguments + "§r§c§o<--[HERE]");
				return true;
			}
		}

		// command should always get a event name
		if (args.length < 2) {
			sender.sendMessage("§cUnknown or incomplete command, see below for error\n§7" + printEndOfString(label + " " + args[0], 10) + "§c§o<--[HERE]");
			return true;
		}

		String eventName = args[1];

		
		if (subCommand.equals("create")) {
			
			Pattern eventNamePattern = Pattern.compile("^[a-z|A-Z|_]+[a-z|A-Z|0-9|_]*");
			
			
			
			if (!eventNamePattern.matcher(eventName).matches()) {
				String allArguments = new String();
				for (String arg : Arrays.asList(args).subList(1, args.length)) {
					allArguments += arg + " ";
				} 
				sender.sendMessage("§cUnknown or incomplete command, see below for error\n§7" + printEndOfString(label + " " + args[0], 10) + " §c§n" + allArguments + "§r§c§o<--[HERE]");
			}
			
			if (args.length < 3) {
				if (plugin.events.contains(eventName)) {
					sender.sendMessage("§cEvent with name \"" + eventName + "\" already exists. Please try another name for your event.");
					return true;
				}
				plugin.listOfEvents.add(new ChestEventFile(eventName));
			}
			else {
				
				JsonParser jsonParser = new JsonParser();
				JsonObject eventData;
				try {
					// eventData = new JsonObject();
					eventData = (JsonObject) jsonParser.parse(args[2]);
				}
				catch(Exception e) {
					// e.printStackTrace();
					String allArguments = new String();
					for (String arg : Arrays.asList(args).subList(2, args.length)) {
						allArguments += arg + " ";
					}
					sender.sendMessage("§cUnknown or incomplete command, see below for error\n§7" + printEndOfString(label + " " + args[0] + " " + args[1], 10) + " §c§n" + allArguments + "§r§c§o<--[HERE]");
					return true;
				}

				if (args.length >= 4) {
					String allArguments = new String();
					for (String arg : Arrays.asList(args).subList(3, args.length)) {
						allArguments += arg + " ";
					} 
					sender.sendMessage("§cUnknown or incomplete command, see below for error\n§7" + printEndOfString(label + " " + args[0] + args[1], 10) + " §c§n" + allArguments + "§r§c§o<--[HERE]");
					return true;
				}

				if (plugin.events.contains(eventName)) {
					sender.sendMessage("§cEvent with name \"" + eventName + "\" already exists. Please try another name for your event.");
					return true;
				}
				plugin.listOfEvents.add(new ChestEventFile(eventName, eventData));
				return true;			

			}
		}
		else if (subCommand.equals("edit")) {
			Pattern eventNamePattern = Pattern.compile("^[a-z|A-Z|_]+[a-z|A-Z|0-9|_]*");
			if (!eventNamePattern.matcher(eventName).matches()) {
				String allArguments = new String();
				for (String arg : Arrays.asList(args).subList(1, args.length)) {
					allArguments += arg + " ";
				} 
				sender.sendMessage("§cUnknown or incomplete command, see below for error\n§7" + printEndOfString(label + " " + args[0], 10) + " §c§n" + allArguments + "§r§c§o<--[HERE]");
				return true;
			}

			if (!plugin.events.contains(eventName)) {
				sender.sendMessage("§cThere is no event with the name \"" + eventName + "\". You first need to create it.");
				return true;
			}

			if (args.length < 3) {
				sender.sendMessage("§cUnknown or incomplete command, see below for error\n§7" + printEndOfString(label + " " + args[0] + " " + args[1], 10) + "§r§c§o<--[HERE]");
				return true;
			}

			JsonParser jsonParser = new JsonParser();
				JsonObject eventData;
				try {
					eventData = (JsonObject) jsonParser.parse(args[2]);
				}
				catch(Exception e) {
					e.printStackTrace();
					String allArguments = new String();
					for (String arg : Arrays.asList(args).subList(2, args.length)) {
						allArguments += arg + " ";
					}
					sender.sendMessage("§cUnknown or incomplete command, see below for error\n§7" + printEndOfString(label + " " + args[0] + " " + args[1], 10) + " §c§n" + allArguments + "§r§c§o<--[HERE]");
					return true;
				}

				if (args.length >= 4) {
					String allArguments = new String();
					for (String arg : Arrays.asList(args).subList(3, args.length)) {
						allArguments += arg + " ";
					} 
					sender.sendMessage("§cUnknown or incomplete command, see below for error\n§7" + printEndOfString(label + " " + args[0] + args[1], 10) + " §c§n" + allArguments + "§r§c§o<--[HERE]");
					return true;
				}
				for (int i = 0; i < plugin.listOfEvents.size(); i++) {
					if (plugin.listOfEvents.get(i).Name == eventName) {
						plugin.listOfEvents.set(i, new ChestEventFile(plugin.listOfEvents.get(i), eventData));
						return true;
					}
				}
				
				// for (ChestEventFile chestEventFile : plugin.listOfEvents) {
				// 	if (chestEventFile.Name == eventName) {
						
				// 	}
				// }
			
		}

		return true;
    }
	// @Override
	// public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
	// 	String[] commandsToChoose;

	// 	if (sender.isOp()) {
	// 		commandsToChoose = ArraysConcat(opCommands, playerCommands);
	// 	}
	// 	else {
	// 		commandsToChoose = playerCommands.clone();
	// 	}
	// 	Arrays.sort(commandsToChoose);
	// 	if (args.length == 1) {
	// 		return OnlyElementsThatStartsWith(Arrays.asList(commandsToChoose), args[0]);
	// 	}
	// 	// List<String> toReturn = Arrays.asList("siema", "witam");
	// 	return null;
	// }

	private String printEndOfString(String string, int numberOfChars) {
		if (string.length() <= numberOfChars) return string;
		else return "..." + string.substring(string.length() - numberOfChars);
	}

	String[] ArraysConcat(String[] firstString, String[] secondString) {
		String[] toReturn = new String[firstString.length + secondString.length];
		int arrayCursor = 0;
		for (String string : firstString) {
			toReturn[arrayCursor] = string;
			arrayCursor++;
		}
		for (String string : secondString) {
			toReturn[arrayCursor] = string;
			arrayCursor++;
		}
		
		return toReturn;
	}

	List<String> OnlyElementsThatStartsWith(List<String> wholeList, String startString) {
		List<String> toReturn = new ArrayList<String>();

		for (String oneString : wholeList) {
			if (oneString.startsWith(startString)) {
				toReturn.add(oneString);
			}
		}

		return toReturn;
	}
}
