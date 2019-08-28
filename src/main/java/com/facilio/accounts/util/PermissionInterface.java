package com.facilio.accounts.util;

import java.util.List;

import com.facilio.accounts.util.PermissionFactory.Permission_Child_Type;

public interface PermissionInterface {
	
	public long getPermission();
	public String getPermissionName();
	public String getModuleName();
	public PermissionInterface getParent();
	public void addChild(PermissionInterface type);
	public List<PermissionInterface> getChilds();
	public Permission_Child_Type getChildType();
}
