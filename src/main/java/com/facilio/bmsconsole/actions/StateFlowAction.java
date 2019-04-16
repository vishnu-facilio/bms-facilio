package com.facilio.bmsconsole.actions;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.StateFlowContext;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.StateContext;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransistionContext;
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
	
	private StateflowTransistionContext stateTransistion;
	public StateflowTransistionContext getStateTransistion() {
		return stateTransistion;
	}
	public void setStateTransistion(StateflowTransistionContext stateTransistion) {
		this.stateTransistion = stateTransistion;
	}
	
	public String addStateTransistion() throws Exception {
		FacilioContext context = new FacilioContext();
		
		updateStateContext(context);
		Chain chain = TransactionChainFactory.getAddStateFlowTransistion();
		chain.execute(context);
		return SUCCESS;
	}
	
	private void updateStateContext(FacilioContext context) {
		stateTransistion.setRuleType(RuleType.STATE_RULE);
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, stateTransistion);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
	}
	
	public String updateStateFlow() {
		
		return SUCCESS;
	}
	
	
	private StateFlowContext stateFlow;
	public StateFlowContext getStateFlow() {
		return stateFlow;
	}
	public void setStateFlow(StateFlowContext stateFlow) {
		this.stateFlow = stateFlow;
	}
	
	private List<ActionContext> stateFlowActions;
	public List<ActionContext> getStateFlowActions() {
		return stateFlowActions;
	}
	public void setStateFlowActions(List<ActionContext> stateFlowActions) {
		this.stateFlowActions = stateFlowActions;
	}
	
	public String addOrUpdateStateFlow() throws Exception {
		FacilioContext context = new FacilioContext();
		
		context.put(FacilioConstants.ContextNames.RECORD, stateFlow);
		context.put(FacilioConstants.ContextNames.ACTIONS_LIST, stateFlowActions);
		Chain chain = TransactionChainFactory.getAddOrUpdateStateFlow();
		chain.execute(context);
		return SUCCESS;
	}
	
	private Map<String, Object> data;
	public Map<String, Object> getData() {
		return data;
	}
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	
	public String updateStateTransistion() throws Exception {
		FacilioContext context = new FacilioContext();
		
		updateContext(context);
		Chain chain = TransactionChainFactory.getUpdateStateTransistionChain();
		chain.execute(context);
		
		return SUCCESS;
	}
	
	public String getAvailableState() throws Exception {
		FacilioContext context = new FacilioContext();
		
		updateContext(context);
		Chain chain = TransactionChainFactory.getAvailableState();
		chain.execute(context);
		
		setResult("states", context.get("availableStates"));
		setResult("currentState", context.get("currentState"));
		
		return SUCCESS;
	}

	private void updateContext(FacilioContext context) {
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.ID, id);
		context.put("transistion_id", transitionId);
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
//		context.put(FacilioConstants.ContextNames.STATE, state);
		
		Chain chain = TransactionChainFactory.getAddOrUpdateStateChain();
		chain.execute(context);
		return SUCCESS;
	}
}
