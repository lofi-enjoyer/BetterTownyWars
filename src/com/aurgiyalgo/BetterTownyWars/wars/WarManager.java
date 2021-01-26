package com.aurgiyalgo.BetterTownyWars.wars;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.aurgiyalgo.BetterTownyWars.BetterTownyWars;
import com.aurgiyalgo.BetterTownyWars.BetterTownyWars.Configuration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class WarManager {
	
	private List<War> wars;
	private BukkitRunnable timeCheckTimer;
	private static Gson gson;
	
	public WarManager() {
		wars = new ArrayList<War>();
		
		gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		
		timeCheckTimer = new BukkitRunnable() {
			
			War currentIteratedWar;
			
			@Override
			public void run() {
				Iterator<War> i = wars.iterator();
				while (i.hasNext()) {
					currentIteratedWar = i.next();
					long currentTime = System.currentTimeMillis();
					if (currentIteratedWar.getEndTime() <= currentTime) {
						currentIteratedWar.onWarPeace();
						currentIteratedWar.disablePvP();
						i.remove();
					}
				}
			}
			
		};
	}
	
	public void loadData() {
		List<JSONObject> jsonArray = BetterTownyWars.getInstance().getDataHandler().getDataList("wars");
		if (jsonArray == null) return;
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject currentObject = jsonArray.get(i);
			War war = gson.fromJson(currentObject.toJSONString(), WarType.getWarType(currentObject.get("_type").toString()).getClassType());
			war.setupWar();
			wars.add(war);
		}
	}
	
	public void saveData() {
		List<JSONObject> jsonArray = new ArrayList<JSONObject>();
		for (War w : wars) {
			try {
				JSONObject jsonObject = (JSONObject) new JSONParser().parse(gson.toJson(w));
				jsonArray.add(jsonObject);
			} catch (ParseException e) {e.printStackTrace();}
		}
		BetterTownyWars.getInstance().getDataHandler().addDataList("wars", jsonArray);
	}
	
	public void initTimeLimitCheck() {
		if (Configuration.MAX_WAR_DURATION <= 0) return;
		timeCheckTimer.runTaskTimer(BetterTownyWars.getInstance(), 0, BetterTownyWars.Configuration.WAR_ENDED_CHECK_INTERVAL * 20);
	}
	
	public void disableTimeLimitCheck() {
		if (Configuration.MAX_WAR_DURATION <= 0) return;
		timeCheckTimer.cancel();
	}
	
	public void forcePvPInTownsAtWar() {
		for (War war : wars) {
			war.enablePvP();
		}
	}
	
	public void disablePvPInTownsAtWar() {
		for (War war : wars) {
			war.disablePvP();
		}
	}
	
	public void requestPeace(War war, UUID member) {
		war.addPeaceRequest(member);
		if (war.everyMemberRequestedPeace()) declarePeace(war);
	}
	
	public void declareWar(War war) {
		wars.add(war);
		war.onWarDeclare();
		war.enablePvP();
	}
	
	public void finishWar(War war) {
		war.onWarFinish();
		war.disablePvP();
		wars.remove(war);
	}
	
	public void declarePeace(War war) {
		war.onWarPeace();
		war.disablePvP();
		wars.remove(war);
	}
	
	public List<War> getWarsForMember(UUID member) {
		List<War> wars = new ArrayList<War>();
		for (War w : this.wars) {
			if (w.getMembers().contains(member)) wars.add(w);
		}
		return wars;
	}
	
	public List<War> getAllWars() {
		return new ArrayList<War>(wars);
	}
	
	public void onPlayerKill(Player victim, Player killer) {
		List<War> tempWars = new ArrayList<War>(wars);
		for (War war : tempWars) {
			war.onPlayerKill(victim, killer);
		}
	}

}
