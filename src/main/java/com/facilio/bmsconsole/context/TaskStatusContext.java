package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class TaskStatusContext extends ModuleBaseWithCustomFields {
	private String status;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
