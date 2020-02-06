package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class SafetyPlanHazardContext extends ModuleBaseWithCustomFields{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private SafetyPlanContext safetyPlan;
	private HazardContext hazard;
	public SafetyPlanContext getSafetyPlan() {
		return safetyPlan;
	}
	public void setSafetyPlan(SafetyPlanContext safetyPlan) {
		this.safetyPlan = safetyPlan;
	}
	public HazardContext getHazard() {
		return hazard;
	}
	public void setHazard(HazardContext hazard) {
		this.hazard = hazard;
	}
	
	
}
