package com.facilio.bmsconsoleV3.context.inspection;

import com.facilio.v3.context.V3Context;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InspectionPriorityContext extends V3Context {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String displayName;
	private String priority;
	private String description;
	private int sequenceNumber;
	private Boolean isDefault;
	private String colour;
	
}
