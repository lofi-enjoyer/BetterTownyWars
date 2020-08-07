
package com.aurgiyalgo.BetterTownyWars.data;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DataHandler {
	
	private File _dataFile;
	private boolean _dataIsLoaded;
	private JSONObject _jsonData;
	private JSONObject _jsonDataToSave;
	
	public DataHandler(File dataFolder) {
		_jsonDataToSave = new JSONObject();
		if (!dataFolder.exists()) return;
		_dataFile = new File(dataFolder, "data.json");
		_dataIsLoaded = _dataFile.exists();

		if (!_dataIsLoaded) return;
		
		JSONParser parser = new JSONParser();

		Object obj;
		try {
			obj = parser.parse(new FileReader(_dataFile));
			_jsonData = (JSONObject) obj;
		} catch (IOException | ParseException e) { e.printStackTrace(); }
		
	}
	
	public List<JSONObject> getDataList(String dataTag) {
		if (!_dataIsLoaded) return null;
		if (!_jsonData.containsKey(dataTag)) return null;
		JSONArray jsonArray = (JSONArray) _jsonData.get(dataTag);
		List<JSONObject> dataList = new ArrayList<JSONObject>();
		for (int i = 0; i < jsonArray.size(); i++) {
			dataList.add((JSONObject) jsonArray.get(i));
		}
		return dataList;
	}
	
	@SuppressWarnings("unchecked")
	public void addDataList(String dataTag, List<JSONObject> dataArray) {
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < dataArray.size(); i++) {
			jsonArray.add(dataArray.get(i));
		}
		if (_jsonDataToSave.containsKey(dataTag)) _jsonDataToSave.remove(dataTag);
		_jsonDataToSave.put(dataTag, jsonArray);
	}
	
	public void saveData() {
		try {
			FileWriter fw = new FileWriter(_dataFile);
			fw.write(_jsonDataToSave.toString());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
