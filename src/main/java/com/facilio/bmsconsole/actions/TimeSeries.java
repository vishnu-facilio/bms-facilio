package com.facilio.bmsconsole.actions;

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
	
	private JSONObject reportData = null;
	public JSONObject getReportData() {
		return reportData;
	}
	public void setReportData(JSONObject reportData) {
		this.reportData = reportData;		
	}
	
}