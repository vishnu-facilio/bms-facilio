package com.facilio.bmsconsole.actions;

import java.util.List;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.ApplicationLayoutContext;
import com.facilio.bmsconsole.context.ApplicationRelatedAppsContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.NewPermissionUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;

@Log4j
public class ApplicationAction extends FacilioAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private ApplicationContext application;
	@Getter
	@Setter
	private List<Long> relatedApplicationIds;

	private Boolean fetchMyApps = false;

	public Boolean getFetchMyApps() {
		return fetchMyApps;
	}

	public void setFetchMyApps(Boolean fetchMyApps) {
		this.fetchMyApps = fetchMyApps;
	}

	public ApplicationContext getApplication() {
		return application;
	}

	public void setApplication(ApplicationContext application) {
		this.application = application;
	}

	private long appId;

	public boolean isEmailVerificationNeeded() {
		return emailVerificationNeeded;
	}

	public void setEmailVerificationNeeded(boolean emailVerificationNeeded) {
		this.emailVerificationNeeded = emailVerificationNeeded;
	}

	private boolean emailVerificationNeeded = true;

	public long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}

	private Boolean userStatus;

	private Long roleId;

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Boolean getUserStatus() {
		return userStatus;	}

	public void setUserStatus(Boolean userStatus) {
		this.userStatus = userStatus;
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

	private long ouId;

	public long getOuId() {
		return ouId;
	}

	public void setOuId(long ouId) {
		this.ouId = ouId;
	}

	private Boolean fetchNonAppUsers;
	public Boolean getFetchNonAppUsers() {
		return fetchNonAppUsers;
	}
	public void setFetchNonAppUsers(Boolean fetchNonAppUsers) {
		this.fetchNonAppUsers = fetchNonAppUsers;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	private String moduleName;

	private String appName;

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	private Boolean fetchAllLayouts = false;

	public Boolean getFetchAllLayouts() {
		return fetchAllLayouts;
	}

	public void setFetchAllLayouts(Boolean fetchAllLayouts) {
		this.fetchAllLayouts = fetchAllLayouts;
	}

	private Boolean addLayout = false;

	public Boolean getAddLayout() {
		return addLayout;
	}

	public void setAddLayout(Boolean addLayout) {
		this.addLayout = addLayout;
	}

	private Boolean considerRole;
	public Boolean getConsiderRole() {
		return considerRole;
	}
	public void setConsiderRole(Boolean considerRole) {
		this.considerRole = considerRole;
	}

	private Boolean fetchCount;

	public Boolean getFetchCount() {
		if (fetchCount == null) {
			return false;
		}
		return fetchCount;
	}

	public void setFetchCount(Boolean fetchCount) {
		this.fetchCount = fetchCount;
	}

	private Long vendorsCount;
	public Long getVendorsCount() {
		return vendorsCount;
	}
	public void setVendorsCount(Long vendorsCount) {
		this.vendorsCount = vendorsCount;
	}

	private Boolean inviteAcceptStatus;
	public Boolean getInviteAcceptStatus() {
		return inviteAcceptStatus;
	}
	public void setInviteAcceptStatus(Boolean inviteAcceptStatus) {
		this.inviteAcceptStatus = inviteAcceptStatus;
	}

	private String signatureContent;

	public String getSignatureContent() {
		return signatureContent;
	}

	public void setSignatureContent(String signatureContent) {
		this.signatureContent = signatureContent;
	}

	public String addOrUpdateApplication() throws Exception {
		FacilioChain chain = TransactionChainFactory.getAddOrUpdateApplication();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.APPLICATION, application);
		context.put(FacilioConstants.ContextNames.ADD_APPLICATION_LAYOUT, getAddLayout());

		chain.execute();

		setResult(FacilioConstants.ContextNames.APPLICATION_ID,context.get(FacilioConstants.ContextNames.APPLICATION_ID));
		return SUCCESS;
	}

	public String getAllApplication() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getAllApplicationChain();
		FacilioContext context = chain.getContext();
		HttpServletRequest request = ServletActionContext.getRequest();

		context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
		context.put(FacilioConstants.ContextNames.FETCH_MY_APPS, getFetchMyApps());
		context.put(FacilioConstants.ContextNames.APP_DOMAIN, request.getServerName());

		chain.execute();

		setResult(FacilioConstants.ContextNames.APPLICATION, context.get(FacilioConstants.ContextNames.APPLICATION));
		return SUCCESS;
	}

	public String getAllApplicationBasedOnModule() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getAllApplicationBasedOnModuleChain();
		FacilioContext context = chain.getContext();
		HttpServletRequest request = ServletActionContext.getRequest();

		context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
		context.put(FacilioConstants.ContextNames.FETCH_MY_APPS, getFetchMyApps());
		context.put(FacilioConstants.ContextNames.APP_DOMAIN, request.getServerName());

		chain.execute();

		setResult(FacilioConstants.ContextNames.APPLICATION, context.get(FacilioConstants.ContextNames.APPLICATION));
		return SUCCESS;
	}

	public String addApplicationRelatedApps() throws Exception{
		FacilioChain chain = TransactionChainFactory.getAddApplicationRelatedAppsChain();
		FacilioContext context = chain.getContext();

		context.put(FacilioConstants.ContextNames.APPLICATION_ID,appId);
		context.put(FacilioConstants.ContextNames.APPLICATION_RELATED_APPS_LIST,relatedApplicationIds);
		chain.execute();

		return SUCCESS;
	}
	public String getAllApplicationRelatedApps() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getAllApplicationRelatedAppsChain();
		FacilioContext context = chain.getContext();

		context.put(FacilioConstants.ContextNames.APPLICATION_ID,appId);
		chain.execute();

		setResult(FacilioConstants.ContextNames.RELATED_APPLICATIONS, context.get(FacilioConstants.ContextNames.RELATED_APPLICATIONS));
		return SUCCESS;
	}

	public String deleteApplicationRelatedApps() throws Exception{
		FacilioChain chain = TransactionChainFactory.getDeleteApllicationRelatedAppsChain();
		FacilioContext context = chain.getContext();

		context.put(FacilioConstants.ContextNames.APPLICATION_ID,appId);
		context.put(FacilioConstants.ContextNames.APPLICATION_RELATED_APPS_LIST,relatedApplicationIds);
		chain.execute();

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
		context.put(FacilioConstants.ContextNames.LAYOUT_APP_TYPE, appName);
		context.put(FacilioConstants.ContextNames.FETCH_ALL_LAYOUTS, fetchAllLayouts);
		context.put(FacilioConstants.ContextNames.CONSIDER_ROLE, considerRole);
		context.put(FacilioConstants.ContextNames.ROLE_ID, roleId);

		HttpServletRequest request = ServletActionContext.getRequest();
		if(request.getAttribute("facilio.app.name") != null) {
			String appLinkName = (String) request.getAttribute("facilio.app.name");
			context.put(FacilioConstants.ContextNames.LINK_NAME, appLinkName);
		}
		chain.execute();
		setResult(FacilioConstants.ContextNames.APPLICATION, context.get(FacilioConstants.ContextNames.APPLICATION));
		if(AccountUtil.getCurrentUser() != null){
			Role currentUserRole = AccountUtil.getCurrentUser().getRole();
			if(currentUserRole == null) {
				return "unauthorized";
			}
			if(currentUserRole != null) {
				if(currentUserRole.getId() < 0) {
					return "unauthorized";
				}
				if (currentUserRole.getIsPrevileged() == null || !currentUserRole.getIsPrevileged()) {
					if (CollectionUtils.isEmpty(currentUserRole.getPermissions()) && CollectionUtils.isEmpty(currentUserRole.getNewPermissions())) {
						return "unauthorized";
					}
				}
				setResult("currentUserRole", currentUserRole);
			}
		}
		boolean isWebTabEnabled = AccountUtil.isFeatureEnabled(FeatureLicense.WEB_TAB);
		setResult("isWebTabEnabled", isWebTabEnabled);
		setResult("hasSetupPermission", NewPermissionUtil.hasSetupPermission());
		return SUCCESS;
	}

	public String addApplicationUsers() throws Exception {
		FacilioChain chain = TransactionChainFactory.addApplicationUsersChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.APPLICATION_ID, appId);
		context.put(FacilioConstants.ContextNames.IS_EMAIL_VERIFICATION_NEEDED,emailVerificationNeeded);
		context.put(FacilioConstants.ContextNames.USER, user);

		chain.execute();
		setResult(FacilioConstants.ContextNames.ORG_USER_ID, context.get(FacilioConstants.ContextNames.ORG_USER_ID));
		return SUCCESS;

	}

	public String getApplicationUsers() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getApplicationUsersChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.APPLICATION_ID, appId);
		if (fetchNonAppUsers != null) {
			context.put(FacilioConstants.ContextNames.FETCH_NON_APP_USERS, fetchNonAppUsers);
		}
		if (getFetchCount()) { // only count
			context.put(FacilioConstants.ContextNames.FETCH_COUNT, true);
		} else {
			JSONObject pagination = new JSONObject();
			pagination.put("page", getPage());
			pagination.put("perPage", getPerPage());
			if (getPerPage() < 0) {
				pagination.put("perPage", 5000);
			}
			context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
		}
		if(getSearch() != null)
		{
			context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
		}
		context.put(FacilioConstants.ContextNames.INVITE_ACCEPT_STATUS, getInviteAcceptStatus());
		context.put(FacilioConstants.ContextNames.USER_STATUS,getUserStatus());
		chain.execute();
		if(!getFetchCount()) {
			setResult(FacilioConstants.ContextNames.USERS, context.get(FacilioConstants.ContextNames.USERS));
		}
		else {
			setResult(FacilioConstants.ContextNames.COUNT, context.get(FacilioConstants.ContextNames.COUNT));
		}

		return SUCCESS;

	}

	public String getApplicationUserDetails() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getApplicationUserDetailsChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.APPLICATION_ID, appId);
		context.put(FacilioConstants.ContextNames.ORG_USER_ID, ouId);

		chain.execute();
		setResult(FacilioConstants.ContextNames.USER, context.get(FacilioConstants.ContextNames.USER));

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

	public String addOrUpdateUserSignature() throws Exception {
		FacilioChain chain = TransactionChainFactory.addorUpdateUserSignatureChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.SIGNATURE_CONTENT,signatureContent);

		chain.execute();
		setResult(FacilioConstants.ContextNames.SIGNATURE, context.get(FacilioConstants.ContextNames.SIGNATURE));

		return SUCCESS;

	}

	public String getUserSignature() throws Exception {
		FacilioChain chain = TransactionChainFactory.getUserSignatureChain();
		FacilioContext context = chain.getContext();
		chain.execute();
		setResult(FacilioConstants.ContextNames.SIGNATURE, context.get(FacilioConstants.ContextNames.SIGNATURE));

		return SUCCESS;

	}

	public String deleteUserSignature() throws Exception {
		FacilioChain chain = TransactionChainFactory.deleteUserSignatureChain();
		FacilioContext context = chain.getContext();
		chain.execute();
		setResult(FacilioConstants.ContextNames.SIGNATURE_DELETE, context.get(FacilioConstants.ContextNames.SIGNATURE_DELETE));

		return SUCCESS;

	}

	public String getAllLicensedPortals() throws Exception{
		List<ApplicationContext> portals = ApplicationApi.getLicensedPortalApps();
		setResult(FacilioConstants.ContextNames.PORTALS,portals);
		return SUCCESS;
	}
}
