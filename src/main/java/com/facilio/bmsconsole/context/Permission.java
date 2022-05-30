package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.accounts.util.AccountUtil.FeatureLicense;

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

	public Permission(String actionName, String displayName, long value, FeatureLicense featureLicense) {
		this.actionName = actionName;
		this.displayName = displayName;
		this.value = value;
		this.featureLicense = featureLicense;
	}

	public Permission() {
	}

}
