package com.facilio.screen.context;

import java.util.ArrayList;
import java.util.List;

public class ScreenContext {

	Long id;
	Long orgId;
	String name;
	int interval;
	List<ScreenDashboardRelContext> screenDashboards;
	
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
}
