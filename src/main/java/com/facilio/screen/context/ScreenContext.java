package com.facilio.screen.context;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public class ScreenContext {

	Long id;
	Long orgId;
	String name;
	int interval;
	List<ScreenDashboardRelContext> screenDashboards = new ArrayList<>();
	String screenSettingString;
	
	JSONObject screenSetting = new JSONObject();
	public JSONObject getScreenSetting() throws ParseException {
		if(this.screenSettingString != null) {
			JSONParser parser = new JSONParser();
			this.screenSetting = (JSONObject) parser.parse(this.screenSettingString);
		}
		return this.screenSetting;
	}
	public void setScreenSetting(JSONObject screenSetting) {
		this.screenSetting = screenSetting;
	}
	public String getScreenSettingString() {
		return screenSettingString;
	}
	public void setScreenSettingString(String screenSettingString) {
		this.screenSettingString = screenSettingString;
	}

	
	public List<ScreenDashboardRelContext> getScreenDashboards() {
		return screenDashboards;
	}
	public void setScreenDashboards(List<ScreenDashboardRelContext> screenDashboards) {
		this.screenDashboards = screenDashboards;
	}
	public void addScreenDashboard(ScreenDashboardRelContext screenDashboard) {
		screenDashboards = (screenDashboards == null) ? new ArrayList<>() : screenDashboards; 
		
		this.screenDashboards.add(screenDashboard);
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	
	List<RemoteScreenContext> remoteScreens;
	
	public void setRemoteScreens(List<RemoteScreenContext> remoteScreens) {
		this.remoteScreens = remoteScreens;
	}
	
	public List<RemoteScreenContext> getRemoteScreens() {
		return this.remoteScreens;
	}
}
