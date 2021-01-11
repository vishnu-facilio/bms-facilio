package com.facilio.trigger.action;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.util.TriggerUtil;
import com.facilio.v3.V3Action;

public class TriggerAction extends V3Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	BaseTriggerContext triggerContext;
	
	
	public BaseTriggerContext getTriggerContext() {
		return triggerContext;
	}

	public void setTriggerContext(BaseTriggerContext triggerContext) {
		this.triggerContext = triggerContext;
	}

	public String addTrigger() throws Exception {
		FacilioChain chain = TransactionChainFactoryV3.getTriggerAddChain();
		FacilioContext context = chain.getContext();
		context.put(TriggerUtil.TRIGGER_CONTEXT, triggerContext);
		chain.execute();
		setData(TriggerUtil.TRIGGER_CONTEXT, triggerContext);
        return SUCCESS;
	}
	
	public String updateTrigger() throws Exception {
		FacilioChain chain = TransactionChainFactoryV3.getTriggerUpdateChain();
		FacilioContext context = chain.getContext();
		context.put(TriggerUtil.TRIGGER_CONTEXT, triggerContext);
		chain.execute();
		return SUCCESS;
	}

	public String deleteTrigger() throws Exception {
		FacilioChain chain = TransactionChainFactoryV3.getTriggerDeleteChain();
		FacilioContext context = chain.getContext();
		context.put(TriggerUtil.TRIGGER_CONTEXT, triggerContext);
		chain.execute();
		setData(TriggerUtil.TRIGGER_CONTEXT, triggerContext);
    	return SUCCESS;
	}

	public String executeTrigger() throws Exception {
		triggerContext = TriggerUtil.getTrigger(triggerContext.getId());
		FacilioChain chain = TransactionChainFactoryV3.getTriggerExecuteChain();
		FacilioContext context = chain.getContext();
		context.put(TriggerUtil.TRIGGER_CONTEXT, triggerContext);
		chain.execute();
		setData(TriggerUtil.TRIGGER_CONTEXT, triggerContext);
    	return SUCCESS;
	}
}
