package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.context.TabWidgetContext;
import com.facilio.bmsconsole.util.TabWidgetAPI;
import com.opensymphony.xwork2.ActionSupport;

public class TabWidgetAction extends ActionSupport {
	
	private TabWidgetContext tabWidget;
	public TabWidgetContext getTabWidget() {
		return tabWidget;
	}
	public void setTabWidget(TabWidgetContext tabWidget) {
		this.tabWidget = tabWidget;
	}
	
	private String tabLinkName;
	public String getTabLinkName() {
		return tabLinkName;
	}
	public void setTabLinkName(String tabLinkName) {
		this.tabLinkName = tabLinkName;
	}
	
	public String viewTabWidget() throws Exception {
		
//		ConnectedAppContext connectedApp = new ConnectedAppContext();
//		connectedApp.setLinkName("bmts");
//		
//		TabWidgetContext tabWidget = new TabWidgetContext();
//		tabWidget.setConnectedApp(connectedApp);
//		tabWidget.setModuleLinkName("firealarm");
//		tabWidget.setWidgetName("Events");
		
		setTabWidget(TabWidgetAPI.getTabWidget(tabLinkName));
		return SUCCESS;
	}
 }

