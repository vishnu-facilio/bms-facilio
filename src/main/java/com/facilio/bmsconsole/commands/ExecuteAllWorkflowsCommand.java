package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericSelectRecordBuilder;

public class ExecuteAllWorkflowsCommand implements Command 
{
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", conn);
		long moduleId = modBean.getModule("workorder").getModuleId();
		
		int eventType = (int) context.get(FacilioConstants.Workflow.EVENT_TYPE);
		GenericSelectRecordBuilder eventBuilder = new GenericSelectRecordBuilder()
				.connection(conn)
				.table("Event")
				.select(FieldFactory.getEventFields())
				.where("ORGID = ? AND MODULEID = ? AND EVENT_TYPE = ?", OrgInfo.getCurrentOrgInfo().getOrgid(), moduleId, eventType);
		List<Map<String, Object>> events = eventBuilder.get();
		
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.connection(conn)
				.table("Workflow_Rule")
				.select(FieldFactory.getWorkflowRuleFields())
				.where("ORGID = ? AND EVENT_ID = ?", OrgInfo.getCurrentOrgInfo().getOrgid(), events.get(0).get("eventId"))
				.orderBy("EXECUTION_ORDER");
		List<Map<String, Object>> workflowRules = ruleBuilder.get();
		
		for(Map<String, Object> workflowRule : workflowRules)
		{
			long criteriaId = (long) workflowRule.get("criteriaId");
			boolean flag = true;
			if(flag)
			{
				long workflowRuleId = (long) workflowRule.get("workflowRuleId");
				GenericSelectRecordBuilder ruleActionBuilder = new GenericSelectRecordBuilder()
						.connection(conn)
						.table("Workflow_Rule_Action")
						.select(FieldFactory.getWorkflowRuleActionFields())
						.where("WORKFLOW_RULE_ID = ?", workflowRuleId);
				List<Map<String, Object>> workflowRuleActions = ruleActionBuilder.get();
				
				for(Map<String, Object> workflowRuleAction : workflowRuleActions)
				{
					long actionId = (long) workflowRuleAction.get("actionId");
					GenericSelectRecordBuilder actionBuilder = new GenericSelectRecordBuilder()
							.connection(conn)
							.table("Action")
							.select(FieldFactory.getActionFields())
							.where("ACTION_ID = ?", actionId);
					List<Map<String, Object>> actions = actionBuilder.get();
					
					int actionType = Integer.parseInt(String.valueOf(actions.get(0).get("actionType")));
					switch(actionType)
					{
						case FacilioConstants.Workflow.ACTION_EMAIL_NOTIFICATION:
						{
							long templateId = (long) actions.get(0).get("templateId");
							if(templateId == 0)
							{
								int templateType = Integer.parseInt(String.valueOf(actions.get(0).get("templateType")));
								switch(templateType)
								{
									case FacilioConstants.Workflow.TEMPLATE_WORKORDER_ASSIGN:
									{
										JSONObject mailJson = new JSONObject();
										mailJson.put("sender", "support@thingscient.com");
										mailJson.put("to", "shivaraj@thingscient.com");
										mailJson.put("subject", "Workorder Assigned");
										mailJson.put("message", "A new work order has been assigned to you. Please follow the link below to view the work order.");
										AwsUtil.sendEmail(mailJson);
										break;
									}
									case FacilioConstants.Workflow.TEMPLATE_WORKORDER_ACTIVITY_FOLLOWUP:
									{
										JSONObject mailJson = new JSONObject();
										mailJson.put("sender", "support@thingscient.com");
										mailJson.put("to", "shivaraj@thingscient.com");
										mailJson.put("subject", "Workorder Assigned");
										mailJson.put("message", "A new work order has been assigned to you. Please follow the link below to view the work order.");
										AwsUtil.sendEmail(mailJson);
										break;
									}
									default:
										break;
								}
								break;
							}
						}
						default:
							break;
					}
				}
			}
		}
		return false;
	}

}
