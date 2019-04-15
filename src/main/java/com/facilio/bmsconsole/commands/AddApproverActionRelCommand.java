package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.ApprovalRulesAPI;
import com.facilio.bmsconsole.workflow.rule.ApproverContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class AddApproverActionRelCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ApproverContext> approvers = (List<ApproverContext>) context.get(FacilioConstants.ContextNames.APPROVER_LIST);
		ApprovalRulesAPI.addApproverActionsRel(approvers);
		return false;
	}

}
