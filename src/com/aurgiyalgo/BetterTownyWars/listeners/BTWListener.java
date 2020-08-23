package com.aurgiyalgo.BetterTownyWars.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.aurgiyalgo.BetterTownyWars.BetterTownyWars;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

public class BTWListener implements Listener {
	
	@EventHandler
	public void onPlayerKill(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof Player)) return;
		if (!(e.getDamager() instanceof Player)) return;
		
		if (((Player)e.getEntity()).getHealth() - e.getDamage() > 0) return;
		
		BetterTownyWars.getInstance().getWarManager().onPlayerKill((Player) e.getEntity(), (Player) e.getDamager());
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		try {
			Resident res = TownyUniverse.getInstance().getDataSource().getResident(e.getPlayer().getName());
			Town t = res.getTown();
			if (BetterTownyWars.getInstance().getWarManager().getWarsForMember(t.getUuid()).size() > 0) {
				e.getPlayer().sendMessage(BetterTownyWars.getInstance().getLanguageHandler().getMessage("war-alert-on-join"));
			}
		} catch (NotRegisteredException e1) {}
	}

}
