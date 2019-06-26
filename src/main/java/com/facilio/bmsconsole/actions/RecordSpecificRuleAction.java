package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.RecordSpecificRuleContext;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class RecordSpecificRuleAction extends FacilioAction {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<RecordSpecificRuleContext> recordRules;
	public List<RecordSpecificRuleContext> getRecordRules() {
		return recordRules;
	}
	public void setRecordRules(List<RecordSpecificRuleContext> recordRules) {
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

	private RecordSpecificRuleContext recordRule;

	public RecordSpecificRuleContext getRecordRule() {
		return recordRule;
	}
	public void setRecordRule(RecordSpecificRuleContext recordRule) {
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
		
		context.put(FacilioConstants.ContextNames.RECORD, recordRule);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, actions);
		
		Chain chain = TransactionChainFactory.getAddOrUpdateRecordRuleChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_RULE, recordRule);
		return SUCCESS;
	}
	
	public String getRecordSpecificRuleList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);
		
		Chain chain = ReadOnlyChainFactory.getRecordSpecificRuleList();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_RULE_LIST, context.get(FacilioConstants.ContextNames.RECORD_RULE_LIST));
		return SUCCESS;
	}
	
	public String viewRule() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, recordId);
		Chain chain = ReadOnlyChainFactory.viewRecordRule();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_RULE, (RecordSpecificRuleContext) context.get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}
	
	public String deleteRecordRule() throws Exception {
		FacilioContext context = new FacilioContext();
		
		context.put(FacilioConstants.ContextNames.RECORD_ID, recordId);
		Chain chain = TransactionChainFactory.getDeleteRecordRule();
		chain.execute(context);
		return SUCCESS;
	}
	
	
}
