package com.facilio.bmsconsoleV3.context.inspection;

import com.facilio.v3.context.V3Context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class InspectionCategoryContext extends V3Context {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String displayName;
	private String name;
	private String description;
	
	
}
