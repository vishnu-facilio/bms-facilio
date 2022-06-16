package com.facilio.bmsconsoleV3.commands.visitorlog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.context.BaseVisitContextV3;
import com.facilio.bmsconsoleV3.context.VisitorLogContextV3;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;

public class  LoadRecordIdForPassCodeCommandV3 extends FacilioCommand {
	
	@Override
    public boolean executeCommand(Context context) throws Exception {
		
		String moduleName = Constants.getModuleName(context); 
		Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        Map<String, List<Object>> queryParams = (Map<String, List<Object>>)context.get(Constants.QUERY_PARAMS);
        List<Long> recordIds = (List<Long>)context.get(Constants.RECORD_ID_LIST);
        
        if(!moduleName.equals(FacilioConstants.ContextNames.BASE_VISIT) && !moduleName.equals(FacilioConstants.ContextNames.VISITOR_LOG) && !moduleName.equals(FacilioConstants.ContextNames.INVITE_VISITOR)) {
        	return false;
        }
        
        if(MapUtils.isNotEmpty(queryParams) && !queryParams.containsKey("id")) {
        	if(queryParams.containsKey("passCode") && queryParams.get("passCode") != null && !queryParams.get("passCode").isEmpty()){
        		if(StringUtils.isNotEmpty((String)queryParams.get("passCode").get(0))) {
					String passCode= ((String) queryParams.get("passCode").get(0)).replaceAll("[^0-9.]","");
					if(passCode != null && !passCode.isEmpty()) {
						List<BaseVisitContextV3> baseVisits = V3VisitorManagementAPI.getBaseVisit(-1l, passCode, false);
						if (baseVisits != null && !baseVisits.isEmpty()) {
							BaseVisitContextV3 inviteBaseVisit = null;
							List<Long> checkInBaseVisitIds = new ArrayList<Long>();
							List<BaseVisitContextV3> visitorLogList = new ArrayList<BaseVisitContextV3>();
							for (BaseVisitContextV3 baseVisit : baseVisits) {
								if (baseVisit.getChildVisitTypeEnum() == BaseVisitContextV3.ChildVisitType.INVITE) {
									inviteBaseVisit = baseVisit;
								} else if (baseVisit.getChildVisitTypeEnum() == BaseVisitContextV3.ChildVisitType.VISIT) {
									visitorLogList.add(baseVisit);
								}
							}
							if (!visitorLogList.isEmpty()) {
								BaseVisitContextV3 lastVistiorlog = visitorLogList.get(visitorLogList.size() - 1);
								checkInBaseVisitIds.add(lastVistiorlog.getId());
								if (lastVistiorlog.getModuleState().getStatus().equals("CheckedOut") && queryParams.containsKey("checkOut")) {
									throw new RESTException(ErrorCode.NO_ACTIVE_CHECK_IN_FOR_INVITE_CODE, "No active check-in found for the invite code");
								} else if (lastVistiorlog.getModuleState().getStatus().equals("CheckedIn") && !queryParams.containsKey("checkOut")) {
									throw new RESTException(ErrorCode.ALREADY_CHECKED_IN, "You are already checked-in using this invite");
								} else if (lastVistiorlog.getModuleState().getStatus().equals("CheckedOut") && inviteBaseVisit == null) {
									throw new RESTException(ErrorCode.INVITE_EXPIRED, "Not a valid invite code");
								}
							}
							if (!checkInBaseVisitIds.isEmpty() && queryParams.containsKey("checkOut")) {
								context.put(Constants.RECORD_ID_LIST, checkInBaseVisitIds);
								recordIds = checkInBaseVisitIds;
							} else if (inviteBaseVisit != null) {
								context.put(Constants.RECORD_ID_LIST, Collections.singletonList(inviteBaseVisit.getId()));
								recordIds = Collections.singletonList(inviteBaseVisit.getId());
							}
							List<Object> recordIdParam = new ArrayList<Object>();
							recordIdParam.addAll(recordIds);
							queryParams.put("id", recordIdParam);
						}
						else
						{
							throw new RESTException(ErrorCode.INVALID_INVITE_CODE, "Invalid invite code");
						}
					}
					else
					{
						throw new RESTException(ErrorCode.INVALID_INVITE_CODE, "Invalid invite code");
					}
        		}
        	}
        	else if(queryParams.containsKey("contactNumber") && queryParams.get("contactNumber") != null && !queryParams.get("contactNumber").isEmpty() && queryParams.containsKey("checkOut") && queryParams.get("checkOut") != null && !queryParams.get("checkOut").isEmpty()){
        		if(StringUtils.isNotEmpty((String)queryParams.get("contactNumber").get(0)) && moduleName.equals(FacilioConstants.ContextNames.VISITOR_LOG) && StringUtils.isNotEmpty((String)queryParams.get("checkOut").get(0)) && Boolean.parseBoolean((String)queryParams.get("checkOut").get(0))) {	
        			VisitorLogContextV3 activeLog = V3VisitorManagementAPI.checkOutVisitorLog((String)queryParams.get("contactNumber").get(0), (FacilioContext)context);
        			if(activeLog != null) {
        				context.put(Constants.RECORD_ID_LIST, Collections.singletonList(activeLog.getId()));
        				context.put(Constants.RECORD_ID, activeLog.getId());
        				context.put("id", activeLog.getId());
    					recordIds = Collections.singletonList(activeLog.getId());
        				
            			List<Object> recordIdParam = new ArrayList<Object>();
            			recordIdParam.addAll(recordIds);
            			queryParams.put("id", recordIdParam);

						if(MapUtils.isNotEmpty(recordMap)){
							List<VisitorLogContextV3> visitorLogs = recordMap.get(moduleName);
							if(CollectionUtils.isNotEmpty(visitorLogs) && visitorLogs.get(0).getId() == activeLog.getId()) {
								visitorLogs.get(0).setCheckOutTime(System.currentTimeMillis());
								List<WorkflowRuleContext> nextStateRule = StateFlowRulesAPI.getAvailableState(activeLog.getStateFlowId(), activeLog.getModuleState().getId(), FacilioConstants.ContextNames.VISITOR_LOG, activeLog, (FacilioContext)context);
								long nextTransitionId = nextStateRule.get(0).getId();
								context.put(FacilioConstants.ContextNames.TRANSITION_ID, nextTransitionId);

							}
						}
        			}
         		}
        	}
        	else if(queryParams.containsKey("recordId") && queryParams.get("recordId") != null && !queryParams.get("recordId").isEmpty()){
        		if(queryParams.get("recordId").get(0) != null && (Long)queryParams.get("recordId").get(0) != -1l) {
        			recordIds.add(0, (Long)queryParams.get("recordId").get(0));
        			List<Object> recordIdParam = new ArrayList<Object>();
        			recordIdParam.add(queryParams.get("recordId").get(0));
        			queryParams.put("id", recordIdParam);
        		}	
        	}
        	else {
//                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Please input a valid passcode or qr");
        	}	
        }
		else if(MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("id") && queryParams.containsKey("checkOut") && queryParams.get("checkOut") != null && !queryParams.get("checkOut").isEmpty() && Boolean.parseBoolean((String)queryParams.get("checkOut").get(0))){
			if(MapUtils.isNotEmpty(recordMap)){
				List<VisitorLogContextV3> visitorLogs = recordMap.get(moduleName);
				if(CollectionUtils.isNotEmpty(visitorLogs)) {
					visitorLogs.get(0).setCheckOutTime(System.currentTimeMillis());
					List<WorkflowRuleContext> nextStateRule = StateFlowRulesAPI.getAvailableState(visitorLogs.get(0).getStateFlowId(), visitorLogs.get(0).getModuleState().getId(), FacilioConstants.ContextNames.VISITOR_LOG, visitorLogs.get(0), (FacilioContext)context);
					long nextTransitionId = nextStateRule.get(0).getId();
					context.put(FacilioConstants.ContextNames.TRANSITION_ID, nextTransitionId);

				}
			}
		}
	    
		return false;
		
	}
}
