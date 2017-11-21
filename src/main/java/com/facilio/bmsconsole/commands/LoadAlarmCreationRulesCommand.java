package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class LoadAlarmCreationRulesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder workflowBuilder = new GenericSelectRecordBuilder()
					.connection(conn)
					.select(FieldFactory.getWorkflowRuleFields())
					.table("Workflow_Rule")
					.andCustomWhere("Workflow_Rule.ORGID = ? AND Workflow_Rule.NAME IN ('Create Alarm - EMail', 'Create Alarm - SMS')", OrgInfo.getCurrentOrgInfo().getOrgid());
			
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
			e.printStackTrace();
			throw e;
		}
		return false;
	}
}
