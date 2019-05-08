package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadAlarmCreationRulesCommand implements Command {

	private static Logger log = LogManager.getLogger(LoadAlarmCreationRulesCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		try {
			GenericSelectRecordBuilder workflowBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getWorkflowRuleFields())
					.table("Workflow_Rule")
					.andCustomWhere("Workflow_Rule.ORGID = ? AND Workflow_Rule.NAME IN ('Create Alarm - EMail', 'Create Alarm - SMS')", AccountUtil.getCurrentOrg().getOrgId());
			
			List<Map<String, Object>> rules = workflowBuilder.get();
			
			if(rules != null && !rules.isEmpty()) {
				List<WorkflowRuleContext> alarmCreationRules = new ArrayList<>();
				for(Map<String, Object> rule : rules) {
					WorkflowRuleContext workflow = FieldUtil.getAsBeanFromMap(rule, WorkflowRuleContext.class);
					alarmCreationRules.add(workflow);
				}
				
				context.put(FacilioConstants.Workflow.WORKFLOW_LIST, alarmCreationRules);
			}
		}
		catch(SQLException e) {
			log.info("Exception occurred ", e);
			throw e;
		}
		return false;
	}
}
