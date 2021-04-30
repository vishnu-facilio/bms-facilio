package com.facilio.bmsconsole.actions;

import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.wmsv2.handler.AuditLogHandler;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.StateContext;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class StateFlowAction extends FacilioAction {
	private static final long serialVersionUID = 1L;

	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long transitionId = -1;
	public long getTransitionId() {
		return transitionId;
	}
	public void setTransitionId(long transitionId) {
		this.transitionId = transitionId;
	}
	
	private StateflowTransitionContext stateTransition;
	public StateflowTransitionContext getStateTransition() {
		return stateTransition;
	}
	public void setStateTransition(StateflowTransitionContext stateTransition) {
		this.stateTransition = stateTransition;
	}
	
	public String addOrUpdateStateTransition() throws Exception {
		FacilioContext context = new FacilioContext();
		
		updateStateContext(context);
		FacilioChain chain = TransactionChainFactory.getAddOrUpdateStateFlowTransition();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.TRANSITION, stateTransition);
		sendAuditLogs(new AuditLogHandler.AuditLog("Add Or Update State Transition",
				"State transition has been updated", AuditLogHandler.RecordType.SETTING,
				"StateTransition", stateTransition.getId()));
		return SUCCESS;
	}
	
	private long stateFlowId = -1;
	public long getStateFlowId() {
		return stateFlowId;
	}
	public void setStateFlowId(long stateFlowId) {
		this.stateFlowId = stateFlowId;
	}
	
	public String getStateTransitionList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.STATE_FLOW_ID, stateFlowId);
		
		FacilioChain chain = ReadOnlyChainFactory.getStateTransitionList();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.STATE_TRANSITION_LIST, context.get(FacilioConstants.ContextNames.STATE_TRANSITION_LIST));
		return SUCCESS;
	}

	public String createDraft() throws Exception {
		FacilioChain chain = TransactionChainFactory.getCreateStateFlowDraftChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.ID, stateFlowId);
		chain.execute();

		setResult(FacilioConstants.ContextNames.STATE_FLOW, context.get(FacilioConstants.ContextNames.STATE_FLOW));
		setResult(FacilioConstants.ContextNames.STATE_TRANSITION_LIST, context.get(FacilioConstants.ContextNames.STATE_TRANSITION_LIST));

		return SUCCESS;
	}

	public String cloneStateFlow() throws Exception {
		FacilioChain chain = TransactionChainFactory.getCloneStateFlowChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.CRITERIA, criteria);
		context.put(FacilioConstants.ContextNames.ID, stateFlowId);
		chain.execute();

		setResult(FacilioConstants.ContextNames.STATE_FLOW, context.get(FacilioConstants.ContextNames.STATE_FLOW));

		return SUCCESS;
	}

	private Criteria criteria;
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	public String publishStateFlow() throws Exception {
		FacilioChain chain = TransactionChainFactory.getPublishStateFlowChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.ID, stateFlowId);
		chain.execute();
		return SUCCESS;
	}
	
	private long stateTransitionId = -1;
	public long getStateTransitionId() {
		return stateTransitionId;
	}
	public void setStateTransitionId(long stateTransitionId) {
		this.stateTransitionId = stateTransitionId;
	}

	public String deleteStateTransition() throws Exception {
		FacilioContext context = new FacilioContext();
		
		context.put(FacilioConstants.ContextNames.STATE_FLOW_ID, stateFlowId);
		context.put(FacilioConstants.ContextNames.TRANSITION_ID, stateTransitionId);
		FacilioChain chain = TransactionChainFactory.getDeleteStateFlowTransition();
		chain.execute(context);
		return SUCCESS;
	}
	
	private void updateStateContext(FacilioContext context) {
		stateTransition.setRuleType(RuleType.STATE_RULE);
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, stateTransition);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
	}
	
	private StateFlowRuleContext stateFlow;
	public StateFlowRuleContext getStateFlow() {
		return stateFlow;
	}
	public void setStateFlow(StateFlowRuleContext stateFlow) {
		this.stateFlow = stateFlow;
	}

	public String getStateFlowList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getStateFlowList();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		chain.execute();
		
		setResult(FacilioConstants.ContextNames.STATE_FLOW_LIST, context.get(FacilioConstants.ContextNames.STATE_FLOW_LIST));
		return SUCCESS;
	}
	
	public String viewStateFlow() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getStateFlowId());
		FacilioChain chain = ReadOnlyChainFactory.viewStateFlow();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.STATE_FLOW, context.get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}
	
	public String viewStateTransition() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getStateTransitionId());
		FacilioChain chain = ReadOnlyChainFactory.viewStateTransition();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.TRANSITION, context.get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}
	
	public String addOrUpdateStateFlow() throws Exception {
		FacilioContext context = new FacilioContext();
		
		context.put(FacilioConstants.ContextNames.RECORD, stateFlow);
		FacilioChain chain = TransactionChainFactory.getAddOrUpdateStateFlow();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.STATE_FLOW, stateFlow);
		return SUCCESS;
	}
	
	private JSONObject stateFlowDiagram;
	public JSONObject getStateFlowDiagram() {
		return stateFlowDiagram;
	}
	public void setStateFlowDiagram(JSONObject stateFlowDiagram) {
		this.stateFlowDiagram = stateFlowDiagram;
	}
	
	public String updateStateflowDiagram() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, getId());
		context.put(FacilioConstants.ContextNames.STATEFLOW_DIAGRAM, getStateFlowDiagram());
		
		FacilioChain c = TransactionChainFactory.getUpdateStateFlowDiagramChain();
		c.execute(context);
		
		return SUCCESS;
	}
	
	private List<StateFlowRuleContext> stateFlows;
	public List<StateFlowRuleContext> getStateFlows() {
		return stateFlows;
	}
	public void setStateFlows(List<StateFlowRuleContext> stateFlows) {
		this.stateFlows = stateFlows;
	}
	
	public String rearrangeStateFlows() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.STATE_FLOW_LIST, getStateFlows());
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		
		FacilioChain c = TransactionChainFactory.getRearrangeStateFlows();
		c.execute(context);
		return SUCCESS;
	}

	private Boolean status;
	public Boolean getStatus() {
		if (status == null) {
			return false;
		}
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}

	public String changeStatus() throws Exception {
		WorkflowRuleContext workFlow = new WorkflowRuleContext();
		workFlow.setStatus(status);
		workFlow.setId(getId());
		WorkflowRuleAPI.updateWorkflowRule(workFlow);

		FacilioChain chain = TransactionChainFactory.getChangeStatusForStateflowChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workFlow);
		context.put(FacilioConstants.ContextNames.STATE_FLOW_LIST, getStateFlows());
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);

		chain.execute();

		return SUCCESS;
	}

	private Map<String, Object> data;
	public Map<String, Object> getData() {
		return data;
	}
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	
	public String updateStateTransition() throws Exception {
		FacilioContext context = new FacilioContext();
		
		updateContext(context);
		FacilioChain chain = TransactionChainFactory.getUpdateStateTransitionChain();
		chain.execute(context);
		
		return SUCCESS;
	}
	
	public String getAvailableState() throws Exception {

		FacilioChain chain = TransactionChainFactory.getAvailableState();
		FacilioContext context = chain.getContext();
		updateContext(context);
		context.put(FacilioConstants.ContextNames.RULE_TYPE, RuleType.STATE_FLOW);
		chain.execute();
		
		setResult(FacilioConstants.ContextNames.AVAILABLE_STATES, context.get("availableStates"));
		setResult(FacilioConstants.ContextNames.CURRENT_STATE, context.get("currentState"));
		
		return SUCCESS;
	}

	private void updateContext(FacilioContext context) {
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.ID, id);
		context.put(FacilioConstants.ContextNames.TRANSITION_ID, transitionId);
		context.put(FacilioConstants.ContextNames.DATA_KEY, data);
	}
	
	private StateContext state;
	public StateContext getState() {
		return state;
	}
	public void setState(StateContext state) {
		this.state = state;
	}
	
	public String addOrUpdateState() throws Exception {
		FacilioContext context = new FacilioContext();
		
		FacilioChain chain = TransactionChainFactory.getAddOrUpdateStateChain();
		chain.execute(context);
		return SUCCESS;
	}

	public String getTransitionDetailsFromPermalink() throws Exception {
		String token = ServletActionContext.getRequest().getHeader("X-Permalink-Token");

		FacilioChain chain = ReadOnlyChainFactory.getStateTransitionDetailsFromPermalink();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.TOKEN, token);

        chain.execute();

        setResult(FacilioConstants.ContextNames.RECORD, context.get(FacilioConstants.ContextNames.RECORD));
        setResult(FacilioConstants.ContextNames.SESSION, context.get(FacilioConstants.ContextNames.SESSION));
        return SUCCESS;
	}

	public String updateStateTransitionStates() throws Exception {
		FacilioChain chain = TransactionChainFactory.getUpdateStateTransitionStateChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.STATE_FLOW_ID, stateFlowId);
		context.put(FacilioConstants.ContextNames.TRANSITION, stateTransition);
		chain.execute();
		return SUCCESS;
	}

	public String getConfirmationDialogs() throws Exception {
		FacilioChain chain = TransactionChainFactory.getConfirmationDialogChain();
		FacilioContext context = chain.getContext();

		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.ID, id);
		context.put(FacilioConstants.ContextNames.RULE_ID, transitionId);
		context.put(FacilioConstants.ContextNames.DATA, data);

		chain.execute();
		setResult(FacilioConstants.ContextNames.VALID_CONFIRMATION_DIALOGS, context.get(FacilioConstants.ContextNames.VALID_CONFIRMATION_DIALOGS));

		return SUCCESS;
	}
}
