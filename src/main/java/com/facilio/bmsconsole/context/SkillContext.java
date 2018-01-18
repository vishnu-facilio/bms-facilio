package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class SkillContext extends ModuleBaseWithCustomFields {
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private Boolean active;
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	
	private long categoryId;
	public long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}
	
}
