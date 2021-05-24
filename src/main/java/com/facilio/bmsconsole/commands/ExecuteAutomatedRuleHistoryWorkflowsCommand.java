package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class ExecuteAutomatedRuleHistoryWorkflowsCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Map<String, String> orgInfoMap = CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.EXECUTE_READING_RULE_THROUGH_AUTOMATED_HISTORY);
    	if (orgInfoMap != null && MapUtils.isNotEmpty(orgInfoMap)) {
    		String executeReadingRuleThroughAutomatedHistoryProp = orgInfoMap.get(FacilioConstants.OrgInfoKeys.EXECUTE_READING_RULE_THROUGH_AUTOMATED_HISTORY);
			if (executeReadingRuleThroughAutomatedHistoryProp != null && !executeReadingRuleThroughAutomatedHistoryProp.isEmpty() && StringUtils.isNotEmpty(executeReadingRuleThroughAutomatedHistoryProp) && Boolean.valueOf(executeReadingRuleThroughAutomatedHistoryProp)) {
				FacilioChain c = FacilioChain.getTransactionChain();
				c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.MODULE_RULE));
				c.addCommand(new ExecuteAllWorkflowsCommand(false,RuleType.ALARM_WORKFLOW_RULE));
				c.execute(context);
			}
			
    	}
    	
		return false;
	}

}
