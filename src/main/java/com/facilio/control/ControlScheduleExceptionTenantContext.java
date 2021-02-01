package com.facilio.control;

import com.facilio.bmsconsole.tenant.TenantContext;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ControlScheduleExceptionTenantContext extends ControlScheduleExceptionContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ControlScheduleExceptionContext parentException;
	TenantContext tenant;
	ControlGroupContext parentGroup;

}
