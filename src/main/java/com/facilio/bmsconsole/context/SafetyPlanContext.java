package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;
import java.util.List;

public class SafetyPlanContext extends ModuleBaseWithCustomFields{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String description;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private List<HazardContext> hazards;
	public List<HazardContext> getHazards() {
		return hazards;
	}
	public void setHazards(List<HazardContext> hazards) {
		this.hazards = hazards;
	}
	
}
