package com.facilio.bmsconsole.workflow.rule;

import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.MLAlarmContext;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.bmsconsole.context.ReadingAlarmContext;
import com.facilio.bmsconsole.context.ReadingRCAAlarm;
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
	long rcaRuleId = -1l;
	long resourceId = -1;
	
	public long getRcaRuleId() {
		return rcaRuleId;
	}
	public void setRcaRuleId(long rcaRuleId) {
		this.rcaRuleId = rcaRuleId;
	}
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}
	@Override
	public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders,FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_ALARMS)) {
			
			if(record instanceof ReadingAlarm || record instanceof ReadingRCAAlarm) {
				
				boolean ruleFlag = false,resourceFlag = true;
				
				if(rcaRuleId > 0 ) {
					if(record instanceof ReadingRCAAlarm) {
						ReadingRCAAlarm readingRCAAlarm = (ReadingRCAAlarm)record;
						if(getReadingRuleGroupId() == readingRCAAlarm.getRuleId() && readingRCAAlarm.getSubRuleId() == rcaRuleId) {
							ruleFlag = true;
						}
					}
				}
				else {
					if(record instanceof ReadingAlarm) {
						ReadingAlarm readingAlarm = (ReadingAlarm)record;
						if(getReadingRuleGroupId() == readingAlarm.getRule().getId()) {
							ruleFlag = true;
						}
					}
				}
				
				if(resourceId > 0) {
					resourceFlag = false;
					BaseAlarmContext baseAlarmContext = (BaseAlarmContext)record;
					if(baseAlarmContext.getResource() != null && baseAlarmContext.getResource().getId() == getResourceId()) {
						resourceFlag = true;
					}
				}
				
				return ruleFlag && resourceFlag;
			}
		}
		else {
			if(record instanceof ReadingAlarmContext || record instanceof MLAlarmContext) {
				
				long ruleId = record instanceof ReadingAlarmContext ? ((ReadingAlarmContext) record).getRuleId() : ((MLAlarmContext) record).getRuleId();
				
				boolean ruleFlag = false,resourceFlag = true;
				if(ruleId == getReadingRuleGroupId()) {
					ruleFlag = true;
				}
				
				if(resourceId > 0) {
					resourceFlag = false;
					try {
						long resourceId = record instanceof ReadingAlarmContext ? ((ReadingAlarmContext) record).getResource().getId() : ((MLAlarmContext) record).getResource().getId();
						if(resourceId == getResourceId()) {
							resourceFlag = true;
						}
					}
					catch(Exception e) {
						
					}
				}
				
				return ruleFlag && resourceFlag;
			}
		}
		
		return false;
	}
}
