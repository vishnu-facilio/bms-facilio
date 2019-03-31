package com.facilio.workflows.util;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.workflows.context.WorkflowContext;

public class WorkflowLogUtil {
	
	private static final Logger LOGGER = Logger.getLogger(WorkflowLogUtil.class.getName());
	
	public static void addWorkflowLog(WorkflowContext workflowContext, Map<String, Object> paramMap, Object result) {
		
		try {
			if(workflowContext.getId() < 0) {
				return;
			}
			
			Map<String,Object> value = new HashMap<>();
			
			value.put("workflowId", workflowContext.getId());
			value.put("executionTime", DateTimeUtil.getCurrenTime());
			value.put("input", paramMap);
			value.put("variableMap", workflowContext.getVariableResultMap());
			value.put("result", result);
			
			GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder();
			insertRecordBuilder.table(ModuleFactory.getWorkflowLogModule().getTableName())
			.fields(FieldFactory.getWorkflowLogFields())
			.addRecord(value);
			
			insertRecordBuilder.save();
		}
		catch(Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

}
