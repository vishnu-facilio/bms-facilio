package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

import java.util.List;

public class ApprovalAction extends FacilioAction {

	private static final long serialVersionUID = 1L;

	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	private List<Long> ids;
	public List<Long> getIds() {
		return ids;
	}
	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	private Long transitionId;
	public Long getTransitionId() {
		return transitionId;
	}
	public void setTransitionId(Long transitionId) {
		this.transitionId = transitionId;
	}

	public String changeStatus() throws Exception {
		FacilioChain chain = TransactionChainFactory.getNextApprovalStateChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
		context.put(FacilioConstants.ContextNames.TRANSITION_ID, transitionId);
		chain.execute();
		return SUCCESS;
	}
}
