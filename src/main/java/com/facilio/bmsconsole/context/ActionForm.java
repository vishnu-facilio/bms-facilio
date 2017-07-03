package com.facilio.bmsconsole.context;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;


public class ActionForm {
	
	
	public Map<Integer, String> getStatusList() {
		return TicketContext.getAllStatus();
	}
	
	public List< String> getLocations() {
		return locations;
	}
	public void setLocations(List< String> locations) {
		this.locations = locations;
	}
	public List< String> getCategories() {
		return categories;
	}
	public void setCategories(List< String> categories) {
		this.categories = categories;
	}
	List<String> locations =new ArrayList<String>();
	List<String> categories =new ArrayList<String>();
//	Map<Long,String> categories ;

	private Map<String, String> modules = new HashMap<String, String>();
	public Map<String,String> getModules() {
		modules.put("tickets", "Tickets");
		modules.put("tasks", "Tasks");
		return modules;
	}
	public void setModules(Map<String, String> modules) {
		this.modules = modules;
	}

	private Map<Long, String> groupList;
	public  Map<Long, String> getGroupList() {
		return groupList;
	}
	public void setGroupList( Map<Long, String> groupList) {
		this.groupList = groupList;
	}
	
	private Map<Long, String> userList;
	public Map<Long, String> getUserList() {
		return userList;
	}
	public void setUserList(Map<Long, String> userList) {
		this.userList = userList;
	}
	
	private Map<Long, String> assetList;
	public Map<Long, String> getAssetList() {
		return assetList;
	}
	public void setAssetList(Map<Long, String> assetList) {
		this.assetList = assetList;
	}
}
