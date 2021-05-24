package com.facilio.bmsconsoleV3.context.induction;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.v3.context.V3Context;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InductionTriggerIncludeExcludeResourceContext extends V3Context {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private InductionTemplateContext inductionTemplate;
	private InductionTriggerContext inductionTrigger;
	private ResourceContext resource;
	private Boolean isInclude;
	
	
	public Long getInductionTriggerId() {
		if(inductionTrigger != null) {
			return inductionTrigger.getId();
		}
		return -1l;
	}
	
}