package com.facilio.bmsconsole.context;

import java.io.Serializable;

public class PMPlannerSettingsContext implements Serializable{
	private static final long serialVersionUID = 1L;


	private long id=-1;
	private String settingsJSON;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getSettingsJSON() {
		return settingsJSON;
	}
	public void setSettingsJSON(String settingsJSON) {
		this.settingsJSON = settingsJSON;
	}
	
	
}
