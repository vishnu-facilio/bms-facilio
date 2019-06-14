package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class AlarmCategoryContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;

	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
