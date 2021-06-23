package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.SharingAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleFactory;


public class AddPrerequisiteApproversCommand extends FacilioCommand {
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		SharingContext<SingleSharingContext> prerequisiteApproversList = (SharingContext<SingleSharingContext>) context.get(FacilioConstants.ContextNames.PREREQUISITE_APPROVERS_LIST);
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		if(prerequisiteApproversList != null && !prerequisiteApproversList.isEmpty()) {
		  SharingAPI.addSharing(prerequisiteApproversList, workOrder.getId(), ModuleFactory.getPrerequisiteApproversModule());
		}
		return false;
	}
}
