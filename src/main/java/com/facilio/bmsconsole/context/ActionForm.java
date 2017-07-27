package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ActionForm {
	
	
//	public Map<Integer, String> getStatusList() {
//		Map<Integer, String> statusList = new HashMap<>();
//		for(TicketContext.Status status : TicketContext.Status.values()) {
//			statusList.put(status.getStatusAsInt(), status.getStatusAsString());
//		}
//		return statusList;
//	}
	
	private Map<Long, String> locations;
	public Map<Long, String> getLocations() {
		return locations;
	}
	public void setLocations(Map<Long, String> locations) {
		this.locations = locations;
	}
	
	public List< String> getCategories() {
		return categories;
	}
	public void setCategories(List< String> categories) {
		this.categories = categories;
	}
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
	
	public Map<Integer, String> getSpaceAvailabilityList() {
		return SpaceContext.getAllAvailability();
	}
}
