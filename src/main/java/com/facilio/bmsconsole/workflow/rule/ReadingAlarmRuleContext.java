package com.facilio.bmsconsole.workflow.rule;

import java.util.Map;

import com.facilio.bmsconsole.context.MLAlarmContext;
import com.facilio.bmsconsole.context.ReadingAlarmContext;
import com.facilio.chain.FacilioContext;

public class ReadingAlarmRuleContext extends WorkflowRuleContext {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	long readingRuleGroupId = -1l;

	public long getReadingRuleGroupId() {
		return readingRuleGroupId;
	}

	public void setReadingRuleGroupId(long readingRuleGroupId) {
		this.readingRuleGroupId = readingRuleGroupId;
	}
	
	@Override
	public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders,FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		long ruleId = record instanceof ReadingAlarmContext ? ((ReadingAlarmContext) record).getRuleId() : ((MLAlarmContext) record).getRuleId();
		if(ruleId == getReadingRuleGroupId()) {
			return true;
		}
		
		return false;
	}
}
