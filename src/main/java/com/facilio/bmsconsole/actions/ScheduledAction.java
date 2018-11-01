package com.facilio.bmsconsole.actions;

import java.util.Collections;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ScheduledActionContext;
import com.facilio.bmsconsole.util.ScheduledActionAPI;
import com.facilio.constants.FacilioConstants;

public class ScheduledAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ScheduledActionContext scheduledAction;

	public ScheduledActionContext getScheduledAction() {
		return scheduledAction;
	}

	public void setScheduledAction(ScheduledActionContext scheduledAction) {
		this.scheduledAction = scheduledAction;
	}
	
	public String addScheduledAction() throws Exception {
		FacilioContext facilioContext = new FacilioContext();
		facilioContext.put(FacilioConstants.ContextNames.SCHEDULE_INFO, scheduledAction);
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(scheduledAction.getAction()));
		Chain addAction = TransactionChainFactory.getAddScheduledActionChain();
		addAction.execute(facilioContext);
		setResult("action", scheduledAction);
		return SUCCESS;
			
	}
	
	private long id;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private String toAddr;
	public String getToAddr() {
		return toAddr;
	}
	public void setToAddr(String toAddr) {
		this.toAddr = toAddr;
	}

	public String executeScheduledAction() throws Exception {
		ScheduledActionAPI.executeScheduledAction(id, toAddr);
		setResult("action", "success");
		return SUCCESS;
	}

}
