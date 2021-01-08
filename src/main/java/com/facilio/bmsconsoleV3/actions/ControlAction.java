package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.v3.V3Action;

import con.facilio.control.ControlGroupContext;
import con.facilio.control.ControlGroupRoutineContext;
import con.facilio.control.ControlScheduleContext;
import con.facilio.control.ControlScheduleExceptionContext;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ControlAction extends V3Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ControlScheduleContext controlScheduleContext;
	ControlGroupContext controlGroupContext;
	ControlScheduleExceptionContext exception;
	ControlGroupRoutineContext routine;
	
}
