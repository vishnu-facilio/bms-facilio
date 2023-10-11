package com.facilio.workflows.command;

import java.util.List;
import java.util.Map;

import com.facilio.service.FacilioHttpStatus;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.workflowlog.context.WorkflowLogContext.WorkflowLogType;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class ExecuteWorkflowCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		WorkflowContext workflowContext = (WorkflowContext) context.get(WorkflowV2Util.WORKFLOW_CONTEXT);
		
		if(workflowContext.getWorkflowV2String() == null && workflowContext.getId() > 0) {
			WorkflowContext wf = WorkflowUtil.getWorkflowContext(workflowContext.getId());
			workflowContext.setWorkflowV2String(wf.getWorkflowV2String());
		}
		fillWorkflowContext(workflowContext, context);
		try{
			workflowContext.executeWorkflow();
			context.put("status", FacilioHttpStatus.SC_OK);
		}catch (Exception e) {
			context.put("status",FacilioHttpStatus.FC_SCRIPT_EXECUTION_ERROR);
			throw e;
		}
		return false;
	}

	void fillWorkflowContext(WorkflowContext workflowContext ,Context context) {
		
		workflowContext.setCachedRDM((Map<String, ReadingDataMeta>) context.get("rdmCache"));
		workflowContext.setIsV2Script(true);
		if(context.containsKey("ignoreMarked")) {
			workflowContext.setIgnoreMarkedReadings((boolean) context.get("ignoreMarked"));
		}
		
		if(context.containsKey(WorkflowV2Util.WORKFLOW_PARAMS)) {
			workflowContext.setParams((List<Object>) context.get(WorkflowV2Util.WORKFLOW_PARAMS));
		}

		if(context.containsKey("ignoreNullExpressions")) {
			workflowContext.setIgnoreNullParams((boolean) context.get("ignoreNullExpressions"));		// check and remove
		}
		
		if(context.get(FacilioConstants.Workflow.WORKFLOW_LOG_PARENT_ID) != null) {
			workflowContext.setParentId((long)context.get(FacilioConstants.Workflow.WORKFLOW_LOG_PARENT_ID));
		}
		
		if(context.get(FacilioConstants.Workflow.WORKFLOW_LOG_RECORD_ID) != null) {
			workflowContext.setRecordId((long)context.get(FacilioConstants.Workflow.WORKFLOW_LOG_RECORD_ID));
		}
		
		if(context.get(FacilioConstants.Workflow.WORKFLOW_LOG_PARENT_TYPE) != null) {
			workflowContext.setLogType((WorkflowLogType)context.get(FacilioConstants.Workflow.WORKFLOW_LOG_PARENT_TYPE));
		}

		if (context.get(FacilioConstants.Workflow.WORKFLOW_LOG_RECORD_MODULE_ID) != null) {
			workflowContext.setRecordModuleId((long)context.get(FacilioConstants.Workflow.WORKFLOW_LOG_RECORD_MODULE_ID));
		}
	}
}
