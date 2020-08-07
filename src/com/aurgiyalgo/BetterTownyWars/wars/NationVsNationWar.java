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
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;

public class NationVsNationWar extends War {

	@Expose
	private List<UUID> _deadPlayers = new ArrayList<UUID>();
	
	private List<Nation> _nations = new ArrayList<Nation>();

	public NationVsNationWar(String warType, long endTime) {
		super(warType, endTime);
	}
	
	private void setupNations() {
		_nations = new ArrayList<Nation>();
		for (UUID member : getMembers()) {
			try {
				Nation n = TownyUniverse.getInstance().getDataSource().getNation(member);
				_nations.add(n);
			} catch (NotRegisteredException e) {
			}
		}
	}

	@Override
	protected void onWarDeclare() {
		if (_nations == null || _nations.size() <= 0) setupNations();
		for (Nation n : _nations)
			TownyMessaging.sendTitleMessageToNation(n, "", BetterTownyWars.getInstance().getLanguageHandler().getMessage("war-started"));
	}

	@Override
	protected void onWarFinish() {
		if (_nations == null || _nations.size() <= 0) setupNations();
		
		Nation winner = null;
		
		for (Nation n : _nations) {
			int playersKilled = 0;
			for (UUID id : _deadPlayers) {
				if (n.hasResident(Bukkit.getOfflinePlayer(id).getName()))
					playersKilled++;
			}
			
			if (playersKilled >= BetterTownyWars.Configuration.NATION_VS_NATION_KILL_PERCENTAGE_TO_FINISH * n.getResidents().size()) {
				winner = n;
				break;
			}
		}
		
		for (Nation n : _nations) {
			if (n.equals(winner)) continue;
			TownyMessaging.sendTitleMessageToNation(n, "", BetterTownyWars.getInstance().getLanguageHandler().getMessage("war-finished-lose"));
			try {
				if (n.getAccount().canPayFromHoldings(BetterTownyWars.Configuration.LOSE_WAR_COST))
					n.getAccount().payTo(BetterTownyWars.Configuration.LOSE_WAR_COST, winner, BetterTownyWars.getInstance().getLanguageHandler().getMessage("war-spoils"));
			} catch (EconomyException e) {
				
			}
		}
		TownyMessaging.sendTitleMessageToNation(winner, "", BetterTownyWars.getInstance().getLanguageHandler().getMessage("war-finished-win"));
	}

	@Override
	protected void onPlayerKill(Player victim, Player killer) {
		if (_nations == null || _nations.size() <= 0) setupNations();

		if (_deadPlayers.contains(victim.getUniqueId()))
			return;

		Nation victimNation = null;
		for (Nation n : _nations) {
			victimNation = n;
			if (!victimNation.hasResident(victim.getName()))
				continue;
		}

		if (victimNation == null)
			return;

		Nation killerNation;
		for (Nation n : _nations) {
			killerNation = n;

			if (killerNation.getName().equals(victimNation.getName()))
				continue;

			if (!killerNation.hasResident(killer.getName()))
				continue;

			_deadPlayers.add(victim.getUniqueId());

			int playersKilled = 0;
			for (UUID id : _deadPlayers) {
				if (victimNation.hasResident(Bukkit.getOfflinePlayer(id).getName()))
					playersKilled++;
			}

			if (playersKilled >= BetterTownyWars.Configuration.NATION_VS_NATION_KILL_PERCENTAGE_TO_FINISH * victimNation.getResidents().size()) {
				BetterTownyWars.getInstance().getWarManager().finishWar(this);
			}

			return;
		}
	}

	@Override
	public void enablePvP() {
		if (_nations == null || _nations.size() <= 0) setupNations();
		for (int i = 0; i < _nations.size(); i++) {
			Nation n = _nations.get(i);
			for (Town t : n.getTowns()) {
				t.setAdminEnabledPVP(true);
			}
		}
	}

	@Override
	public void disablePvP() {
		if (_nations == null || _nations.size() <= 0) setupNations();
		for (int i = 0; i < _nations.size(); i++) {
			Nation n = _nations.get(i);
			for (Town t : n.getTowns()) {
				t.setAdminEnabledPVP(false);
			}
		}
	}

	@Override
	public String getMemberName(UUID member) {
		if (_nations == null || _nations.size() <= 0) setupNations();
		for (Nation n : _nations) {
			if (n.uuid == member)
				return n.getName();
		}
		return null;
	}

	@Override
	protected void onMemberJoin(UUID member) {
		if (_nations == null || _nations.size() <= 0) setupNations();
		try {
			Nation n = TownyUniverse.getInstance().getDataSource().getNation(member);
			_nations.add(n);
		} catch (NotRegisteredException e) {
		}
	}

	@Override
	protected void onMemberLeave(UUID member) {
		if (_nations == null || _nations.size() <= 0) setupNations();
		for (Nation n : _nations) {
			if (n.uuid.equals(member))
				_nations.remove(n);
		}
	}

