package com.facilio.readingrule.faultimpact.command;

import java.util.List;
import org.apache.commons.chain.Context;

import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingrule.faultimpact.FaultImpactContext;
import com.facilio.util.DisplayNameToLinkNameUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import com.facilio.workflows.util.WorkflowUtil;

public class FaultImpactBeforeSaveCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<FaultImpactContext> faultImpacts = Constants.getRecordList((FacilioContext)context);
		
		for(FaultImpactContext faultImpact : faultImpacts) {
			
			validate(faultImpact);
			
			if(faultImpact.getId() < 0) {
				
				String linkName = DisplayNameToLinkNameUtil.getLinkName(faultImpact.getName(), FacilioConstants.FaultImpact.MODULE_NAME, "linkName");
				faultImpact.setLinkName(linkName);
				
			}
			faultImpact.getWorkflow().setIsV2Script(true);
			if(faultImpact.getWorkflowId() == null) {
				Long workflowId = WorkflowUtil.addWorkflow(faultImpact.getWorkflow());
				faultImpact.setWorkflowId(workflowId);
			}
			else {
				WorkflowUtil.updateWorkflow(faultImpact.getWorkflow(),faultImpact.getWorkflowId());
			}
		}
		return false;
	}

	private void validate(FaultImpactContext faultImpact) throws RESTException {

		 V3Util.throwRestException(faultImpact.getName() == null, ErrorCode.VALIDATION_ERROR, "Impact name cannot be empty");
		 V3Util.throwRestException(faultImpact.getTypeEnum() == null, ErrorCode.VALIDATION_ERROR, "Impact Scope cannot be empty");
		 V3Util.throwRestException(faultImpact.getWorkflow() == null || faultImpact.getWorkflow().getWorkflowV2String() == null , ErrorCode.VALIDATION_ERROR, "Impact workflow cannot be empty");
		 
		 switch(faultImpact.getTypeEnum()) {
			 case ASSET_CATEGORY: {
				 V3Util.throwRestException(faultImpact.getAssetCategory() == null, ErrorCode.VALIDATION_ERROR, "Asset Category cannot be empty");
				 break;
			 }
			 case SPACE_CATEGORY: {
				 V3Util.throwRestException(faultImpact.getSpaceCategory() == null, ErrorCode.VALIDATION_ERROR, "Space Category cannot be empty");
				 break;
			 }
		 }
	}
}
