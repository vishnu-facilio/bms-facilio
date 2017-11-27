package com.facilio.bmsconsole.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.GroupContext;
import com.facilio.bmsconsole.util.GroupAPI;
import com.facilio.bmsconsole.util.UserAPI;
import com.facilio.bmsconsole.util.WorkflowAPI;
import com.facilio.bmsconsole.workflow.ActionContext;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.UserType;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionSupport;

public class WorkflowRuleAction extends ActionSupport {
	
	public String execute() throws Exception 
	{
	    return SUCCESS;
	}
	
	public String assignmentRules() throws Exception 
	{
		setRules(WorkflowAPI.getWorkflowRules(OrgInfo.getCurrentOrgInfo().getOrgid()));
	    return "assignmentRules";
	}
	
	public String newAssignmentRule() throws Exception 
	{
		List<GroupContext> groups = GroupAPI.getGroupsOfOrg(OrgInfo.getCurrentOrgInfo().getOrgid(), true);
		assignmentGroupList = new HashMap<>();
		if (groups != null && groups.size() > 0) {
			for(GroupContext group : groups) {
				assignmentGroupList.put(group.getGroupId(), group.getName());
			}
		}
		
		assignedToList = UserAPI.getOrgUsers(OrgInfo.getCurrentOrgInfo().getOrgid(), UserType.USER.getValue());
		return "newAssignmentRule";
	}
	
	public String addAssignmentRule() throws Exception {
		
		WorkflowRuleContext rule = new WorkflowRuleContext();
		rule.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
		rule.setName(getName());
		rule.setDescription(getDescription());
		rule.setExecutionOrder(getExecutionOrder());
		
		long ruleId = WorkflowAPI.addWorkflowRule(rule);
		
		return "addAssignmentRuleSuccess";
	}
	
	public String addWorkflowRule() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION, actions);
		
		Chain addRule = FacilioChainFactory.getAddWorkflowRuleChain();
		addRule.execute(context);
		
		return SUCCESS;
	}
	
	public String businessRules() throws Exception {
		
	    return SUCCESS;
	}
	
	private ActionForm form = new ActionForm();
	public ActionForm getForm() {
		return form;
	}
	
	public void setForm(ActionForm form) {
		this.form = form;
	}
	
	WorkflowRuleContext rule;
	public WorkflowRuleContext getRule() {
		return rule;
	}
	
	public void setRule(WorkflowRuleContext rule) {
		this.rule = rule;
	}
	
	private List<ActionContext> actions;
	public List<ActionContext> getActions() {
		return this.actions;
	}
	
	public void setActions(List<ActionContext> actions) {
		this.actions = actions;
	}
	
	private List<WorkflowRuleContext> rules = null;
	public List<WorkflowRuleContext> getRules() {
		return rules;
	}
	public void setRules(List<WorkflowRuleContext> rules) {
		this.rules = rules;
	}
	
	private Map<Long, String> assignmentGroupList;
	public  Map<Long, String> getAssignmentGroupList() {
		return assignmentGroupList;
	}
	public void setAssignmentGroupList( Map<Long, String> assignmentGroupList) {
		this.assignmentGroupList = assignmentGroupList;
	}
	
	private Map<Long, String> assignedToList;
	public Map<Long, String> getAssignedToList() {
		return assignedToList;
	}
	public void setAssignedToList(Map<Long, String> assignedToList) {
		this.assignedToList = assignedToList;
	}
	
	private long orgId = 0;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long workflowId;
	public long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private String module;
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	
	private boolean isActive = true;
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	private int executionOrder = 1;
	public int getExecutionOrder() {
		return executionOrder;
	}
	public void setExecutionOrder(int executionOrder) {
		this.executionOrder = executionOrder;
	}
}
