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
	
	/**
	 * @param member UUID of the member to add
	 */
	public void addMember(UUID member) {
		if (_members.contains(member)) return;
		_members.add(member);
		onMemberJoin(member);
	}
	
	/**
	 * @param member UUID of the member to remove
	 */
	public void removeMember(UUID member) {
		_members.remove(member);
		onMemberLeave(member);
	}
	
	/**
	 * @return List of members' UUIDs
	 */
	public List<UUID> getMembers() {
		return new ArrayList<UUID>(_members);
	}
	
	/**
	 * @return List of members who requested peace
	 */
	public List<UUID> getPeaceRequestedMembers() {
		return new ArrayList<UUID>(_peaceRequested);
	}
	
	/**
	 * @param member Member who requests peace
	 */
	public void addPeaceRequest(UUID member) {
		if (!_members.contains(member)) return;
		if (_peaceRequested.contains(member)) return;
		_peaceRequested.add(member);
	}
	
	/**
	 * @return True if every member of the war requested peace
	 */
	public boolean everyMemberRequestedPeace() {
		for (UUID member : _members) {
			if (_peaceRequested.contains(member)) continue;
			return false;
		}
		return true;
	}
	
	/**
	 * @return Time when the war ends in milliseconds (System time)
	 */
	public long getEndTime() {
		return _endTime;
	}
	
	/**
	 * @return Type of war
	 */
	public WarType getType() {
		return WarType.getWarType(_type);
	}
	
	/**
	 * @param member Member who joined the war
	 */
	protected abstract void onMemberJoin(UUID member);
	
	/**
	 * @param member Member who left the war
	 */
	protected abstract void onMemberLeave(UUID member);
	
	/**
	 * Fired when the war is declared
	 */
	protected abstract void onWarDeclare();
	
	/**
	 * Fired when the war finishes by killing enough enemy residents
	 */
	protected abstract void onWarFinish();
	
	/**
	 * Fired when the war finishes peacefully (every member sent a peace request)
	 */
	protected abstract void onWarPeace();
	
	/**
	 * @param member Fired when a member of the war sends a peace request
	 */
	protected abstract void onMemberRequestPeace(UUID member);
	
	/**
	 * Fired when a player is killed by a resident of another member of the war
	 * @param victim Player killed
	 * @param killer Assassin player
	 */
	protected abstract void onPlayerKill(Player victim, Player killer);
	
	/**
	 * Enables PvP on all the cities/nations of the war
	 */
	public abstract void enablePvP();
	
	/**
	 * Disables PvP on all the cities/nations of the war
	 */
	public abstract void disablePvP();

	/**
	 * Setup method for startup actions
	 */
	public abstract void setupWar();
	
	/**
	 * Executed when a player uses the "declare" command with this type of war
 	 * @param sender
	 * @param cmd
	 * @param cmdString
	 * @param args
	 */
	public static void declareCommandHandler(CommandSender sender, Command cmd, String cmdString, String[] args) {
		throw new IllegalStateException("Command handler for declare hasn't been set for this war type");
	}
	
	/**
	 * Executed when a player uses the "finish" command with this type of war
	 * @param sender
	 * @param cmd
	 * @param cmdString
	 * @param args
	 */
	public static void finishCommandHandler(CommandSender sender, Command cmd, String cmdString, String[] args) {
		throw new IllegalStateException("Command handler for finish hasn't been set for this war type");
	}
	
	/**
	 * @param member UUID of the member
	 * @return Formatted name of the member
	 */
	public abstract String getMemberName(UUID member);

}
