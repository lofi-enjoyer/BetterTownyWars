package com.aurgiyalgo.BetterTownyWars.gui;

import java.util.ArrayList;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ExampleGUI implements InventoryHolder, Listener {
	
    private final Inventory _inv;
    private final String _inventoryTitle;
    private final int _invSize;
    private ClickableItem[] _buttons;

    public ExampleGUI(int size, String title) {
    	this._inventoryTitle = title;
    	this._invSize = size;
    	this._buttons = new ClickableItem[_invSize];
    	
        _inv = Bukkit.createInventory(this, _invSize, _inventoryTitle);
        addItem(createGuiItem(Material.PAPER, ChatColor.BLUE + "Plugin info", "alo"), 0, p -> {
        	p.sendMessage("Ostia pues funciona xdd");
        });
    }

    @Override
    public Inventory getInventory() {
        return _inv;
    }
    
    private void addItem(ItemStack item, int position, Consumer<Player> action) {
    	_buttons[position] = new ClickableItem(item, action);
    	_inv.setItem(position, item);
    }
    
    private void executeItem(Player player, int position) {
    	_buttons[position].run(player);
    }

    private void initializeItems() {
        _inv.addItem(createGuiItem(Material.DIAMOND_SWORD, "Example Sword", "&aFirst line of the lore", "&bSecond line of the lore"));
        _inv.addItem(createGuiItem(Material.IRON_HELMET, "&bExample Helmet", "&aFirst line of the lore", "&bSecond line of the lore"));
    }

    // Nice little method to create a gui item with a custom name, and description
    private ItemStack createGuiItem(Material material, String name, String...lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        ArrayList<String> metaLore = new ArrayList<String>();

        for(String loreComments : lore) {
            metaLore.add(loreComments);
        }

        meta.setLore(metaLore);
        item.setItemMeta(meta);
        return item;
    }

    // You can open the inventory with this
    public void openInventory(Player p) {
        p.openInventory(_inv);
    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() != this) {
            return;
        }
        if (e.getClick().equals(ClickType.NUMBER_KEY)){
            e.setCancelled(true);
        }
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        // Using slots click is a best option for your inventory click's
        executeItem(p, e.getRawSlot());
    }
}