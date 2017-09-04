package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.LookupOperator;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.view.ViewFactory;
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
		Object record = context.get(FacilioConstants.ContextNames.RECORD);
		if(record != null) {
			Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
			long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			long moduleId = modBean.getModule("workorder").getModuleId();
			
			int eventType = (int) context.get(FacilioConstants.Workflow.EVENT_TYPE);
			
	//		GenericSelectRecordBuilder eventBuilder = new GenericSelectRecordBuilder()
	//				.connection(conn)
	//				.table("Event")
	//				.select(FieldFactory.getEventFields())
	//				.where("ORGID = ? AND MODULEID = ? AND EVENT_TYPE = ?", OrgInfo.getCurrentOrgInfo().getOrgid(), moduleId, eventType);
	//		List<Map<String, Object>> events = eventBuilder.get();
			
			GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
					.connection(conn)
					.table("Workflow_Rule")
					.select(FieldFactory.getWorkflowRuleFields())
					.innerJoin("Event")
					.on("Workflow_Rule.EVENT_ID = Event.EVENT_ID")
					.andCustomWhere("Workflow_Rule.ORGID = ? AND Event.MODULEID = ? AND Event.EVENT_TYPE = ?", OrgInfo.getCurrentOrgInfo().getOrgid(), moduleId, eventType)
					.orderBy("EXECUTION_ORDER");
			List<Map<String, Object>> workflowRules = ruleBuilder.get();
			
			for(Map<String, Object> workflowRule : workflowRules)
			{
				long criteriaId = (long) workflowRule.get("criteriaId");
				Criteria criteria = CriteriaAPI.getCriteria(orgId, criteriaId, conn);
				boolean flag = criteria.computePredicate().evaluate(record);
				if(flag)
				{
					long workflowRuleId = (long) workflowRule.get("workflowRuleId");
//					GenericSelectRecordBuilder ruleActionBuilder = new GenericSelectRecordBuilder()
//							.connection(conn)
//							.table("Workflow_Rule_Action")
//							.select(FieldFactory.getWorkflowRuleActionFields())
//							.andCustomWhere("WORKFLOW_RULE_ID = ?", workflowRuleId);
//					List<Map<String, Object>> workflowRuleActions = ruleActionBuilder.get();
					GenericSelectRecordBuilder actionBuilder = new GenericSelectRecordBuilder()
							.connection(conn)
							.select(FieldFactory.getActionFields())
							.table("Action")
							.innerJoin("Workflow_Rule_Action")
							.on("Action.ACTION_ID = Workflow_Rule_Action.ACTION_ID")
							.andCustomWhere("Workflow_Rule_Action.WORKFLOW_RULE_ID = ?", workflowRuleId);
					List<Map<String, Object>> actions = actionBuilder.get();
					for(Map<String, Object> action : actions)
					{
//						long actionId = (long) workflowRuleAction.get("actionId");
//						GenericSelectRecordBuilder actionBuilder = new GenericSelectRecordBuilder()
//								.connection(conn)
//								.table("Action")
//								.select(FieldFactory.getActionFields())
//								.andCustomWhere("ACTION_ID = ?", actionId);
//						List<Map<String, Object>> actions = actionBuilder.get();
						
						int actionType = Integer.parseInt(String.valueOf(action.get("actionType")));
						switch(actionType)
						{
							case FacilioConstants.Workflow.ACTION_EMAIL_NOTIFICATION:
							{
								long templateId = (long) action.get("templateId");
								if(templateId == 0)
								{
									int templateType = Integer.parseInt(String.valueOf(action.get("templateType")));
									switch(templateType)
									{
										case FacilioConstants.Workflow.TEMPLATE_WORKORDER_ASSIGN:
										{
											JSONObject mailJson = new JSONObject();
											mailJson.put("sender", "support@"+OrgInfo.getCurrentOrgInfo().getOrgDomain()+".facilio.com");
											mailJson.put("to", "manthosh@facilio.com");
											mailJson.put("subject", "Workorder Assigned");
											mailJson.put("message", "A new work order has been assigned to you. Please follow the link below to view the work order.");
											try {
												AwsUtil.sendEmail(mailJson);
											}
											catch(Exception e) {
												e.printStackTrace();
											}
											break;
										}
										case FacilioConstants.Workflow.TEMPLATE_WORKORDER_ACTIVITY_FOLLOWUP:
										{
											JSONObject mailJson = new JSONObject();
											mailJson.put("sender", "support@thingscient.com");
											mailJson.put("to", "shivaraj@thingscient.com");
											mailJson.put("subject", "Workorder Assigned");
											mailJson.put("message", "A new work order has been assigned to you. Please follow the link below to view the work order.");
											try {
												AwsUtil.sendEmail(mailJson);
											}
											catch(Exception e) {
												e.printStackTrace();
											}
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
		}
		return false;
	}
	
	private Criteria getCriteria() {
		Criteria ticketCriteria = new Criteria();
		ticketCriteria.addAndCondition(ViewFactory.getMyUserCondition("Tickets"));
		
		FacilioModule ticketModule = new FacilioModule();
		ticketModule.setName("ticket");
		ticketModule.setTableName("Tickets");
		ticketModule.setDisplayName("Tickets");
		
		LookupField ticketField = new LookupField();
		ticketField.setName("ticket");
		ticketField.setColumnName("TICKET_ID");
		ticketField.setDataType(FieldType.LOOKUP);
		ticketField.setModuleTableName("Workorders");
		ticketField.setLookupModule(ticketModule);
		
		Condition ticketCondition = new Condition();
		ticketCondition.setField(ticketField);
		ticketCondition.setOperator(LookupOperator.LOOKUP);
		ticketCondition.setCriteriaValue(ticketCriteria);
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(ticketCondition);
		
		return criteria;
	}
}
