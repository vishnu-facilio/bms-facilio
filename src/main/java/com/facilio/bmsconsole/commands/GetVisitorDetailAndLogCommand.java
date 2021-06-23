package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.time.DateTimeUtil;

public class GetVisitorDetailAndLogCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String passCode = (String)context.getOrDefault(FacilioConstants.ContextNames.PASSCODE, null);
		long currentTime = System.currentTimeMillis();
		
		if(StringUtils.isNotEmpty(passCode)) {
			VisitorLoggingContext vLog = VisitorManagementAPI.getVisitorLoggingTriggers(-1, passCode, false);
			if(vLog != null) {
				if(!vLog.isRecurring()) {
					if(vLog.getExpectedCheckInTime() > 0) {
						if(currentTime < vLog.getExpectedCheckInTime() && !DateTimeUtil.isSameDay(currentTime, vLog.getExpectedCheckInTime()))
						{
							throw new IllegalArgumentException("Invalid checkin time");
						}
					}
					if(vLog.getExpectedCheckOutTime() > 0 && currentTime > vLog.getExpectedCheckOutTime()) {
						throw new IllegalArgumentException("Invalid checkin time");
					}
					if(vLog != null) {
						List<WorkflowRuleContext> nextStateRule = StateFlowRulesAPI.getAvailableState(vLog.getStateFlowId(), vLog.getModuleState().getId(), FacilioConstants.ContextNames.VISITOR_LOGGING, vLog, (FacilioContext)context);
						if(CollectionUtils.isNotEmpty(nextStateRule)) {
							long nextTransitionId = nextStateRule.get(0).getId();
							context.put(FacilioConstants.ContextNames.TRANSITION_ID, nextTransitionId);
						}
					}
					context.put(FacilioConstants.ContextNames.VISITOR_LOGGING, vLog);
				}
				else {
					VisitorLoggingContext validChildLog = null;
					if(currentTime >= vLog.getExpectedCheckInTime() && currentTime <= vLog.getExpectedCheckOutTime()) {
						validChildLog = vLog;
					}
					else {
						validChildLog = VisitorManagementAPI.getValidChildLogForToday(vLog.getId(), currentTime, false, -1);
					}
					if(validChildLog == null) {
						throw new IllegalArgumentException("No valid invite log found for the day");
					}
					if(validChildLog != null) {
						List<WorkflowRuleContext> nextStateRule = StateFlowRulesAPI.getAvailableState(validChildLog.getStateFlowId(), validChildLog.getModuleState().getId(), FacilioConstants.ContextNames.VISITOR_LOGGING, validChildLog, (FacilioContext)context);
						long nextTransitionId = nextStateRule.get(0).getId();
						context.put(FacilioConstants.ContextNames.TRANSITION_ID, nextTransitionId);
					}
					context.put(FacilioConstants.ContextNames.VISITOR_LOGGING, validChildLog);
					
				}
				
				
			}
			
		}
		return false;
	}

}
