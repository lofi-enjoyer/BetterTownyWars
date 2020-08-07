package com.aurgiyalgo.BetterTownyWars.wars;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.gson.annotations.Expose;

public abstract class War {
	
	@Expose
	private List<UUID> _members;
	@Expose
	private String _type;
	@Expose
	private long _endTime;
	@Expose
	private List<UUID> _peaceRequested;
	
	public War(String type, long endTime) {
		this._type = type;
		this._endTime = endTime;
		
		_members = new ArrayList<UUID>();
		_peaceRequested = new ArrayList<UUID>();
	}
	
	public void addMember(UUID member) {
		if (_members.contains(member)) return;
		_members.add(member);
		onMemberJoin(member);
	}
	
	public void removeMember(UUID member) {
		if (!_members.contains(member)) return;
		_members.remove(member);
		onMemberLeave(member);
	}
	
	public List<UUID> getMembers() {
		return new ArrayList<UUID>(_members);
	}
	
	public List<UUID> getPeaceRequestedMembers() {
		return new ArrayList<UUID>(_peaceRequested);
	}
	
	public void addPeaceRequest(UUID member) {
		if (!_members.contains(member)) return;
		if (_peaceRequested.contains(member)) return;
		_peaceRequested.add(member);
	}
	
	public boolean everyMemberRequestedPeace() {
		for (UUID member : _members) {
			if (_peaceRequested.contains(member)) continue;
			return false;
		}
		return true;
	}
	
	public long getEndTime() {
		return _endTime;
	}
	
	public WarType getType() {
		return WarType.getWarType(_type);
	}
	
	protected abstract void onMemberJoin(UUID member);
	
	protected abstract void onMemberLeave(UUID member);
	
	protected abstract void onWarDeclare();
	
	protected abstract void onWarFinish();
	
	protected abstract void onWarPeace();
	
	protected abstract void onMemberRequestPeace(UUID member);
	
	protected abstract void onPlayerKill(Player victim, Player killer);
	
	public abstract void enablePvP();
	
	public abstract void disablePvP();
	
	public static void declareCommandHandler(CommandSender sender, Command cmd, String cmdString, String[] args) {
		throw new IllegalStateException("Command handler for declare hasn't been set for this war type");
	}
	
	public static void finishCommandHandler(CommandSender sender, Command cmd, String cmdString, String[] args) {
		throw new IllegalStateException("Command handler for finish hasn't been set for this war type");
	}
	
	public abstract String getMemberName(UUID member);

}
