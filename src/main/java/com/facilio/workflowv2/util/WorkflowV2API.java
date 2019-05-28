package com.facilio.workflowv2.util;

import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.workflows.context.WorkflowContext;

public class WorkflowV2API {
	
	public static Object executeWorkflow(WorkflowContext workflowContext,List<Object> params, Map<String, ReadingDataMeta> rdmCache, boolean ignoreNullExpressions, boolean ignoreMarked, boolean isVariableMapNeeded) throws Exception {
		workflowContext.setCachedRDM(rdmCache);
		workflowContext.setIgnoreMarkedReadings(ignoreMarked);
		
		workflowContext.setParams(params);
		
		workflowContext.setIgnoreNullParams(ignoreNullExpressions);		// check and remove
		
		Object result = workflowContext.executeWorkflow();
		
		if(isVariableMapNeeded) {
			return workflowContext.getVariableResultMap();
		}
		else {
			return result;
		}
	}

}
