package com.facilio.trigger.action;

import java.util.List;
import java.util.Map;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.context.TriggerActionContext;
import com.facilio.trigger.context.TriggerFieldRelContext;
import com.facilio.trigger.context.TriggerType;
import com.facilio.trigger.util.TriggerChainUtil;
import com.facilio.trigger.util.TriggerUtil;
import com.facilio.v3.V3Action;

import lombok.Getter;
import lombok.Setter;

public class TriggerAction extends V3Action {
	private static final long serialVersionUID = 1L;
	
	private Map<String, Object> trigger;
	public Map<String, Object> getTrigger() {
		return trigger;
	}
	public void setTrigger(Map<String, Object> trigger) {
		this.trigger = trigger;
	}

	public String addOrUpdateTrigger() throws Exception {
		FacilioChain chain = TransactionChainFactoryV3.getInitTriggerAddOrUpdateChain();
		FacilioContext context = chain.getContext();
		context.put(TriggerUtil.TRIGGER_CONTEXT, FieldUtil.getAsBeanFromMap(trigger,  BaseTriggerContext.class));
		context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
		chain.execute();
		setData(TriggerUtil.TRIGGER_CONTEXT, trigger);
        return SUCCESS;
	}

	public String listTriggers() throws Exception {
		fetchTriggers(null);
		return SUCCESS;
	}
	
	private void fetchTriggers(FacilioContext contextParams) throws Exception {
		FacilioChain chain = TransactionChainFactoryV3.getTriggerListChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
		if (contextParams != null) {
			context.putAll(contextParams);
		}
		chain.execute();

		setData(TriggerUtil.TRIGGERS_LIST, FieldUtil.getAsMapList((List<?>) context.get(TriggerUtil.TRIGGERS_LIST), TriggerFieldRelContext.class));
	}
	
	public String triggerDetails() throws Exception {
		BaseTriggerContext triggerContext = TriggerUtil.getTrigger(getId(), true);
		setData(TriggerUtil.TRIGGER_CONTEXT, triggerContext);
    	return SUCCESS;
	}

	public String deleteTrigger() throws Exception {
		FacilioChain chain = TransactionChainFactoryV3.getTriggerDeleteChainV2();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.ID, getId());
		chain.execute();
		setMessage("Trigger deleted successfully!");
    	return SUCCESS;
	}

	private List<TriggerActionContext> actionList;
	public List<TriggerActionContext> getActionList() {
		return actionList;
	}
	public void setActionList(List<TriggerActionContext> actionList) {
		this.actionList = actionList;
	}

	public String reArrangeTriggerAction() throws Exception {
		FacilioChain chain = TransactionChainFactoryV3.rearrangeTriggerActionChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.ID, getId());
		context.put(FacilioConstants.ContextNames.ACTIONS_LIST, actionList);
		chain.execute();

		setMessage("Trigger Actions re-arranged successfully");

		return SUCCESS;
	}

	private long recordId = -1;
	public long getRecordId() {
		return recordId;
	}
	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}

	public String executeTrigger() throws Exception {
		FacilioChain chain = TransactionChainFactoryV3.getTriggerExecuteChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.ID, getId());
		context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
		context.put(FacilioConstants.ContextNames.RECORD_ID, recordId);
		chain.execute();
		setMessage("Trigger executed successfully!");
    	return SUCCESS;
	}

	private Boolean status;
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}

	public String changeStatus() throws Exception {
		FacilioChain chain = TransactionChainFactoryV3.getChangeStatusOfTriggerChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.ID, getId());
		context.put(FacilioConstants.ContextNames.STATUS, status);
		chain.execute();

		setMessage("Trigger status changed successfully");
		return SUCCESS;
	}
	
	@Getter @Setter
	private long agentId = -1;
	
	public String listAgentTriggers() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(ContextNames.TRIGGER_TYPE, TriggerType.AGENT_TRIGGER);
		if (agentId > 0) {
			Map<String, FacilioField> triggerFields = FieldFactory.getAsMap(FieldFactory.getAgentTriggerFields());
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition(triggerFields.get("agentId"), String.valueOf(agentId), NumberOperators.EQUALS));
			context.put(ContextNames.CRITERIA, criteria);
		}
		
		fetchTriggers(context);
		
		return SUCCESS;
	}
}
