package com.facilio.bmsconsole.actions;

import java.util.List;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
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
	
	private User user;
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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
	
	public String getApplicationDetails() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getApplicationDetails();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.APPLICATION_ID, appId);
		chain.execute();
		setResult(FacilioConstants.ContextNames.APPLICATION, context.get(FacilioConstants.ContextNames.APPLICATION));
		boolean isWebTabEnabled = AccountUtil.isFeatureEnabled(FeatureLicense.WEB_TAB);
		setResult("isWebTabEnabled", isWebTabEnabled);
		return SUCCESS;
	}
	
	public String addApplicationUsers() throws Exception {
		FacilioChain chain = TransactionChainFactory.addApplicationUsersChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.APPLICATION_ID, appId);
		context.put(FacilioConstants.ContextNames.USER, user);
		
		chain.execute();
		setResult(FacilioConstants.ContextNames.ORG_USER_ID, context.get(FacilioConstants.ContextNames.ORG_USER_ID));
		return SUCCESS;
	
	}
	
	public String getApplicationUsers() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getApplicationUsersChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.APPLICATION_ID, appId);
		chain.execute();
		setResult(FacilioConstants.ContextNames.USERS, context.get(FacilioConstants.ContextNames.USERS));
		
		return SUCCESS;
	
	}
	
	public String deleteApplicationUsers() throws Exception {
		FacilioChain chain = TransactionChainFactory.deleteApplicationUsersChain();
		chain.getContext().put(FacilioConstants.ContextNames.APPLICATION_ID, appId);
		chain.getContext().put(FacilioConstants.ContextNames.USER, user);
		
		FacilioContext context = chain.getContext();
		chain.execute();
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
		
		return SUCCESS;
	
	}

}
