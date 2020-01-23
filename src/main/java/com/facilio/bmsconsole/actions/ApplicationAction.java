package com.facilio.bmsconsole.actions;

import java.util.List;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ApplicationAction extends FacilioAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ApplicationContext application;

	public ApplicationContext getApplication() {
		return application;
	}

	public void setApplication(ApplicationContext application) {
		this.application = application;
	}

	private long appId;

	public long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}
	
	private List<Long> appIds;
	public List<Long> getAppIds() {
		return appIds;
	}
	public void setAppIds(List<Long> appIds) {
		this.appIds = appIds;
	}

	public String addOrUpdateApplication() throws Exception {
		FacilioChain chain = TransactionChainFactory.getAddOrUpdateApplication();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.APPLICATION, application);
		chain.execute();
		return SUCCESS;
	}

	public String getAllApplication() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getAllApplicationChain();
		FacilioContext context = chain.getContext();
		chain.execute();

		setResult(FacilioConstants.ContextNames.APPLICATION, context.get(FacilioConstants.ContextNames.APPLICATION));

		return SUCCESS;
	}
	
	public String markApplicationAsDefault() throws Exception {
		FacilioChain chain = TransactionChainFactory.markApplicationAsDefault();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.APPLICATION_ID, appId);
		chain.execute();
		return SUCCESS;
	}
	
	public String deleteApplication() throws Exception {
		FacilioChain chain = TransactionChainFactory.getDeleteApplicationsChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.APPLICATION_ID, appId);
		chain.execute();
		return SUCCESS;
	}

}