	@Override
	protected void onWarPeace() {
		if (_nations == null || _nations.size() <= 0) setupNations();
		for (Nation n : _nations) {
			TownyMessaging.sendNationMessage(n, BetterTownyWars.getInstance().getLanguageHandler().getMessage("war-ended-by-peace"));
		}
	}

	@Override
	protected void onMemberRequestPeace(UUID member) {
		if (_nations == null || _nations.size() <= 0) setupNations();
		Nation nationRequest = null;
		for (Nation n : _nations) {
			if (n.uuid.equals(member)) {
				nationRequest = n;
				break;
			}
		}
		for (Nation n : _nations) {
			if (!n.uuid.equals(member))
				TownyMessaging.sendNationMessage(n, ChatColor.GREEN + nationRequest.getFormattedName() + BetterTownyWars.getInstance().getLanguageHandler().getMessage("peace-requested"));
		}
	}
	
	public static void declareCommandHandler(CommandSender sender, Command cmd, String cmdString, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command can only be used by a player!");
			return;
		}
		if (!sender.hasPermission(BetterTownyWars.Permissions.NVN_DECLARE_PERMISSION)) {
			sender.sendMessage(BetterTownyWars.getInstance().getLanguageHandler().getMessage("no-permission"));
			return;
		}
		Player p = (Player) sender;
		Nation playerNation;
		try {
			playerNation = TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown().getNation();
			if (playerNation.isNeutral()) {
				sender.sendMessage(BetterTownyWars.getInstance().getLanguageHandler().getMessage("your-nation-neutral"));
				return;
			}
		} catch (NotRegisteredException e) {
			sender.sendMessage(BetterTownyWars.getInstance().getLanguageHandler().getMessage("not-in-a-nation"));
			return;
		}
		Nation enemyNation;
		try {
			enemyNation = TownyUniverse.getInstance().getDataSource().getNation(args[2]);
			if (enemyNation.isNeutral()) {
				sender.sendMessage(BetterTownyWars.getInstance().getLanguageHandler().getMessage("enemy-nation-neutral"));
				return;
			}
		} catch (NotRegisteredException e) {
			sender.sendMessage(BetterTownyWars.getInstance().getLanguageHandler().getMessage("nation-does-not-exist"));
			return;
		}
		if(playerNation.equals( enemyNation )) {
			sender.sendMessage(BetterTownyWars.getInstance().getLanguageHandler().getMessage("cannot-declare-your-own-nation"));
			return;
		}
		for (War w : BetterTownyWars.getInstance().getWarManager().getWarsForMember(playerNation.uuid)) {
			if (!w.getMembers().contains(enemyNation.uuid)) continue;
			sender.sendMessage("You are already at war with this nation");
			return;
		}
		NationVsNationWar war = new NationVsNationWar(WarType.getWarType("NATION_VS_NATION").toString(), System.currentTimeMillis() + BetterTownyWars.Configuration.MAX_WAR_DURATION);
		war.addMember(playerNation.uuid);
		war.addMember(enemyNation.uuid);
		BetterTownyWars.getInstance().getWarManager().declareWar(war);
		try {
			if (playerNation.getAccount().canPayFromHoldings(Configuration.DECLARE_WAR_COST)) 
			playerNation.getAccount().pay(Configuration.DECLARE_WAR_COST, "Declare war cost");
		} catch (EconomyException e) {}
	}
	
	public static void finishCommandHandler(CommandSender sender, Command cmd, String cmdString, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command can only be used by a player!");
			return;
		}
		if (!sender.hasPermission(BetterTownyWars.Permissions.NVN_FINISH_PERMISSION)) {
			sender.sendMessage(BetterTownyWars.getInstance().getLanguageHandler().getMessage("no-permission"));
			return;
		}
		Player p = (Player) sender;
		Nation playerNation;
		try {
			playerNation = TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown().getNation();
		} catch (NotRegisteredException e) {
			sender.sendMessage(BetterTownyWars.getInstance().getLanguageHandler().getMessage("not-in-a-nation"));
			return;
		}
		Nation enemyNation;
		try {
			enemyNation = TownyUniverse.getInstance().getDataSource().getNation(args[2]);
		} catch (NotRegisteredException e) {
			sender.sendMessage(BetterTownyWars.getInstance().getLanguageHandler().getMessage("nation-does-not-exist"));
			return;
		}
		if (enemyNation.getName().equals(playerNation.getName())) {
			sender.sendMessage(ChatColor.RED + "You cannot request peace to your own nation!");
			return;
		}
		List<War> activeWars = BetterTownyWars.getInstance().getWarManager().getWarsForMember(playerNation.uuid);
		War correctWar = null;
		for (War war : activeWars) {
			List<UUID> members = war.getMembers();
			if (members.contains(enemyNation.uuid)) {
				correctWar = war;
				break;
			}
		}
		if (correctWar == null) {
			sender.sendMessage(ChatColor.RED + "You are not in war with that nation!");
			return;
		}
		sender.sendMessage(BetterTownyWars.getInstance().getLanguageHandler().getMessage("you-peace-requested"));
		BetterTownyWars.getInstance().getWarManager().requestPeace(correctWar, playerNation.uuid);
	}

}
