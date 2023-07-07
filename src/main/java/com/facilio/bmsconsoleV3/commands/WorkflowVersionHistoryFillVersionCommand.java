package com.facilio.bmsconsoleV3.commands;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PMResourcePlanner;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import com.facilio.workflowlog.context.WorkflowVersionHistoryContext;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class WorkflowVersionHistoryFillVersionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
    	HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
    	
    	List<WorkflowVersionHistoryContext> workflowVersionHistoryList = (List<WorkflowVersionHistoryContext>) recordMap.get(FacilioConstants.Workflow.WORKFLOW_VERSION_HISTORY);
    	
    	for(WorkflowVersionHistoryContext workflowVersionHistory : workflowVersionHistoryList) {
    		
    		int oldVersion = WorkflowV2Util.getWorkflowVersionHistoryMaxVersion(workflowVersionHistory.getWorkflowId());
    		workflowVersionHistory.setVersion(++oldVersion);
    	}
		// TODO Auto-generated method stub
		return false;
	}

}
