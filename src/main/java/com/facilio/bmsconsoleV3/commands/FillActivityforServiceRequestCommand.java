package com.facilio.bmsconsoleV3.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsoleV3.context.V3ServiceRequestContext;
import com.facilio.chain.FacilioContext;
import com.facilio.v3.context.Constants;

public class FillActivityforServiceRequestCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<V3ServiceRequestContext> serviceRequests = Constants.getRecordList((FacilioContext) context);
		
		for(V3ServiceRequestContext serviceRequest : serviceRequests) {
			
			if( (serviceRequest.getAssignedTo() != null && serviceRequest.getAssignedTo().getId() != -1) || (serviceRequest.getAssignmentGroup() != null && serviceRequest.getAssignmentGroup().getId() != -1) ) {
				addAssignmentActivity(serviceRequest, context);
			}
		}
		return false;
	}
	
	
	private void addAssignmentActivity(V3ServiceRequestContext serviceRequest, Context context) {
        JSONObject info = new JSONObject();
        
        info.put("assignedBy", AccountUtil.getCurrentUser().getOuid());
        
        if (serviceRequest.getAssignedTo() != null && serviceRequest.getAssignedTo().getId() != -1) {
        	 info.put("assignedTo", serviceRequest.getAssignedTo().getOuid());
        }
        else {
        	info.put("assignmentGroup", serviceRequest.getAssignmentGroup().getId());
        }
        
        JSONObject newinfo = new JSONObject();
        newinfo.put("assigned", info);
        CommonCommandUtil.addActivityToContext(serviceRequest.getId(), -1, WorkOrderActivityType.ASSIGN, newinfo, (FacilioContext) context);
    }

}
