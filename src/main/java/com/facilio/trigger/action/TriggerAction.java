package com.facilio.trigger.action;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.trigger.context.Trigger;
import com.facilio.trigger.util.TriggerUtil;
import com.facilio.v3.V3Action;
import com.facilio.v3.exception.ErrorCode;

public class TriggerAction extends V3Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Trigger triggerContext;
	
	
	public Trigger getTriggerContext() {
		return triggerContext;
	}

	public void setTriggerContext(Trigger triggerContext) {
		this.triggerContext = triggerContext;
	}

	public String addTrigger() {
		
		try {
           
			FacilioChain chain = TransactionChainFactoryV3.getTriggerAddChain();
			
			FacilioContext context = chain.getContext();
			
			context.put(TriggerUtil.TRIGGER_CONTEXT, triggerContext);
			
			chain.execute();
			
			setData(TriggerUtil.TRIGGER_CONTEXT, triggerContext);
			
        } catch (Exception ex) {
            this.setCode(ErrorCode.UNHANDLED_EXCEPTION.getCode());
            this.setMessage("Internal Server Error" + ex);
            return "failure";
        }
        return SUCCESS;
	}
	
	public String updateTrigger() {
		
		try {
           
			FacilioChain chain = TransactionChainFactoryV3.getTriggerUpdateChain();
			
			FacilioContext context = chain.getContext();
			
			context.put(TriggerUtil.TRIGGER_CONTEXT, triggerContext);
			
			chain.execute();
			
			setData(TriggerUtil.TRIGGER_CONTEXT, triggerContext);
        } catch (Exception ex) {
            this.setCode(ErrorCode.UNHANDLED_EXCEPTION.getCode());
            this.setMessage("Internal Server Error" + ex);
            return "failure";
        }
        return SUCCESS;
	}

	public String deleteTrigger() {
	
	try {
       
		FacilioChain chain = TransactionChainFactoryV3.getTriggerDeleteChain();
		
		FacilioContext context = chain.getContext();
		
		context.put(TriggerUtil.TRIGGER_CONTEXT, triggerContext);
		
		chain.execute();
		
		setData(TriggerUtil.TRIGGER_CONTEXT, triggerContext);
		
    } catch (Exception ex) {
        this.setCode(ErrorCode.UNHANDLED_EXCEPTION.getCode());
        this.setMessage("Internal Server Error" + ex);
        return "failure";
    }
    return SUCCESS;
}

	public String executeTrigger() {
	
	try {
		triggerContext = TriggerUtil.getTrigger(triggerContext.getId());
		
		FacilioChain chain = TransactionChainFactoryV3.getTriggerExecuteChain();
		
		FacilioContext context = chain.getContext();
		
		context.put(TriggerUtil.TRIGGER_CONTEXT, triggerContext);
		
		chain.execute();
		
		setData(TriggerUtil.TRIGGER_CONTEXT, triggerContext);
    } catch (Exception ex) {
        this.setCode(ErrorCode.UNHANDLED_EXCEPTION.getCode());
        this.setMessage("Internal Server Error" + ex);
        return "failure";
    }
    return SUCCESS;
}
}
