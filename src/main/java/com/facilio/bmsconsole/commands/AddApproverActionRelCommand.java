package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ApprovalRulesAPI;
import com.facilio.bmsconsole.workflow.rule.ApproverContext;
import com.facilio.constants.FacilioConstants;

public class AddApproverActionRelCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ApproverContext> approvers = (List<ApproverContext>) context.get(FacilioConstants.ContextNames.APPROVER_LIST);
		ApprovalRulesAPI.addApproverActionsRel(approvers);
		return false;
	}

}
