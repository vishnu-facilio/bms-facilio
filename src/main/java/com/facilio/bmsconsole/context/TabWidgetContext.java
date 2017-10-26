package com.facilio.bmsconsole.context;

public class TabWidgetContext {
	
	private long tabWidgetId;
	public long getTabWidgetId() {
		return tabWidgetId;
	}
	public void setTabWidgetId(long tabWidgetId) {
		this.tabWidgetId = tabWidgetId;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private String moduleLinkName;
	public String getModuleLinkName() {
		return moduleLinkName;
	}
	public void setModuleLinkName(String moduleLinkName) {
		this.moduleLinkName = moduleLinkName;
	}
	
	private String tabName;
	public String getTabName() {
		return tabName;
	}
	public void setTabName(String tabName) {
		this.tabName = tabName;
	}
	
	private String tabLinkName;
	public String getTabLinkName() {
		return tabLinkName;
	}
	public void setTabLinkName(String tabLinkName) {
		this.tabLinkName = tabLinkName;
	}
	
	private String widgetName;
	public String getWidgetName() {
		return widgetName;
	}
	public void setWidgetName(String widgetName) {
		this.widgetName = widgetName;
	}
	
	private Long connectedAppId;
	public Long getConnectedAppId() {
		return connectedAppId;
	}
	public void setConnectedAppId(Long connectedAppId) {
		this.connectedAppId = connectedAppId;
	}
	
	private String resourcePath;
	public String getResourcePath() {
		return resourcePath;
	}
	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}
	
	private ConnectedAppContext connectedApp;
	public ConnectedAppContext getConnectedApp() {
		return connectedApp;
	}
	public void setConnectedApp(ConnectedAppContext connectedApp) {
		this.connectedApp = connectedApp;
	}
}
