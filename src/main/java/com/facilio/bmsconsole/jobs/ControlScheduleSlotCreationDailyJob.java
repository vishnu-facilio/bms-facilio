package com.facilio.bmsconsole.jobs;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.ControlGroupContext;
import com.facilio.control.ControlGroupTenentContext;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateTimeUtil;

public class ControlScheduleSlotCreationDailyJob extends FacilioJob {

	private static final Logger LOGGER = LogManager.getLogger(ControlScheduleSlotCreationJob.class.getName());
	@Override
	public void execute(JobContext jc) throws Exception {
		// TODO Auto-generated method stub
		
		
		try {
			
			long currentDate = DateTimeUtil.getDayStartTime();
			
			long startTime = DateTimeUtil.addDays(currentDate, 30);
			
			long endTime = DateTimeUtil.getDayEndTimeOf(startTime);
			
			List<ControlGroupContext> allParentControlGroups =  ControlScheduleUtil.fetchRecord(ControlGroupContext.class, ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME, null, null);
			
			if(allParentControlGroups != null) {
				for(ControlGroupContext controlGroupContext : allParentControlGroups) {
					
					controlGroupContext = ControlScheduleUtil.getControlGroup(controlGroupContext.getId());
					
					FacilioChain chain = TransactionChainFactoryV3.planControlGroupSlotsAndRoutines();
					
					FacilioContext newContext = chain.getContext();
					
					newContext.put(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME, controlGroupContext);
					newContext.put(FacilioConstants.ContextNames.START_TIME, startTime);
					newContext.put(FacilioConstants.ContextNames.END_TIME, endTime);
					
					chain.execute();
				}
			}
			
			List<ControlGroupTenentContext> allChildGroups =  ControlScheduleUtil.fetchRecord(ControlGroupTenentContext.class, ControlScheduleUtil.CONTROL_GROUP_TENANT_SHARING_MODULE_NAME, null, null);
			
			if(allChildGroups != null) {
				for(ControlGroupContext controlGroupContext : allChildGroups) {
					
					controlGroupContext = ControlScheduleUtil.getControlGroup(controlGroupContext.getId());
					
					FacilioChain chain = TransactionChainFactoryV3.planControlGroupSlotsAndRoutines();
					
					FacilioContext newContext = chain.getContext();
					
					newContext.put(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME, controlGroupContext);
					newContext.put(FacilioConstants.ContextNames.START_TIME, startTime);
					newContext.put(FacilioConstants.ContextNames.END_TIME, endTime);
					
					chain.execute();
				}
			}
			
		}
		catch(Exception e) {
			LOGGER.error("ControlScheduleSlotCreation Daily Job Failed", e);
		}
	}

	
}
