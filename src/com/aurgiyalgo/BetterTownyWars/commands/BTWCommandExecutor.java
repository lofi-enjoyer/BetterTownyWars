package com.aurgiyalgo.BetterTownyWars.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.aurgiyalgo.BetterTownyWars.BetterTownyWars;
import com.aurgiyalgo.BetterTownyWars.wars.War;
import com.aurgiyalgo.BetterTownyWars.wars.WarType;

public class BTWCommandExecutor implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdString, String[] args) {
		if (args.length == 0) {
			showHelp(sender);
			return true;
		}
		switch (args[0]) {
		case "help":
			showHelp(sender);
			break;
		case "declare":
			if (args.length < 3) {
				sender.sendMessage(BetterTownyWars.getInstance().getLanguageHandler().getMessage("not-enough-arguments"));
				return true;
			}
			WarType warType = WarType.getWarType(args[1]);
			if (warType == null) {
				sender.sendMessage(ChatColor.RED + "Invalid war type!");
				return true;
			}
			try {
				Method method = warType.getClassType().getMethod("declareCommandHandler", CommandSender.class, Command.class, String.class, String[].class);
				method.invoke(null, sender, cmd, cmdString, args);
			} catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
				System.err.println("Cause:");
				e.getCause().printStackTrace();
			}
			break;
		case "finish":
			if (args.length < 3) {
				sender.sendMessage(BetterTownyWars.getInstance().getLanguageHandler().getMessage("not-enough-arguments"));
				return true;
			}
			WarType warType2 = WarType.getWarType(args[1]);
			if (warType2 == null) {
				sender.sendMessage(ChatColor.RED + "Invalid war type!");
				return true;
			}
			try {
				Method method = warType2.getClassType().getMethod("finishCommandHandler", CommandSender.class, Command.class, String.class, String[].class);
				method.invoke(null, sender, cmd, cmdString, args);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case "list":
			listActiveWars(sender);
			break;
//		case "neutral":
//			toggleNationNeutrality(sender);
//			break;
		default:
			sender.sendMessage(ChatColor.RED + "Type a correct argument (/btw help)");
			break;
		}
		return true;
	}
	
	private void showHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
				"&3------ &bBetterTownyWars &3------&r\n"
				+ "\n"
				+ "&7&oSubcommands:&r\n"
				+ "&f  - help &8- &7Shows this message&7&r\n"
				+ "&f  - declare [nation] &8- &7Declare a war to a nation&r\n"
				+ "&f  - finish [nation] &8- &8Finish a war&r\n"
				+ "&f  - neutral &8- &7Toggle neutrality of your nation&r\n"
				+ "&f  - list &8- &7List of active wars"));
	}
	
//	private void toggleNationNeutrality(CommandSender sender) {
//		if (!(sender instanceof Player)) {
//			sender.sendMessage(ChatColor.RED + "This command can only be used by a player!");
//			return;
//		}
//		if (!sender.hasPermission(BetterTownyWars.Permissions.TOGGLE_NEUTRALITY_PERMISSION)) {
//			sender.sendMessage(BetterTownyWars.getInstance().getLanguageHandler().getFormattedMessage("no-permission"));
//			return;
//		}
//		Player p = (Player) sender;
//		Nation playerNation;
//		try {
//			playerNation = TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown().getNation();
//		} catch (NotRegisteredException e) {
//			sender.sendMessage(BetterTownyWars.getInstance().getLanguageHandler().getFormattedMessage("not-in-a-nation"));
//			return;
//		}
//		try {
//			if (playerNation.getAccount().getHoldingBalance() < Configuration.NEUTRALITY_COST) {
//				sender.sendMessage(ChatColor.RED + "Your nation can not afford being neutral");
//				return;
//			}
//		} catch (EconomyException e) {
//			return;
//		}
//		//TODO Comprobar si la nación tiene en su cuenta el coste de neutralidad
//		if (BetterTownyWars.getInstance().getNeutralityManager().isNeutral(playerNation.uuid)) {
//			BetterTownyWars.getInstance().getNeutralityManager().removeNeutral(playerNation.uuid);
//			TownyMessaging.sendTitleMessageToNation(playerNation, "", ChatColor.RED + "Your nation is no longer neutral");
//		} else {
//			BetterTownyWars.getInstance().getNeutralityManager().addNeutral(playerNation.uuid);
//			TownyMessaging.sendTitleMessageToNation(playerNation, "", ChatColor.GREEN + "Your nation is now neutral");
//		}
//		try {
//			playerNation.getAccount().collect(Configuration.NEUTRALITY_COST, "Neutrality cost");
//		} catch (EconomyException e) {}
//	}
	
	private void listActiveWars(CommandSender sender) {
		List<War> allWars = BetterTownyWars.getInstance().getWarManager().getAllWars();
		if (allWars.size() <= 0) {
			sender.sendMessage(ChatColor.RED + "There's no active wars");
			return;
		}
		StringBuilder builder = new StringBuilder();
		builder.append(ChatColor.GREEN + "" + ChatColor.UNDERLINE + "Active wars Now" + ChatColor.RESET + "\n\n");
		for (War war : allWars) {
			builder.append(" - ");
			builder.append(war.getMemberName(war.getMembers().get(0)));
			for (int i = 1; i < war.getMembers().size(); i++) {
				builder.append(", ");
				builder.append(war.getMemberName(war.getMembers().get(i)));
			}
			builder.append(" (" + war.getType().getFormattedName() + ")\n");
		}
		sender.sendMessage(builder.toString());
	}

}
