package com.facilio.bmsconsoleV3.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.CommonActivityType;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsoleV3.context.V3ServiceRequestContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.v3.context.Constants;

public class FillActivityforServiceRequestCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<V3ServiceRequestContext> serviceRequests = Constants.getRecordList((FacilioContext) context);
		
		Map<Long, V3ServiceRequestContext> oldRecordMap = Constants.getOldRecordMap(context);
		
		for(V3ServiceRequestContext serviceRequest : serviceRequests) {
			
			V3ServiceRequestContext oldServiceRequest = oldRecordMap.get(serviceRequest.getId());
			
			if (serviceRequest.getAssignedTo() != null && serviceRequest.getAssignedTo().getId() != -1) {
				if(oldServiceRequest == null || (oldServiceRequest.getAssignedTo() == null || oldServiceRequest.getAssignedTo().getId() < 0) || (oldServiceRequest.getAssignedTo().getId() != serviceRequest.getAssignedTo().getId())) {
					addAssignmentActivity(serviceRequest, context,true);
				}
			}
			if (serviceRequest.getAssignmentGroup() != null && serviceRequest.getAssignmentGroup().getId() != -1) {
				if(oldServiceRequest == null || (oldServiceRequest.getAssignmentGroup() == null || oldServiceRequest.getAssignmentGroup().getId() < 0) || (oldServiceRequest.getAssignmentGroup().getId() != serviceRequest.getAssignmentGroup().getId())) {
					addAssignmentActivity(serviceRequest, context,false);
				}
			}
			
//			if( (serviceRequest.getAssignedTo() != null && serviceRequest.getAssignedTo().getId() != -1) || (serviceRequest.getAssignmentGroup() != null && serviceRequest.getAssignmentGroup().getId() != -1) ) {
//				
//				if(oldServiceRequest == null) {
//					addAssignmentActivity(serviceRequest, context);
//				}
//				else {
//					if(oldServiceRequest.getAssignedTo() == null || oldServiceRequest.getAssignedTo().getId() < 0) {
//						
//						if(serviceRequest.getAssignedTo() != null && serviceRequest.getAssignedTo().getId() != -1) {
//							addAssignmentActivity(serviceRequest, context);
//						}
//					}
//					else {
//						if(oldServiceRequest.getAssignedTo().getId() != serviceRequest.getAssignedTo().getId()) {
//							addAssignmentActivity(serviceRequest, context);
//						}
//					}
//					
//					if(oldServiceRequest.getAssignmentGroup() == null || oldServiceRequest.getAssignmentGroup().getId() < 0) {
//						
//						if(serviceRequest.getAssignmentGroup() != null && serviceRequest.getAssignmentGroup().getId() != -1) {
//							addAssignmentActivity(serviceRequest, context);
//						}
//					}
//					else {
//						if(oldServiceRequest.getAssignmentGroup().getId() != serviceRequest.getAssignmentGroup().getId()) {
//							addAssignmentActivity(serviceRequest, context);
//						}
//					}
//				}
//			}
		}
		return false;
	}
	
	
	private void addAssignmentActivity(V3ServiceRequestContext serviceRequest, Context context, boolean isFromAssignedTo) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        JSONObject info = new JSONObject();
        
        info.put("assignedBy", AccountUtil.getCurrentUser().getOuid());
        
        if (isFromAssignedTo) {
        	 info.put("assignedTo", serviceRequest.getAssignedTo().getOuid());
        }
        else {
        	info.put("assignmentGroup", serviceRequest.getAssignmentGroup().getId());
        }
        
        JSONObject newinfo = new JSONObject();
        newinfo.put("assigned", info);
        newinfo.put("moduleDisplayName", modBean.getModule(FacilioConstants.ContextNames.SERVICE_REQUEST).getDisplayName());
        CommonCommandUtil.addActivityToContext(serviceRequest.getId(), -1, CommonActivityType.ASSIGNED, newinfo, (FacilioContext) context);
    }

}
