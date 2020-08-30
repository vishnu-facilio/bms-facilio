package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class DashboardUserFilterWidgetFieldMappingContext extends ModuleBaseWithCustomFields {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long userFilterId;
	private long widgetId;
	private long widgetFieldId;
	public long getUserFilterId() {
		return userFilterId;
	}
	public void setUserFilterId(long userFilterId) {
		this.userFilterId = userFilterId;
	}
	public long getWidgetId() {
		return widgetId;
	}
	public void setWidgetId(long widgetId) {
		this.widgetId = widgetId;
	}
	public long getWidgetFieldId() {
		return widgetFieldId;
	}
	public void setWidgetFieldId(long widgetFieldId) {
		this.widgetFieldId = widgetFieldId;
	}
	
	
	// default values is type string to handle  ,'all' ,'others' cases

}
