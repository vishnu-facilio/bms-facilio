package com.facilio.bmsconsole.actions;

import java.util.List;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class RecordSpecificRuleAction extends FacilioAction {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<WorkflowRuleContext> recordRules;
	public List<WorkflowRuleContext> getRecordRules() {
		return recordRules;
	}
	public void setRecordRules(List<WorkflowRuleContext> recordRules) {
		this.recordRules = recordRules;
	}
	
	private long recordId;
	public long getRecordId() {
		return recordId;
	}
	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private long parentId;
	
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	private WorkflowRuleContext recordRule;

	public WorkflowRuleContext getRecordRule() {
		return recordRule;
	}
	public void setRecordRule(WorkflowRuleContext recordRule) {
		this.recordRule = recordRule;
	}
	
	private List<ActionContext> actions;
	
	public List<ActionContext> getActions() {
		return actions;
	}
	public void setActions(List<ActionContext> actions) {
		this.actions = actions;
	}
	public String addOrUpdateRecordSpecificRule() throws Exception {
		FacilioContext context = new FacilioContext();
		
		recordRule.setRuleType(RuleType.RECORD_SPECIFIC_RULE);
		context.put(FacilioConstants.ContextNames.RECORD, recordRule);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, actions);
		
		FacilioChain chain = TransactionChainFactory.getAddOrUpdateRecordRuleChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_RULE, recordRule);
		return SUCCESS;
	}
	
	public String getRecordSpecificRuleList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);
		
		FacilioChain chain = ReadOnlyChainFactory.getRecordSpecificRuleList();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_RULE_LIST, context.get(FacilioConstants.ContextNames.RECORD_RULE_LIST));
		return SUCCESS;
	}
	
	public String viewRule() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, recordId);
		FacilioChain chain = ReadOnlyChainFactory.viewRecordRule();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_RULE, context.get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}
	
	public String deleteRecordRule() throws Exception {
		FacilioContext context = new FacilioContext();
		
		context.put(FacilioConstants.ContextNames.RECORD_ID, recordId);
		FacilioChain chain = TransactionChainFactory.getDeleteRecordRule();
		chain.execute(context);
		return SUCCESS;
	}
	
	
}
