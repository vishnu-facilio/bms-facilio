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

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.bmsconsoleV3.context.BaseVisitContextV3;
import com.facilio.bmsconsoleV3.context.VisitorLogContextV3;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;

public class LoadRecordIdForPassCodeCommandV3 extends FacilioCommand {
	
	@Override
    public boolean executeCommand(Context context) throws Exception {
		
		String moduleName = Constants.getModuleName(context);   
        Map<String, List<Object>> queryParams = (Map<String, List<Object>>)context.get(Constants.QUERY_PARAMS);
        List<Long> recordIds = (List<Long>)context.get(Constants.RECORD_ID_LIST);
        
        if(!moduleName.equals(FacilioConstants.ContextNames.BASE_VISIT) && !moduleName.equals(FacilioConstants.ContextNames.VISITOR_LOG) && !moduleName.equals(FacilioConstants.ContextNames.INVITE_VISITOR)) {
        	return false;
        }
        
        if((recordIds == null||recordIds.isEmpty()||recordIds.get(0) == 0l) && (MapUtils.isNotEmpty(queryParams) && !queryParams.containsKey("id"))) {
        	
        	if(queryParams.containsKey("passCode") && queryParams.get("passCode") != null && !queryParams.get("passCode").isEmpty()){
        		if(StringUtils.isNotEmpty((String)queryParams.get("passCode").get(0))) {	
        			List<BaseVisitContextV3> baseVisits = V3VisitorManagementAPI.getBaseVisit(-1l, (String)queryParams.get("passCode").get(0), false);
        			if(baseVisits != null && !baseVisits.isEmpty()) {
        				BaseVisitContextV3 inviteBaseVisit = null;
        				List<Long> checkInBaseVisitIds = new ArrayList<Long>();
        				
        				for(BaseVisitContextV3 baseVisit:baseVisits) {
        					if(baseVisit.getChildVisitTypeEnum() == BaseVisitContextV3.ChildVisitType.INVITE) {
        						inviteBaseVisit = baseVisit;
        					}
        					else if(baseVisit.getChildVisitTypeEnum() == BaseVisitContextV3.ChildVisitType.VISIT) {
        						checkInBaseVisitIds.add(baseVisit.getId());
        					}
        				}
        				
        				if(inviteBaseVisit == null && !checkInBaseVisitIds.isEmpty()) {
        					context.put(Constants.RECORD_ID_LIST, checkInBaseVisitIds);
        					recordIds = checkInBaseVisitIds;
        				}
        				else if(inviteBaseVisit != null) {
        					context.put(Constants.RECORD_ID_LIST, Collections.singletonList(inviteBaseVisit.getId()));
        					recordIds = Collections.singletonList(inviteBaseVisit.getId());
        				}
            			List<Object> recordIdParam = new ArrayList<Object>();
            			recordIdParam.addAll(recordIds);
            			queryParams.put("id", recordIdParam);		
        			}
        		}	
        	}
        	else if(queryParams.containsKey("contactNumber") && queryParams.get("contactNumber") != null && !queryParams.get("contactNumber").isEmpty()){
        		if(StringUtils.isNotEmpty((String)queryParams.get("contactNumber").get(0)) && moduleName.equals(FacilioConstants.ContextNames.VISITOR_LOG) && (Boolean)queryParams.get("checkOut").get(0) != null && (Boolean)queryParams.get("checkOut").get(0)) {	
        			VisitorLogContextV3 activeLog = V3VisitorManagementAPI.checkOutVisitorLog((String)queryParams.get("contactNumber").get(0), (FacilioContext)context);
        			if(activeLog != null) {
        				context.put(Constants.RECORD_ID_LIST, Collections.singletonList(activeLog.getId()));
        				context.put(Constants.RECORD_ID, activeLog.getId());
        				context.put("id", activeLog.getId());
    					recordIds = Collections.singletonList(activeLog.getId());
        				
            			List<Object> recordIdParam = new ArrayList<Object>();
            			recordIdParam.addAll(recordIds);
            			queryParams.put("id", recordIdParam);
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
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Please input a valid passcode or qr");
        	}	
        }
	    
		return false;
		
	}
}
