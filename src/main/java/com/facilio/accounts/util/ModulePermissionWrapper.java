package com.facilio.accounts.util;

import java.util.List;

public class ModulePermissionWrapper {

	int order;
	String name;
	List<PermissionInterface> permissions;
	
	public ModulePermissionWrapper() {
		// TODO Auto-generated constructor stub
	}
	
	public ModulePermissionWrapper(int order,String name,List<PermissionInterface> permissions) {
		this.order = order;
		this.name  = name;
		this.permissions = permissions;
	}
	
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<PermissionInterface> getPermissions() {
		return permissions;
	}
	public void setPermissions(List<PermissionInterface> permissions) {
		this.permissions = permissions;
	}
	
	
 }
