package com.facilio.screen.context;

public class RemoteScreenContext {

	Long id;
	String name;
	Long screenName;
	String token;
	Long sessionStartTime;
	public Long getScreenId() {
		return screenId;
	}
	public void setScreenId(Long screenId) {
		this.screenId = screenId;
	}
	Long screenId;
	
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getScreenName() {
		return screenName;
	}
	public void setScreenName(Long screenName) {
		this.screenName = screenName;
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
	
	
}
