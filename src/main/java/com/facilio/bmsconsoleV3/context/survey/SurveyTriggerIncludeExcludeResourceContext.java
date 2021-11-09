package com.facilio.bmsconsoleV3.context.survey;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.v3.context.V3Context;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SurveyTriggerIncludeExcludeResourceContext extends V3Context {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SurveyTemplateContext surveyTemplate;
	private SurveyTriggerContext surveyTrigger;
	private ResourceContext resource;
	private Boolean isInclude;
	
	
	public Long getSurveyTriggerId() {
		if(surveyTrigger != null) {
			return surveyTrigger.getId();
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
