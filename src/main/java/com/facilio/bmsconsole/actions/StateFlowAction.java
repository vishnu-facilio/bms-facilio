package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.StateFlowContext;
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
	
	public String addOrUpdateStateFlow() throws Exception {
		FacilioContext context = new FacilioContext();
		
		context.put(FacilioConstants.ContextNames.RECORD, stateFlow);
		Chain chain = TransactionChainFactory.getAddOrUpdateStateFlow();
		chain.execute(context);
		return SUCCESS;
	}
	
	public String updateState() throws Exception {
		FacilioContext context = new FacilioContext();
		
		updateContext(context);
		Chain chain = TransactionChainFactory.getUpdateStateChain();
		chain.execute(context);
		
		return SUCCESS;
	}
	
	public String getAvailableState() throws Exception {
		FacilioContext context = new FacilioContext();
		
		updateContext(context);
		Chain chain = TransactionChainFactory.getAvailableState();
		chain.execute(context);
		
		setResult("states", context.get("availableStates"));
		
		return SUCCESS;
	}

	private void updateContext(FacilioContext context) {
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.ID, id);
		context.put("transistion_id", transitionId);
	}
}
