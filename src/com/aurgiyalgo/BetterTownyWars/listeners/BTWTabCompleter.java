package com.aurgiyalgo.BetterTownyWars.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.aurgiyalgo.BetterTownyWars.wars.WarType;

public class BTWTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String stringCmd, String[] args) {
		if (args.length < 2) {
			return null;
		}
		switch (args[0].toLowerCase()) {
		case "declare":
			return executeDeclare(sender, cmd, stringCmd, args);
//		case "finish":
//			return executeFinish(sender, cmd, stringCmd, args);
		default:
			return null;
		}
	}
	
	private List<String> executeDeclare(CommandSender sender, Command cmd, String stringCmd, String[] args) {
		Set<String> subCommands = WarType.getWarTypes();
		
		if (args.length > 2) return null;
		
		List<String> cmdList = new ArrayList<String>();
		for (String s : subCommands) {
			if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
				cmdList.add(s);
			}
		}
		return cmdList;
	}
	
	private List<String> executeFinish(CommandSender sender, Command cmd, String stringCmd, String[] args) {
		return null;
	}

}
