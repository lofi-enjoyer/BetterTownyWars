package com.aurgiyalgo.BetterTownyWars.wars;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aurgiyalgo.BetterTownyWars.BetterTownyWars;
import com.aurgiyalgo.BetterTownyWars.BetterTownyWars.Configuration;
import com.google.gson.annotations.Expose;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.EconomyException;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;

public class TownVsTownWar extends War {

	@Expose
	private List<UUID> _deadPlayers = new ArrayList<UUID>();
	
	private List<Town> _towns = new ArrayList<Town>();

	public TownVsTownWar(String warType, long endTime) {
		super(warType, endTime);
	}
	
	@Override
	public void setupWar() {
		_towns = new ArrayList<Town>();
		for (UUID member : getMembers()) {
			try {
				Town n = TownyUniverse.getInstance().getDataSource().getTown(member);
				_towns.add(n);
			} catch (NotRegisteredException e) {
			}
		}
	}

	@Override
	protected void onWarDeclare() {
		for (Town t : _towns)
			TownyMessaging.sendTitleMessageToTown(t, "",
					BetterTownyWars.getInstance().getLanguageHandler().getMessage("war-started"));
	}

	@Override
	protected void onWarFinish() {
		Town winner = null;

		for (Town t : _towns) {
			int playersKilled = 0;
			for (UUID id : _deadPlayers) {
				if (t.hasResident(Bukkit.getOfflinePlayer(id).getName()))
					playersKilled++;
			}

			if (playersKilled >= BetterTownyWars.Configuration.NATION_VS_NATION_KILL_PERCENTAGE_TO_FINISH
					* t.getResidents().size()) {
				winner = t;
				break;
			}
		}

		for (Town t : _towns) {
			if (t.equals(winner))
				continue;
			TownyMessaging.sendTitleMessageToTown(t, "",
					BetterTownyWars.getInstance().getLanguageHandler().getMessage("war-finished-lose"));
			try {
				if (t.getAccount().canPayFromHoldings(BetterTownyWars.Configuration.LOSE_WAR_COST))
					t.getAccount().payTo(BetterTownyWars.Configuration.LOSE_WAR_COST, winner,
							BetterTownyWars.getInstance().getLanguageHandler().getMessage("war-spoils"));
			} catch (EconomyException e) {

			}
		}
		TownyMessaging.sendTitleMessageToTown(winner, "",
				BetterTownyWars.getInstance().getLanguageHandler().getMessage("war-finished-win"));
	}

	@Override
	protected void onPlayerKill(Player victim, Player killer) {

		if (_deadPlayers.contains(victim.getUniqueId()))
			return;

		Town victimTown = null;
		for (Town t : _towns) {
			victimTown = t;
			if (!victimTown.hasResident(victim.getName()))
				continue;
		}

		if (victimTown == null)
			return;

		Town killerTown;
		for (Town t : _towns) {
			killerTown = t;

			if (killerTown.getName().equals(victimTown.getName()))
				continue;

			if (!killerTown.hasResident(killer.getName()))
				continue;

			_deadPlayers.add(victim.getUniqueId());

			int playersKilled = 0;
			for (UUID id : _deadPlayers) {
				if (victimTown.hasResident(Bukkit.getOfflinePlayer(id).getName()))
					playersKilled++;
			}

			if (playersKilled >= BetterTownyWars.Configuration.NATION_VS_NATION_KILL_PERCENTAGE_TO_FINISH
					* victimTown.getResidents().size()) {
				BetterTownyWars.getInstance().getWarManager().finishWar(this);
			}

			return;
		}
	}

	@Override
	public void enablePvP() {
		for (Town t : _towns) {
			t.setAdminEnabledPVP(true);
		}
	}

	@Override
	public void disablePvP() {
		for (Town t : _towns) {
			t.setAdminEnabledPVP(false);
		}
	}

	@Override
	public String getMemberName(UUID member) {
		for (Town t : _towns) {
			if (t.getUuid().equals(member))
				return t.getName();
		}
		return null;
	}

	@Override
	protected void onMemberJoin(UUID member) {
		try {
			Town t = TownyUniverse.getInstance().getDataSource().getTown(member);
			_towns.add(t);
		} catch (NotRegisteredException e) {
		}
	}

	@Override
	protected void onMemberLeave(UUID member) {
		for (Town t : _towns) {
			if (t.getUuid().equals(member))
				_towns.remove(t);
		}
	}

	@Override
	protected void onWarPeace() {
		for (Town t : _towns) {
			TownyMessaging.sendTownMessage(t,
					BetterTownyWars.getInstance().getLanguageHandler().getMessage("war-ended-by-peace"));
		}
	}

