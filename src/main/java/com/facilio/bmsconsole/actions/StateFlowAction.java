package com.facilio.bmsconsole.actions;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.StateContext;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
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
		Chain chain = TransactionChainFactory.getAddOrUpdateStateFlowTransition();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.TRANSITION, stateTransition);
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
		
		Chain chain = ReadOnlyChainFactory.getStateTransitionList();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.STATE_TRANSITION_LIST, context.get(FacilioConstants.ContextNames.STATE_TRANSITION_LIST));
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
		Chain chain = TransactionChainFactory.getDeleteStateFlowTransition();
		chain.execute(context);
		return SUCCESS;
	}
	
	private void updateStateContext(FacilioContext context) {
		stateTransition.setRuleType(RuleType.STATE_RULE);
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, stateTransition);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
	}
	
	public String updateStateFlow() {
		
		return SUCCESS;
	}
	
	
	private StateFlowRuleContext stateFlow;
	public StateFlowRuleContext getStateFlow() {
		return stateFlow;
	}
	public void setStateFlow(StateFlowRuleContext stateFlow) {
		this.stateFlow = stateFlow;
	}
	
//	private List<ActionContext> stateFlowActions;
//	public List<ActionContext> getStateFlowActions() {
//		return stateFlowActions;
//	}
//	public void setStateFlowActions(List<ActionContext> stateFlowActions) {
//		this.stateFlowActions = stateFlowActions;
//	}
	
	public String getStateFlowList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		Chain chain = ReadOnlyChainFactory.getStateFlowList();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.STATE_FLOW_LIST, context.get(FacilioConstants.ContextNames.STATE_FLOW_LIST));
		return SUCCESS;
	}
	
	public String viewStateFlow() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getStateFlowId());
		Chain chain = ReadOnlyChainFactory.viewStateFlow();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.STATE_FLOW, (StateFlowRuleContext) context.get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}
	
	public String viewStateTransition() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getStateTransitionId());
		Chain chain = ReadOnlyChainFactory.viewStateTransition();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.TRANSITION, context.get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}
	
	public String addOrUpdateStateFlow() throws Exception {
		FacilioContext context = new FacilioContext();
		
		context.put(FacilioConstants.ContextNames.RECORD, stateFlow);
		Chain chain = TransactionChainFactory.getAddOrUpdateStateFlow();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.STATE_FLOW, stateFlow);
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
		
		Chain c = TransactionChainFactory.getRearrangeStateFlows();
		c.execute(context);
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
		Chain chain = TransactionChainFactory.getUpdateStateTransitionChain();
		chain.execute(context);
		
		return SUCCESS;
	}
	
	public String getAvailableState() throws Exception {
		FacilioContext context = new FacilioContext();
		
		updateContext(context);
		Chain chain = TransactionChainFactory.getAvailableState();
		chain.execute(context);
		
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
		
		Chain chain = TransactionChainFactory.getAddOrUpdateStateChain();
		chain.execute(context);
		return SUCCESS;
	}
}
