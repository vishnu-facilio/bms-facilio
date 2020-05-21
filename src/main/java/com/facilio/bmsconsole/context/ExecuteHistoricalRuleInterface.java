package com.facilio.bmsconsole.context;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.map.SingletonMap;

import com.facilio.time.DateRange;

public interface ExecuteHistoricalRuleInterface {
	
	public List<BaseEventContext> executeRuleAndGenerateEvents(String messageKey, DateRange dateRange, HashMap<String, Boolean> jobStatesMap, long jobId) throws Exception;
	
	public HashMap<Long, List<Long>> getRuleAndResourceIds(long ruleOrResourceId, boolean isInclude, List<Long> selectedResourceIds) throws Exception;

}
