package com.aurgiyalgo.BetterTownyWars.gui;

import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ClickableItem {
	
	private Consumer<Player> _action;
	private ItemStack _item;
	
	public ClickableItem(ItemStack item, Consumer<Player> action) {
		this._item = item;
		this._action = action;
	}
	
	public void run(Player player) {
		_action.accept(player);
	}
	
	public ItemStack getItem() {
		return _item;
	}

}
