package com.facilio.bmsconsole.context;

import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.bmsconsoleV3.util.AppModulePermissionUtil;

public class Permission {
	public  String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public AppModulePermissionUtil.PermissionMapping getPermissionMapping() {
		return permissionMapping;
	}

	public void setPermissionMapping(AppModulePermissionUtil.PermissionMapping permissionMapping) {
		this.permissionMapping = permissionMapping;
	}

	public FeatureLicense getFeatureLicense() {
		return featureLicense;
	}

	public void setFeatureLicense(FeatureLicense featureLicense) {
		this.featureLicense = featureLicense;
	}

	String actionName;
	String displayName;
	long value;
	FeatureLicense featureLicense;
	AppModulePermissionUtil.PermissionMapping permissionMapping;
	boolean enabled;

	public Permission(String actionName, String displayName, long value, FeatureLicense featureLicense) {
		this.actionName = actionName;
		this.displayName = displayName;
		this.value = value;
		this.featureLicense = featureLicense;
	}

	public Permission(long value, String actionName, String displayName, AppModulePermissionUtil.PermissionMapping permissionMapping) {
		this.actionName = actionName;
		this.displayName = displayName;
		this.value = value;
		this.permissionMapping = permissionMapping;
	}

	public Permission(long value, String actionName, AppModulePermissionUtil.PermissionMapping permissionMapping) {
		this.actionName = actionName;
		this.value = value;
		this.permissionMapping = permissionMapping;
	}

	public Permission(String actionName, String displayName){
		this.actionName = actionName;
		this.displayName = displayName;
	}
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Permission() {
	}

}
