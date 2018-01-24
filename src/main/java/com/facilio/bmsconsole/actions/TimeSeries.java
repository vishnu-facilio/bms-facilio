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
	
}