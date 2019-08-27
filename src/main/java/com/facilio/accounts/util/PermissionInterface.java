package com.facilio.accounts.util;

import java.util.List;

public interface PermissionInterface {
	
	public long getPermission();
	public String getPermissionName();
	public String getModuleName();
	public PermissionInterface getParent();
	public void addChild(PermissionInterface type);
	public List<PermissionInterface> getChilds();
}
