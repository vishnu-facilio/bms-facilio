package com.facilio.bmsconsole.actions;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.timeseries.TimeSeriesAPI;
import com.opensymphony.xwork2.ActionSupport;

public class TimeSeries extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	
	public String publish() throws Exception
	{
		JSONParser parser = new JSONParser();
		JSONObject payLoad = (JSONObject) parser.parse(getDeviceData());
		TimeSeriesAPI.processPayLoad(getTimestamp(), payLoad);
		return SUCCESS;
	}
	
	public String migrateData() throws Exception{
		
		List<Map<String, Object>> result=TimeSeriesAPI.fetchUnmodeledData(getDeviceList());
		TimeSeriesAPI.migrateUnmodeledData(result);
		return SUCCESS;
	}
	
	public String deviceList() throws Exception{
		setAllDevices(TimeSeriesAPI.getAllDevices());
		return SUCCESS;
	}
	
	public String mapInstanceAsset() throws Exception
	{
		Map<String,Object> object = getInstanceAssetMap();
		String deviceName = (String) object.get("deviceName");
		long assetId = (long) object.get("assetId");
		Map<String,Long> instanceFieldMap = (Map<String, Long>) object.get("instanceFieldMap");
		TimeSeriesAPI.insertInstanceAssetMapping(deviceName, assetId, instanceFieldMap);
		return SUCCESS;
	}
	
	long timestamp;
	public void setTimestamp(long ttime) {
		this.timestamp=ttime;
	}

	public long getTimestamp() {
		return timestamp;
	}
	
	private String deviceData = null;
	public void setDeviceData(String deviceData) 
	{
		this.deviceData = deviceData;
	}
	
	public String getDeviceData() {
		
		return deviceData;
	}
	
	private String deviceList = null;
	public void setDeviceList(String deviceList) 
	{
		this.deviceList = deviceList;
	}
	
	public String getDeviceList() {
		
		return deviceList;
	}
	
	private JSONObject reportData = null;
	public JSONObject getReportData() {
		return reportData;
	}
	public void setReportData(JSONObject reportData) {
		this.reportData = reportData;		
	}
	
	private Map<String,Object> instanceAssetMap = null;
	public Map<String,Object> getInstanceAssetMap() {
		return instanceAssetMap;
	}
	public void setInstanceAssetMap(Map<String,Object> instanceAssetMap) {
		this.instanceAssetMap = instanceAssetMap;		
	}
	
	private Map<String, List<String>> allDevices = null;
	public Map<String, List<String>> getAllDevices() {
		return allDevices;
	}
	public void setAllDevices(Map<String, List<String>> allDevices) {
		this.allDevices = allDevices;		
	}
}