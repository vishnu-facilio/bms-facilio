package com.facilio.bmsconsole.jobs;

import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext.RuleType;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class AssetActionJob extends FacilioJob {

	private static final Logger LOGGER = LogManager.getLogger(AssetActionJob.class.getName());
	@Override
	public void execute(JobContext jc) {
		try {
			List<WorkflowRuleContext> workflowRules = WorkflowRuleAPI.getAllWorkflowRuleContextOfType(RuleType.ASSET_ACTION_RULE, false, true);
			
			FacilioContext context = new FacilioContext();
			context.put("workflowRules", workflowRules);
			Chain assetAction = FacilioChainFactory.getAssetActionChain();
			assetAction.execute(context);
		}
		catch(Exception e) {
			LOGGER.error("Asset notification job failed", e);
			CommonCommandUtil.emailException(AssetActionJob.class.getName(), "asset notification Failed -- "+AccountUtil.getCurrentOrg().getId(), e);
		}
	}

}
