package com.siwek9.main;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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
			List<String> temp = Stream.concat(Arrays.asList(opCommands).stream(), Arrays.asList(playerCommands).stream()).toList();
			commandsToChoose = (String[])temp.toArray();
		}
		else {
			commandsToChoose = playerCommands.clone();
		}
		sender.sendMessage(commandsToChoose);
		//Arrays.sort(commandsToChoose);
 		// sender.sendMessage("witam");
		// if (args.length == 1)
		// 	return Arrays.asList(commandsToChoose);
		List<String> toReturn = Arrays.asList("siema", "witam");
		return toReturn;
	}
}
