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
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
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
		if(rule.isConsolidatedAction()) {
			Map<String, List> recordMap = CommonCommandUtil.getRecordMap((FacilioContext) context);
			LOGGER.info("Record Map : "+recordMap);
			
			if (rule != null && recordMap != null && !recordMap.isEmpty()) {
				Map<String, Object> placeHolders = WorkflowRuleAPI.getOrgPlaceHolders();
				List<Map<String, Object>> recordList = new ArrayList<Map<String,Object>>();
				Map<String, Map<Long, List<UpdateChangeSet>>> changeSetMap = CommonCommandUtil.getChangeSetMap((FacilioContext) context);
				
				
				for (Map.Entry<String, List> entry : recordMap.entrySet()) {
					String moduleName = entry.getKey();
					if (moduleName == null || moduleName.isEmpty() || entry.getValue() == null || entry.getValue().isEmpty()) {
						LOGGER.log(Level.WARN, "Module Name / Records is null/ empty ==> "+moduleName+"==>"+entry.getValue());
						continue;
					}
					Map<Long, List<UpdateChangeSet>> currentChangeSet = changeSetMap == null ? null : changeSetMap.get(moduleName);
					
					for (Object record : entry.getValue()) {
						List<UpdateChangeSet> changeSet = currentChangeSet == null ? null : currentChangeSet.get( ((ModuleBaseWithCustomFields)record).getId() );
						Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(moduleName, record, placeHolders);
						boolean result = WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(rule, moduleName, record, changeSet, recordPlaceHolders, (FacilioContext) context, false);
						if(result) {
							recordList.add(FieldUtil.getAsProperties(record));
						}
					}
					Map<String, Object> placeHolders_rule = WorkflowRuleAPI.getOrgPlaceHolders();
					
					placeHolders_rule.put("records", recordList);
					placeHolders_rule.put("moduleName",moduleName);
				    List<ActionContext> actions = ActionAPI.getActiveActionsFromWorkflowRule(rule.getId());
				    for(ActionContext action : actions) {
				    	action.getTemplate().setFtl(true);
				        action.executeAction(placeHolders_rule, null, null, null);
				    }
				 }
				
			}
		}
		
		return false;
	}

	
}
