package com.facilio.bmsconsole.actions;

import com.amazonaws.regions.Regions;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.interceptors.AuthInterceptor;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.StateContext;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.wmsv2.handler.AuditLogHandler;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Log4j
public class StateFlowAction extends FacilioAction {
	private static final long serialVersionUID = 1L;

	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	private String parentModuleName;
	public void setParentModuleName(String parentModuleName) {
		this.parentModuleName = parentModuleName;
	}
	public String getParentModuleName() {
		return parentModuleName;
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

		boolean add  = stateTransition.getId() <= 0;
		
		updateStateContext(context);
		FacilioChain chain = TransactionChainFactory.getAddOrUpdateStateFlowTransition();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.TRANSITION, stateTransition);
		sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("State transition {%s} has been %s.", stateTransition.getName(), (add ? "added" : "updated")),
				stateTransition.getAuditLogDescription(add),
				AuditLogHandler.RecordType.SETTING,
				"StateTransition", stateTransition.getId())
				.setActionType(add ? AuditLogHandler.ActionType.ADD : AuditLogHandler.ActionType.UPDATE)
				.setLinkConfig(((Function<Void, String>) o -> {
					JSONArray array = new JSONArray();
					JSONObject json = new JSONObject();
					json.put("id", stateTransition.getId());
					json.put("moduleName", moduleName);
					json.put("stateFlowId", stateTransition.getStateFlowId());
					json.put("navigateTo", "StateTransition");

					array.add(json);
					return array.toJSONString();
				}).apply(null))
		);

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

		List<StateflowTransitionContext> stateTransitions = (List<StateflowTransitionContext>) context.get(FacilioConstants.ContextNames.STATE_TRANSITION_LIST);
		List<Map<String, Object>> transitionPros = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(stateTransitions)) {
			for (StateflowTransitionContext transitionContext : stateTransitions) {
				Map<String, Object> map = new HashMap<>();
				map.put("id", transitionContext.getId());
				map.put("name", transitionContext.getName());
				map.put("fromStateId", transitionContext.getFromStateId());
				map.put("toStateId", transitionContext.getToStateId());
				map.put("type", transitionContext.getType());
				map.put("formId",transitionContext.getFormId());
				transitionPros.add(map);
			}
		}
		setResult(FacilioConstants.ContextNames.STATE_TRANSITION_LIST, transitionPros);
		return SUCCESS;
	}

	public String getStateTransitionFormList() throws Exception {

		FacilioChain chain = ReadOnlyChainFactory.getStateTransitionList();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.STATE_FLOW_ID, stateFlowId);
		chain.execute();

		List<StateflowTransitionContext> stateTransitions = (List<StateflowTransitionContext>) context.get(FacilioConstants.ContextNames.STATE_TRANSITION_LIST);
		List<Map<String, Object>> transitionPros = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(stateTransitions)) {
			for (StateflowTransitionContext transitionContext : stateTransitions) {
				Long formId = transitionContext.getFormId();
				if(formId != null && formId > 0){
					Map<String, Object> map = new HashMap<>();
					map.put("id", transitionContext.getId());
					map.put("name", transitionContext.getName());
					map.put("formId",formId);
					transitionPros.add(map);
				}
			}
		}
		setResult(FacilioConstants.ContextNames.STATE_TRANSITION_LIST, transitionPros);
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

		StateFlowRuleContext stateFlow = (StateFlowRuleContext) context.get(FacilioConstants.ContextNames.RECORD);
		StateFlowRuleContext oldStateFlow =  (StateFlowRuleContext) context.get(FacilioConstants.ContextNames.OLD_STATE_FLOW);

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(stateFlow.getModuleName());

		setResult(FacilioConstants.ContextNames.STATE_FLOW, context.get(FacilioConstants.ContextNames.STATE_FLOW));
		sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("Stateflow %s of %s has been cloned to {%s}.",oldStateFlow.getName(),module.getDisplayName(),stateFlow.getName()),
				null,
				AuditLogHandler.RecordType.SETTING,
				"StateFlow", stateFlow.getId())
				.setActionType(AuditLogHandler.ActionType.ADD)
				.setLinkConfig(((Function<Void, String>) o -> {
					JSONArray array = new JSONArray();
					JSONObject json = new JSONObject();
					json.put("id", stateFlow.getId());
					try {
						json.put("moduleName", stateFlow.getModuleName());
					} catch (Exception e) {
						e.printStackTrace();
					}
					json.put("navigateTo", "StateFlow");

					array.add(json);
					return array.toJSONString();
				}).apply(null))
		);
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
		FacilioChain chain = TransactionChainFactory.getDeleteStateFlowTransition();

		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.STATE_FLOW_ID, stateFlowId);
		context.put(FacilioConstants.ContextNames.TRANSITION_ID, stateTransitionId);
		context.put(FacilioConstants.ContextNames.TRANSITION, stateTransition);

		chain.execute();

		WorkflowRuleContext workflowRuleContext = (WorkflowRuleContext) context.get("workFlowRuleContext");

		sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("State transition %s has been deleted.",workflowRuleContext.getName()),
				null,
				AuditLogHandler.RecordType.SETTING,
				"StateTransition",stateTransitionId)
				.setActionType(AuditLogHandler.ActionType.DELETE)
		);
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

		boolean add = stateFlow.getId() <= 0;
		context.put(FacilioConstants.ContextNames.RECORD, stateFlow);
		FacilioChain chain = TransactionChainFactory.getAddOrUpdateStateFlow();
		chain.execute(context);

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(stateFlow.getModuleName());
		
		setResult(FacilioConstants.ContextNames.STATE_FLOW, stateFlow);
		sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("Stateflow {%s} of %s has been %s.", stateFlow.getName(), module.getDisplayName(), (add ? "added" : "updated")),
				null,
				AuditLogHandler.RecordType.SETTING,
				"StateFlow", stateFlow.getId())
				.setActionType(stateFlow.getId() < 0 ? AuditLogHandler.ActionType.ADD : AuditLogHandler.ActionType.UPDATE)
				.setLinkConfig(((Function<Void, String>) o -> {
					JSONArray array = new JSONArray();
					JSONObject json = new JSONObject();
					json.put("id", stateFlow.getId());
					try {
						json.put("moduleName", stateFlow.getModuleName());
					} catch (Exception e) {
						e.printStackTrace();
					}
					json.put("navigateTo", "StateFlow");

					array.add(json);
					return array.toJSONString();
				}).apply(null))
		);
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

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);

		WorkflowRuleContext rule = WorkflowRuleAPI.getWorkflowRule(workFlow.getId(),false,false);

		sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("StateFlow {%s} of %s status changed to %s.", rule.getName(),module.getDisplayName(), ((status==true)?"Active":"Inactive")),
				null,
				AuditLogHandler.RecordType.SETTING,
				"Change Status", workFlow.getId())
				.setActionType(AuditLogHandler.ActionType.UPDATE)
				.setLinkConfig(((Function<Void, String>) o -> {
					JSONArray array = new JSONArray();
					JSONObject json = new JSONObject();
					json.put("stateFlowId", workFlow.getId());
					json.put("moduleName", moduleName);
					json.put("navigateTo", "stateFlow");

					array.add(json);
					return array.toJSONString();
				}).apply(null))
		);

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

		if (Regions.US_WEST_2.getName().equals(FacilioProperties.getRegion())) {
			LOGGER.info("Action class of getAvailableState called");
		}

		FacilioChain chain = TransactionChainFactory.getAvailableState();
		FacilioContext context = chain.getContext();
		updateContext(context);
		context.put(FacilioConstants.ContextNames.RULE_TYPE, RuleType.STATE_FLOW);
		chain.execute();
		
		setResult(FacilioConstants.ContextNames.AVAILABLE_STATES, context.get("availableStates"));
		setResult(FacilioConstants.ContextNames.CURRENT_STATE, context.get("currentState"));

		if (Regions.US_WEST_2.getName().equals(FacilioProperties.getRegion())) {
			LOGGER.info("Action class done for getAvailableState" + AuthInterceptor.getResponseCode());
		}
		
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
	
	public String getTimeLog() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getTimeLogChain();
		FacilioContext context = chain.getContext();

		context.put(FacilioConstants.ContextNames.PARENT_MODULE_NAME, parentModuleName);
		context.put(FacilioConstants.ContextNames.ID, id);
		constructListContext(context);

		chain.execute();
		setResult(FacilioConstants.ContextNames.TIMELOGS, context.get(FacilioConstants.ContextNames.TIMELOGS));

		return SUCCESS;
	}

	public String getOfflineStateTransition() throws Exception {
		FacilioChain chain = TransactionChainFactory.getOfflineStateTransitionChain();
		FacilioContext context = chain.getContext();

		context.put(FacilioConstants.ContextNames.ID, id);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);

		chain.execute();

		setResult(FacilioConstants.ContextNames.OFFLINE_STATE_TRANSITION, context.get("offlineStateTransition"));
		return SUCCESS;
	}

}
