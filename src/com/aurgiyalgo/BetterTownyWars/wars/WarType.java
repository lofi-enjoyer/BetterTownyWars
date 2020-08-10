package com.aurgiyalgo.BetterTownyWars.wars;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WarType {
	
	private static final Map<String, WarType> values = new HashMap<String, WarType>();
	
	private String _type;
	private String _formattedName;
	private Class<? extends War> _classType;
	
	public WarType(String type, String formattedName, Class<? extends War> classType) {
		this._type = type;
		this._formattedName = formattedName;
		this._classType = classType;
	}
	
	@Override
	public String toString() {
		return this._type;
	}
	
	public String getFormattedName() {
		return this._formattedName;
	}
	
	public Class<? extends War> getClassType() {
		return _classType;
	}
	
	public static WarType getWarType(String type) {
		return values.get(type.toUpperCase());
	}
	
	public static Set<String> getWarTypes() {
		return values.keySet();
	}
	
	public static void addWarType(String type, String formattedName, Class<? extends War> classType) {
		values.put(type, new WarType(type, formattedName, classType));
	}
	
}

