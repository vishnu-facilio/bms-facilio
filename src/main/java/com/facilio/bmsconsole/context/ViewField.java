package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.FacilioField;

public class ViewField extends FacilioField{
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long viewId = -1;
	public long getViewId() {
		return viewId;
	}
	public void setViewId(long viewId) {
		this.viewId = viewId;
	}
	
	private String columnDisplayName;
	public String getColumnDisplayName() {
		return columnDisplayName;
	}
	public void setColumnDisplayName(String columnDisplayName) {
		this.columnDisplayName = columnDisplayName;
	}
	
	public ViewField() { }
	
	public ViewField(String name, String displayName) {
		setName(name);
		this.columnDisplayName = displayName;
	}
	
}
