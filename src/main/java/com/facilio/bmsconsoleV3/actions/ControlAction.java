package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.v3.V3Action;

import con.facilio.control.ControlGroupContext;
import con.facilio.control.ControlScheduleContext;

public class ControlAction extends V3Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ControlScheduleContext controlScheduleContext;
	ControlGroupContext controlGroupContext;
	
	
	
	public String addControlSchedule() throws Exception {
		FacilioChain chain = TransactionChainFactoryV3.getAddControlScheduleChain();
		FacilioContext context = chain.getContext();
		context.put(ControlScheduleUtil.CONTROL_SCHEDULE_CONTEXT, controlScheduleContext);
		chain.execute();
		setData(ControlScheduleUtil.CONTROL_SCHEDULE_CONTEXT, controlScheduleContext);
        return SUCCESS;
	}

	public String addControlGroup() throws Exception {
		try {
			FacilioChain chain = TransactionChainFactoryV3.getAddControlGroupChain();
			FacilioContext context = chain.getContext();
			context.put(ControlScheduleUtil.CONTROL_GROUP_CONTEXT, controlGroupContext);
			chain.execute();
			setData(ControlScheduleUtil.CONTROL_GROUP_CONTEXT, controlGroupContext);
			
			long groupId = controlGroupContext.getId();
			
			FacilioChain chain1 = TransactionChainFactoryV3.planControlGroupSlotsAndRoutines();
			
			FacilioContext context1 = chain1.getContext();
			
			context1.put(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME, ControlScheduleUtil.getControlGroup(groupId));
			
			chain1.execute();
			
	        return SUCCESS;
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
	}

	public ControlGroupContext getControlGroupContext() {
		return controlGroupContext;
	}

	public void setControlGroupContext(ControlGroupContext controlGroupContext) {
		this.controlGroupContext = controlGroupContext;
	}

	public ControlScheduleContext getControlScheduleContext() {
		return controlScheduleContext;
	}

	public void setControlScheduleContext(ControlScheduleContext controlScheduleContext) {
		this.controlScheduleContext = controlScheduleContext;
	}
}
