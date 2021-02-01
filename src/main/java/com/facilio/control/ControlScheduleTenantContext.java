package com.facilio.control;

import com.facilio.bmsconsole.tenant.TenantContext;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ControlScheduleTenantContext extends ControlScheduleContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ControlScheduleContext parentSchedule;
	TenantContext tenant;
	ControlGroupContext parentGroup;

}
