package com.facilio.workflows.util;

import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.time.DateTimeUtil;
import com.facilio.workflows.context.WorkflowContext;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
			value.put("variableMap", workflowContext.getVariableResultMap());
			value.put("input", paramMap);
			value.put("result", result);
			
			GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder();
			insertRecordBuilder.table(ModuleFactory.getWorkflowLogModule().getTableName())
			.fields(FieldFactory.getWorkflowLogFields())
			.addRecord(value);
			
			insertRecordBuilder.save();
		}
		catch(Exception e) {
			LOGGER.log(Level.SEVERE, "workflowContext -- "+workflowContext.getId() +" paramMap -- "+paramMap);
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

}
