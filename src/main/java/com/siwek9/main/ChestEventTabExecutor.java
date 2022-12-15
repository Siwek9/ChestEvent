package com.siwek9.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

public class ChestEventTabExecutor implements TabExecutor {
	private final ChestEvent plugin;

	public ChestEventTabExecutor(ChestEvent plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		plugin.getLogger().info("Użyłeś komendy, wiesz? Fajnie co nie? Nic ona nie robi ;)");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		String[] opCommands = {"create", "edit", "remove", "start"};
		String[] playerCommands = {"info"};
		String[] commandsToChoose;

		if (sender.isOp()) {
			commandsToChoose = StringConcat(opCommands, playerCommands);
		}
		else {
			commandsToChoose = playerCommands.clone();
		}
		Arrays.sort(commandsToChoose);
		if (args.length == 1) {
			return OnlyElementsThatStartsWith(Arrays.asList(commandsToChoose), args[0]);
		}
		// List<String> toReturn = Arrays.asList("siema", "witam");
		return null;
	}

	String[] StringConcat(String[] firstString, String[] secondString) {
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
