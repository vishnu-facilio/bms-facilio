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
	
	public String addControlScheduleException() throws Exception {
		FacilioChain chain = TransactionChainFactoryV3.getAddControlScheduleExceptionChain();
		FacilioContext context = chain.getContext();
		context.put(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_CONTEXT, exception);
		chain.execute();
		setData(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_CONTEXT, exception);
        return SUCCESS;
	}
	
	public String updateControlScheduleException() throws Exception {
		FacilioChain chain = TransactionChainFactoryV3.getUpdateControlScheduleExceptionChain();
		FacilioContext context = chain.getContext();
		context.put(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_CONTEXT, exception);
		chain.execute();
		setData(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_CONTEXT, exception);
        return SUCCESS;
	}
	
	public String deleteControlScheduleException() throws Exception {
		FacilioChain chain = TransactionChainFactoryV3.getGenricDeleteChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, exception.getId());
		context.put(FacilioConstants.ContextNames.MODULE_NAME, ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_MODULE_NAME);
		chain.execute();
		setData(ControlScheduleUtil.CONTROL_SCHEDULE_CONTEXT, exception);
        return SUCCESS;
	}
	
	public String addControlGroupRoutine() throws Exception {
		FacilioChain chain = TransactionChainFactoryV3.getAddControlGroupRoutineChain();
		FacilioContext context = chain.getContext();
		context.put(ControlScheduleUtil.CONTROL_GROUP_ROUTINE_CONTEXT, routine);
		chain.execute();
		setData(ControlScheduleUtil.CONTROL_GROUP_ROUTINE_CONTEXT, routine);
        return SUCCESS;
	}
	
	public String updateControlGroupRoutine() throws Exception {
		FacilioChain chain = TransactionChainFactoryV3.getUpdateControlGroupRoutineChain();
		FacilioContext context = chain.getContext();
		context.put(ControlScheduleUtil.CONTROL_GROUP_ROUTINE_CONTEXT, routine);
		chain.execute();
		setData(ControlScheduleUtil.CONTROL_GROUP_ROUTINE_CONTEXT, routine);
        return SUCCESS;
	}
	
	public String deleteControlGroupRoutine() throws Exception {
		FacilioChain chain = TransactionChainFactoryV3.getGenricDeleteChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, routine.getId());
		context.put(FacilioConstants.ContextNames.MODULE_NAME, ControlScheduleUtil.CONTROL_GROUP_ROUTINE_MODULE_NAME);
		chain.execute();
		setData(ControlScheduleUtil.CONTROL_SCHEDULE_CONTEXT, routine);
        return SUCCESS;
	}
	
	
	public String addControlSchedule() throws Exception {
		FacilioChain chain = TransactionChainFactoryV3.getAddControlScheduleChain();
		FacilioContext context = chain.getContext();
		context.put(ControlScheduleUtil.CONTROL_SCHEDULE_CONTEXT, controlScheduleContext);
		chain.execute();
		setData(ControlScheduleUtil.CONTROL_SCHEDULE_CONTEXT, controlScheduleContext);
        return SUCCESS;
	}
	
	public String updateControlSchedule() throws Exception {
		FacilioChain chain = TransactionChainFactoryV3.getUpdateControlScheduleChain();
		FacilioContext context = chain.getContext();
		context.put(ControlScheduleUtil.CONTROL_SCHEDULE_CONTEXT, controlScheduleContext);
		chain.execute();
		setData(ControlScheduleUtil.CONTROL_SCHEDULE_CONTEXT, controlScheduleContext);
        return SUCCESS;
	}
	
	public String deleteControlSchedule() throws Exception {
		FacilioChain chain = TransactionChainFactoryV3.getGenricDeleteChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, controlScheduleContext.getId());
		context.put(FacilioConstants.ContextNames.MODULE_NAME, ControlScheduleUtil.CONTROL_SCHEDULE_MODULE_NAME);
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
			
	        return SUCCESS;
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
	}
	
	public String updateControlGroup() throws Exception {
		try {
			FacilioChain chain = TransactionChainFactoryV3.getUpdateControlGroupChain();
			FacilioContext context = chain.getContext();
			context.put(ControlScheduleUtil.CONTROL_GROUP_CONTEXT, controlGroupContext);
			context.put(ControlScheduleUtil.CONTROL_GROUP_CONTEXT_OLD, controlGroupContext);
			chain.execute();
			setData(ControlScheduleUtil.CONTROL_GROUP_CONTEXT, controlGroupContext);
			
	        return SUCCESS;
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
	}
	
	public String deleteControlGroup() throws Exception {
		try {
			FacilioChain chain = TransactionChainFactoryV3.getGenricDeleteChain();
			FacilioContext context = chain.getContext();
			context.put(FacilioConstants.ContextNames.RECORD_ID, controlGroupContext.getId());
			context.put(FacilioConstants.ContextNames.MODULE_NAME, ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME);
			chain.execute();
			setData(ControlScheduleUtil.CONTROL_SCHEDULE_CONTEXT, controlGroupContext);
			
	        return SUCCESS;
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
	}
}
