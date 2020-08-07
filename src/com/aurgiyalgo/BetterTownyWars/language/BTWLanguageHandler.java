package com.aurgiyalgo.BetterTownyWars.language;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import com.aurgiyalgo.BetterTownyWars.BetterTownyWars.Configuration;

public class BTWLanguageHandler {
	
	private Map<String, String> _messagesMap;
	private File file;
	
	public BTWLanguageHandler(FileConfiguration languageFile, File file) {
		
		languageFile.addDefault("no-permission", "&cYou do not have permission to do that");
		languageFile.addDefault("not-enough-arguments", "&cNot enough arguments!");
		languageFile.addDefault("war-started", "&cA war was declared to your nation!");
		languageFile.addDefault("war-finished-lose", "&cWar finished! You lose...");
		languageFile.addDefault("war-finished-win", "&aWar finished! Your nation won!");
		languageFile.addDefault("war-spoils", "&bWar spoils");
		languageFile.addDefault("peace-requested", " sent a peace request!");
		languageFile.addDefault("you-peace-requested", "You sent a peace request!");
		languageFile.addDefault("not-in-a-nation", "&cYou are not in a nation");
		languageFile.addDefault("nation-does-not-exist", "&cThat nation does not exist");
		languageFile.addDefault("your-nation-neutral", "&cYour nation is neutral!");
		languageFile.addDefault("enemy-nation-neutral", "&cThat nation is neutral!");
		languageFile.addDefault("war-ended-by-peace", "&aThe war ended peacefully!");
		languageFile.addDefault("war-alert-on-join", "&6Be careful! There's an active war against your nation");
		languageFile.addDefault("cannot-declare-your-own-nation", "&cYou cannot declare a war to your own nation");
		languageFile.addDefault("not-in-a-town", "&cYou are not in a town");
		languageFile.addDefault("town-does-not-exist", "&cThat town does not exist");
		languageFile.addDefault("cannot-declare-your-own-town", "&cYou cannot declare a war to your own town");
		languageFile.options().copyDefaults(true);
		
		try {
			languageFile.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		_messagesMap = new HashMap<String, String>();
		
		for (Map.Entry<String, Object> entry : languageFile.getValues(false).entrySet()) {
			_messagesMap.put(entry.getKey(), (String) entry.getValue());
		}
	}
	
	public String getMessage(String key) {
		if (!_messagesMap.containsKey(key)) {
			if (!Configuration.ADD_MISSING_MESSAGES_TO_CONFIG) return "";
			_messagesMap.put(key, "");
			return "";
		}
		return ChatColor.translateAlternateColorCodes('&', _messagesMap.get(key));
	}

}
