package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ApprovalRuleMetaContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

import java.util.List;

public class ApprovalAction extends FacilioAction {

	private static final long serialVersionUID = 1L;

	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	private List<Long> ids;
	public List<Long> getIds() {
		return ids;
	}
	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	private Long transitionId;
	public Long getTransitionId() {
		return transitionId;
	}
	public void setTransitionId(Long transitionId) {
		this.transitionId = transitionId;
	}

	public String changeStatus() throws Exception {
		FacilioChain chain = TransactionChainFactory.getNextApprovalStateChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
		context.put(FacilioConstants.ContextNames.TRANSITION_ID, transitionId);
		chain.execute();
		return SUCCESS;
	}

	private ApprovalRuleMetaContext approvalRule;
	public ApprovalRuleMetaContext getApprovalRule() {
		return approvalRule;
	}
	public void setApprovalRule(ApprovalRuleMetaContext approvalRule) {
		this.approvalRule = approvalRule;
	}

	public String addOrUpdateApprovalRule() throws Exception {
		FacilioChain chain = TransactionChainFactory.getAddOrUpdateApprovalRuleChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.APPROVAL_RULE, approvalRule);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		chain.execute();

		setResult(FacilioConstants.ContextNames.APPROVAL_RULE, context.get(FacilioConstants.ContextNames.APPROVAL_RULE));
		return SUCCESS;
	}

	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public String getApprovalRuleDetails() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getApprovalRuleDetailsChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.ID, id);
		chain.execute();

		setResult(FacilioConstants.ContextNames.APPROVAL_RULE, context.get(FacilioConstants.ContextNames.APPROVAL_RULE));
		return SUCCESS;
	}

	public String reorderApprovalRule() throws Exception {
		FacilioChain chain = TransactionChainFactory.getReorderWorkflowRuleChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_TYPE, WorkflowRuleContext.RuleType.APPROVAL_STATE_FLOW);
		chain.execute();

		return SUCCESS;
	}
}
