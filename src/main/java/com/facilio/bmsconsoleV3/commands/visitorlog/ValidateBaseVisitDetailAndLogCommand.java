package com.facilio.bmsconsoleV3.commands.visitorlog;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.context.BaseVisitContextV3;
import com.facilio.bmsconsoleV3.context.InviteVisitorContextV3;
import com.facilio.bmsconsoleV3.context.V3VisitorLoggingContext;
import com.facilio.bmsconsoleV3.context.VisitorLogContextV3;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;

public class ValidateBaseVisitDetailAndLogCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		 String moduleName = Constants.getModuleName(context);
	        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
	        List<BaseVisitContextV3> baseVisits = recordMap.get(moduleName);
			long currentTime = System.currentTimeMillis();

	        if(CollectionUtils.isNotEmpty(baseVisits)) {
				for(BaseVisitContextV3 baseVisit:baseVisits) {
					if(baseVisit.getChildVisitTypeEnum() == BaseVisitContextV3.ChildVisitType.INVITE) {
						InviteVisitorContextV3 inviteVisit = V3VisitorManagementAPI.getInviteVisit(baseVisit.getId(), null, false);
						if(inviteVisit != null) {
							if(!inviteVisit.isRecurring()) {
								if(inviteVisit.getExpectedCheckInTime() > 0) {
									if(currentTime < inviteVisit.getExpectedCheckInTime() && !DateTimeUtil.isSameDay(currentTime, inviteVisit.getExpectedCheckInTime()))
									{
										throw new IllegalArgumentException("Invalid checkin time");
									}
								}
								if(inviteVisit.getExpectedCheckOutTime() > 0 && currentTime > inviteVisit.getExpectedCheckOutTime()) {
									throw new IllegalArgumentException("Invalid checkout time");
								}
								if(inviteVisit != null) {
									List<WorkflowRuleContext> nextStateRule = StateFlowRulesAPI.getAvailableState(inviteVisit.getStateFlowId(), inviteVisit.getModuleState().getId(), FacilioConstants.ContextNames.INVITE_VISITOR, inviteVisit, (FacilioContext)context);
									if(CollectionUtils.isNotEmpty(nextStateRule)) {
										long nextTransitionId = nextStateRule.get(0).getId();
										context.put(FacilioConstants.ContextNames.TRANSITION_ID, nextTransitionId);
									}
								}
							}
							else {
								InviteVisitorContextV3 validChildLog = null;
								if(currentTime >= inviteVisit.getExpectedCheckInTime() && currentTime <= inviteVisit.getExpectedCheckOutTime()) {
									validChildLog = inviteVisit;
								}
								else {
									validChildLog = V3VisitorManagementAPI.getValidChildLogForToday(inviteVisit.getId(), currentTime, false, -1); //recurring change inviteVisitid
								}
								if(validChildLog == null) {
									throw new IllegalArgumentException("No valid invite log found for the day");
								}
								baseVisit = validChildLog;
								if(validChildLog != null) {
									List<WorkflowRuleContext> nextStateRule = StateFlowRulesAPI.getAvailableState(validChildLog.getStateFlowId(), validChildLog.getModuleState().getId(), FacilioConstants.ContextNames.INVITE_VISITOR, validChildLog, (FacilioContext)context);
									long nextTransitionId = nextStateRule.get(0).getId();
									context.put(FacilioConstants.ContextNames.TRANSITION_ID, nextTransitionId);
								}								
							}	
						}
					}
					else if(baseVisit.getChildVisitTypeEnum() == BaseVisitContextV3.ChildVisitType.VISIT) {
						VisitorLogContextV3 vLog = V3VisitorManagementAPI.getActiveVisitorLogById(baseVisit.getVisitor().getId(), true, baseVisit.getId());
						if(vLog != null) {
							baseVisit = vLog;
							if(vLog != null) {
								List<WorkflowRuleContext> nextStateRule = StateFlowRulesAPI.getAvailableState(vLog.getStateFlowId(), vLog.getModuleState().getId(), FacilioConstants.ContextNames.VISITOR_LOG, vLog, (FacilioContext)context);
								long nextTransitionId = nextStateRule.get(0).getId();
								context.put(FacilioConstants.ContextNames.TRANSITION_ID, nextTransitionId);
							}
						}
						else {
							throw new IllegalArgumentException("No active visitor logs");
						}
					}
					else {
						throw new IllegalArgumentException("Invalid Passcode/QR");
					}
					
				}
	        	
	        }
	        
		return false;
	}

}
