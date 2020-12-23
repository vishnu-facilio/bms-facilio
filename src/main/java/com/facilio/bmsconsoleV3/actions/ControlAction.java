package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.v3.V3Action;

import con.facilio.control.ControlGroupContext;
import con.facilio.control.ControlGroupWrapper;
import con.facilio.control.ControlScheduleWrapper;

public class ControlAction extends V3Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ControlScheduleWrapper controlScheduleWrapper;
	ControlGroupWrapper controlGroupWrapper;
	
	public ControlScheduleWrapper getControlScheduleWrapper() {
		return controlScheduleWrapper;
	}

	public void setControlScheduleWrapper(ControlScheduleWrapper controlScheduleWrapper) {
		this.controlScheduleWrapper = controlScheduleWrapper;
	}
	public ControlGroupWrapper getControlGroupWrapper() {
		return controlGroupWrapper;
	}

	public void setControlGroupWrapper(ControlGroupWrapper controlGroupWrapper) {
		this.controlGroupWrapper = controlGroupWrapper;
	}
	
	public String addControlSchedule() throws Exception {
		FacilioChain chain = TransactionChainFactoryV3.getAddControlScheduleChain();
		FacilioContext context = chain.getContext();
		context.put(ControlScheduleUtil.CONTROL_SCHEDULE_WRAPPER, controlScheduleWrapper);
		chain.execute();
		setData(ControlScheduleUtil.CONTROL_SCHEDULE_WRAPPER, controlScheduleWrapper);
        return SUCCESS;
	}

	public String addControlGroup() throws Exception {
		try {
			FacilioChain chain = TransactionChainFactoryV3.getAddControlGroupChain();
			FacilioContext context = chain.getContext();
			context.put(ControlScheduleUtil.CONTROL_GROUP_WRAPPER, controlGroupWrapper);
			chain.execute();
			setData(ControlScheduleUtil.CONTROL_GROUP_WRAPPER, controlGroupWrapper);
			
			long groupId = controlGroupWrapper.getControlGroupContext().getId();
			
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
}
