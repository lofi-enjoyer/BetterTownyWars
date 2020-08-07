package com.aurgiyalgo.BetterTownyWars;

public class BTWDebugHandler {
	
	public static boolean logEnabled = false;
	
	public static void log(String msg) {
		if (!logEnabled) return;
		BetterTownyWars.getInstance().getLogger().info(msg);
	}

}
