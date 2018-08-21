package com.facilio.screen.context;

public class RemoteScreenContext {

	Long id;
	Long orgId;
	String name;
	Long screenId;
	String token;
	Long sessionStartTime;
	String sessionInfo;
	
	public Long getScreenId() {
		return screenId;
	}
	public void setScreenId(Long screenId) {
		this.screenId = screenId;
	}
	
	ScreenContext screenContext;
	public ScreenContext getScreenContext() {
		return screenContext;
	}
	public void setScreenContext(ScreenContext screenContext) {
		this.screenContext = screenContext;
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
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Long getSessionStartTime() {
		return sessionStartTime;
	}
	public void setSessionStartTime(Long sessionStartTime) {
		this.sessionStartTime = sessionStartTime;
	}
	public String getSessionInfo() {
		return sessionInfo;
	}
	public void setSessionInfo(String sessionInfo) {
		this.sessionInfo = sessionInfo;
	}
}
