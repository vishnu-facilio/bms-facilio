package com.facilio.bmsconsole.actions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ApprovalRuleMetaContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.ims.handler.AuditLogHandler;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.function.Function;

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
		context.put(FacilioConstants.ContextNames.APPROVAL_TRANSITION_ID, transitionId);
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
		boolean add  = approvalRule.getId() <= 0;
		chain.execute();

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);

		setResult(FacilioConstants.ContextNames.APPROVAL_RULE, context.get(FacilioConstants.ContextNames.APPROVAL_RULE));
		sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("Approval Process {%s} for %s has been %s.", approvalRule.getName(),module.getDisplayName(), (add ? "added" : "updated")),
				approvalRule.description,
				AuditLogHandler.RecordType.SETTING,
				"Approval Process", approvalRule.getId())
				.setActionType(add ? AuditLogHandler.ActionType.ADD : AuditLogHandler.ActionType.UPDATE)
				.setLinkConfig(((Function<Void, String>) o -> {
					JSONArray array = new JSONArray();
					JSONObject json = new JSONObject();
					json.put("id", approvalRule.getId());
					json.put("moduleName", moduleName);
					json.put("navigateTo", "ApprovalProcess");

					array.add(json);
					return array.toJSONString();
				}).apply(null))
		);

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
		context.put(FacilioConstants.ContextNames.RULE_TYPE, WorkflowRuleContext.RuleType.APPROVAL_STATE_FLOW.getIntVal());
		chain.execute();

		return SUCCESS;
	}

	public String getAvailableState() throws Exception {

		FacilioChain chain = TransactionChainFactory.getAvailableApprovalState();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.ID, id);
		context.put(FacilioConstants.ContextNames.RULE_TYPE, WorkflowRuleContext.RuleType.APPROVAL_STATE_FLOW);
		chain.execute();

		setResult(FacilioConstants.ContextNames.AVAILABLE_STATES, context.get("availableStates"));
		setResult(FacilioConstants.ContextNames.CURRENT_STATE, context.get("currentState"));
		setResult(FacilioConstants.ContextNames.PENDING_APPROVAL_LIST, context.get(FacilioConstants.ContextNames.PENDING_APPROVAL_LIST));
		setResult(FacilioConstants.ContextNames.APPROVAL_RULE, context.get(FacilioConstants.ContextNames.APPROVAL_RULE));

		return SUCCESS;
	}

	public String getApprovalDetails() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getApprovalDetails();
		Context context = chain.getContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, id);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		chain.execute();

		setResult(FacilioConstants.ContextNames.APPROVAL_RULE, context.get(FacilioConstants.ContextNames.APPROVAL_RULE));
		setResult(FacilioConstants.ContextNames.APPROVAL_LIST, context.get(FacilioConstants.ContextNames.APPROVAL_LIST));
		return SUCCESS;
	}

	public String getApprovalModuleDataList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getApprovalModuleDataListChain();
		Context context = chain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.VIEW_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.SKIP_MODULE_CRITERIA, true);
		constructListContext((FacilioContext) context);
		chain.execute();

		if (isFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,
					chain.getContext().get(FacilioConstants.ContextNames.RECORD_COUNT));
		} else {
			setResult(FacilioConstants.ContextNames.RECORD_LIST, context.get(FacilioConstants.ContextNames.RECORD_LIST));
			setResult("stateFlows", context.get("stateFlows"));
		}
		return SUCCESS;
	}

	public String getApprovalModuleActivityList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getApprovalModuleActivityListChain();
		Context context = chain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		constructListContext((FacilioContext) context);
		chain.execute();

		if (isFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,
					chain.getContext().get(FacilioConstants.ContextNames.RECORD_COUNT));
		} else {
			setResult(FacilioConstants.ContextNames.ACTIVITY_LIST, context.get(FacilioConstants.ContextNames.ACTIVITY_LIST));
			setResult(FacilioConstants.ContextNames.PICKLIST, context.get(FacilioConstants.ContextNames.PICKLIST));
			setResult(FacilioConstants.ContextNames.WORKFLOW_RULE, context.get(FacilioConstants.ContextNames.WORKFLOW_RULE));
		}

		return SUCCESS;
	}

	public String getApprovalModules() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getApprovalModules();
		Context context = chain.getContext();
		chain.execute();

		setResult("modules", chain.getContext().get("modules"));

		return SUCCESS;
	}
}
