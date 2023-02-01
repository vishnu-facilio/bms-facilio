package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateChangeSet;

public class ExecuteSingleWorkflowRuleCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(ExecuteSingleWorkflowRuleCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkflowRuleContext rule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		Map<String, List> recordMap = CommonCommandUtil.getRecordMap((FacilioContext) context);
		LOGGER.debug("Record Map : "+recordMap);
		Map<String, Map<Long, List<UpdateChangeSet>>> changeSetMap = CommonCommandUtil.getChangeSetMap((FacilioContext) context);
		
		if (rule != null && recordMap != null && !recordMap.isEmpty()) {
			Map<String, Object> placeHolders = WorkflowRuleAPI.getOrgPlaceHolders();
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
					boolean result = WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(rule, moduleName, record, changeSet, recordPlaceHolders, (FacilioContext) context);
					LOGGER.info("Result of record : "+((ModuleBaseWithCustomFields) record).getId()+" for for rule : "+rule.getId()+" is "+result);
				}
				
			}
		}
		
		return false;
	}

}
