package com.facilio.bmsconsoleV3.context.inspection;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.v3.context.V3Context;

import lombok.Setter;

import lombok.Getter;

@Getter @Setter
public class InspectionTriggerIncludeExcludeResourceContext extends V3Context {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private InspectionTemplateContext inspectionTemplate;
	private InspectionTriggerContext inspectionTrigger;
	private ResourceContext resource;
	private Boolean isInclude;
	
	
	public Long getInspectionTriggerId() {
		if(inspectionTrigger != null) {
			return inspectionTrigger.getId();
		}
		return -1l;
	}
	
	public Long getResourceId() {
		if(resource != null) {
			return resource.getId();
		}
		return -1l;
	}
	
}
