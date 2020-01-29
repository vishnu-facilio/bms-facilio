package com.facilio.accounts.util;

import java.util.List;

import com.facilio.accounts.util.NewPermissionFactory.Permission_Child_Type;


public interface NewPermissionInterface {
	public long getPermission();
	public String getPermissionName();
	public NewPermissionInterface getParent();
	public void addChild(NewPermissionInterface type);
	public List<NewPermissionInterface> getChilds();
	public Permission_Child_Type getChildType();
}
