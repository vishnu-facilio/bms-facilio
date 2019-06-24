package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateChangeSet;

public class ExecuteConsolidatedActionWorkflowRuleCommand implements Command{

	private static final Logger LOGGER = LogManager.getLogger(ExecuteConsolidatedActionWorkflowRuleCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkflowRuleContext rule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		if(!rule.isConsolidatedAction()) {
			Map<String, List> recordMap = CommonCommandUtil.getRecordMap((FacilioContext) context);
			LOGGER.info("Record Map : "+recordMap);
			
			if (rule != null && recordMap != null && !recordMap.isEmpty()) {
				Map<String, Object> placeHolders = WorkflowRuleAPI.getOrgPlaceHolders();
				List<Map<String, Object>> recordList = new ArrayList<Map<String,Object>>();
				String moduleName = (String)context.get(FacilioConstants.ContextNames.MODULE_NAME);
				
				for (Map.Entry<String, List> entry : recordMap.entrySet()) {
					recordList.add(FieldUtil.getAsProperties(entry.getValue()));
				}
				placeHolders.put("records", recordList);
			    placeHolders.put("moduleName",moduleName);
		        WorkflowRuleAPI.evaluateConsolidatedWorkflowAndExecuteActions(rule, moduleName, null, null, placeHolders, (FacilioContext) context, true);
			}
		}
		
		return false;
	}

	
}
