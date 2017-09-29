package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.util.WorkflowAPI;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionSupport;

public class NotificationAction extends ActionSupport {
	public String addAlarmEMailNotifier() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.Workflow.NOTIFICATION_EMAIL, email);
		
		Chain addAlarmEmailChain = FacilioChainFactory.getAddAlarmEMailNotifierChain();
		addAlarmEmailChain.execute(context);
		
		Boolean result = (Boolean) context.get(FacilioConstants.ContextNames.RESULT);
		if(result != null && result) {
			setResult("success");
		}
		
		return SUCCESS;
	}
	
	private String result;
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}

	private String email;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String addAlarmSMSNotifier() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.Workflow.NOTIFICATION_SMS, phone);
		
		Chain addAlarmSMSChain = FacilioChainFactory.getAddAlarmSMSNotifierChain();
		addAlarmSMSChain.execute(context);
		
		Boolean result = (Boolean) context.get(FacilioConstants.ContextNames.RESULT);
		if(result != null && result) {
			setResult("success");
		}
		
		return SUCCESS;
	}
	
	private String phone;
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String alarmCreationRulesList() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		Chain fetchAlarmCreationRules = FacilioChainFactory.getAlarmCreationRulesChain();
		fetchAlarmCreationRules.execute(context);
		
		alarmCreationRules = (List<WorkflowRuleContext>) context.get(FacilioConstants.Workflow.WORKFLOW_LIST);
		
		return SUCCESS;
	}
	
	private List<WorkflowRuleContext> alarmCreationRules;
	public List<WorkflowRuleContext> getAlarmCreationRules() {
		return alarmCreationRules;
	}
	public void setAlarmCreationRules(List<WorkflowRuleContext> alarmCreationRules) {
		this.alarmCreationRules = alarmCreationRules;
	}
	
	public String turnOffWorkflow() throws Exception {
		if(workflowId != -1) {
			WorkflowRuleContext workFlow = new WorkflowRuleContext();
			workFlow.setStatus(false);
			WorkflowAPI.updateWorkflowRule(OrgInfo.getCurrentOrgInfo().getOrgid(), workFlow, workflowId);
			setResult("success");
		}
		return SUCCESS;
	}
	
	public String turnOnWorkflow() throws Exception {
		if(workflowId != -1) {
			WorkflowRuleContext workFlow = new WorkflowRuleContext();
			workFlow.setStatus(true);
			WorkflowAPI.updateWorkflowRule(OrgInfo.getCurrentOrgInfo().getOrgid(), workFlow, workflowId);
			setResult("success");
		}
		return SUCCESS;
	}
	
	private long workflowId = -1;
	public long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}
}