	@Override
	protected void onMemberRequestPeace(UUID member) {
		Town townRequest = null;
		for (Town t : _towns) {
			if (t.getUuid().equals(member)) {
				townRequest = t;
				break;
			}
		}
		for (Town t : _towns) {
			if (!t.getUuid().equals(member))
				TownyMessaging.sendTownMessage(t, ChatColor.GREEN + townRequest.getFormattedName()
						+ BetterTownyWars.getInstance().getLanguageHandler().getMessage("peace-requested"));
		}
	}

	public static void declareCommandHandler(CommandSender sender, Command cmd, String cmdString, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command can only be used by a player!");
			return;
		}
		if (!sender.hasPermission(BetterTownyWars.Permissions.TVT_DECLARE_PERMISSION)) {
			sender.sendMessage(BetterTownyWars.getInstance().getLanguageHandler().getMessage("no-permission"));
			return;
		}
		Player p = (Player) sender;
		Town playerTown;
		try {
			playerTown = TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown();
		} catch (NotRegisteredException e) {
			sender.sendMessage(BetterTownyWars.getInstance().getLanguageHandler().getMessage("not-in-a-town"));
			return;
		}
		Town enemyTown;
		try {
			enemyTown = TownyUniverse.getInstance().getDataSource().getTown(args[2]);
		} catch (NotRegisteredException e) {
			sender.sendMessage(
					BetterTownyWars.getInstance().getLanguageHandler().getMessage("town-does-not-exist"));
			return;
		}
		if (playerTown.equals(enemyTown)) {
			sender.sendMessage(BetterTownyWars.getInstance().getLanguageHandler()
					.getMessage("cannot-declare-your-own-town"));
			return;
		}
		try {
			if (playerTown.getNation().equals(enemyTown.getNation())) {
				sender.sendMessage(ChatColor.RED + "This town and you are part of the same nation!");
				return;
			}
		} catch (NotRegisteredException e1) {
		}
		for (War w : BetterTownyWars.getInstance().getWarManager().getWarsForMember(playerTown.getUuid())) {
			if (!w.getMembers().contains(enemyTown.getUuid()))
				continue;
			sender.sendMessage(ChatColor.RED + "You are already at war with this town");
			return;
		}
		TownVsTownWar war = new TownVsTownWar(WarType.getWarType("TOWN_VS_TOWN").toString(),
				System.currentTimeMillis() + BetterTownyWars.Configuration.MAX_WAR_DURATION);
		war.addMember(playerTown.getUuid());
		war.addMember(enemyTown.getUuid());
		BetterTownyWars.getInstance().getWarManager().declareWar(war);
		try {
			if (playerTown.getAccount().canPayFromHoldings(Configuration.DECLARE_WAR_COST))
				playerTown.getAccount().pay(Configuration.DECLARE_WAR_COST, "Declare war cost");
		} catch (EconomyException e) {
		}
	}

	public static void finishCommandHandler(CommandSender sender, Command cmd, String cmdString, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command can only be used by a player!");
			return;
		}
		if (!sender.hasPermission(BetterTownyWars.Permissions.TVT_FINISH_PERMISSION)) {
			sender.sendMessage(BetterTownyWars.getInstance().getLanguageHandler().getMessage("no-permission"));
			return;
		}
		Player p = (Player) sender;
		Town playerTown;
		try {
			playerTown = TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown();
		} catch (NotRegisteredException e) {
			sender.sendMessage(BetterTownyWars.getInstance().getLanguageHandler().getMessage("not-in-a-town"));
			return;
		}
		Town enemyTown;
		try {
			enemyTown = TownyUniverse.getInstance().getDataSource().getTown(args[2]);
		} catch (NotRegisteredException e) {
			sender.sendMessage(
					BetterTownyWars.getInstance().getLanguageHandler().getMessage("town-does-not-exist"));
			return;
		}
		if (enemyTown.getName().equals(playerTown.getName())) {
			sender.sendMessage(ChatColor.RED + "You cannot request peace to your own town!");
			return;
		}
		List<War> activeWars = BetterTownyWars.getInstance().getWarManager().getWarsForMember(playerTown.getUuid());
		War correctWar = null;
		for (War war : activeWars) {
			List<UUID> members = war.getMembers();
			if (members.contains(enemyTown.getUuid())) {
				correctWar = war;
				break;
			}
		}
		if (correctWar == null) {
			sender.sendMessage(ChatColor.RED + "You are not in war with that town!");
			return;
		}
		sender.sendMessage(
				BetterTownyWars.getInstance().getLanguageHandler().getMessage("you-peace-requested"));
		BetterTownyWars.getInstance().getWarManager().requestPeace(correctWar, playerTown.getUuid());
	}

}
