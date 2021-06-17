package com.facilio.bmsconsole.jobs;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;

public class AssetActionJob extends FacilioJob {

	private static final Logger LOGGER = LogManager.getLogger(AssetActionJob.class.getName());
	@Override
	public void execute(JobContext jc) {
		try {
			List<WorkflowRuleContext> workflowRules = WorkflowRuleAPI.getAllWorkflowRuleContextOfType(RuleType.ASSET_ACTION_RULE, true,true);
			
			FacilioContext context = new FacilioContext();
			context.put("workflowRules", workflowRules);
			FacilioChain assetAction = FacilioChainFactory.getAssetActionChain();
			assetAction.execute(context);
		}
		catch(Exception e) {
			LOGGER.error("Asset notification job failed", e);
			CommonCommandUtil.emailException(AssetActionJob.class.getName(), "asset notification Failed -- "+AccountUtil.getCurrentOrg().getId(), e);
		}
	}

}
