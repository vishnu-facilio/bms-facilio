package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
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
	
	public String updateStage() throws Exception {
		FacilioContext context = new FacilioContext();
		
		updateContext(context);
		Chain chain = TransactionChainFactory.getUpdateStageChain();
		chain.execute(context);
		
		return SUCCESS;
	}

	private void updateContext(FacilioContext context) {
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.ID, id);
		context.put("transistion_id", transitionId);
	}
}
