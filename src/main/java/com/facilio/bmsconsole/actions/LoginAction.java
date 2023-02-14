package com.facilio.bmsconsole.actions;

import com.amazonaws.util.StringUtils;
import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.dto.*;
import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.sso.SSOUtil;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.DeviceContext.DeviceType;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsoleV3.context.V3CustomKioskContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.auth.SAMLAttribute;
import com.facilio.fw.auth.SAMLUtil;
import com.facilio.iam.accounts.context.SecurityPolicy;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.screen.context.RemoteScreenContext;
import com.facilio.screen.context.ScreenContext;
import com.facilio.screen.util.ScreenUtil;
import com.facilio.util.CurrencyUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.wms.endpoints.LiveSession.LiveSessionSource;
import com.facilio.wms.endpoints.LiveSession.LiveSessionType;
import com.facilio.wms.util.WmsApi;
import com.opensymphony.xwork2.ActionContext;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import java.io.File;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.Inflater;

public class LoginAction extends FacilioAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static {
		System.out.println("Login action loaded");
	}
	private static String HOSTNAME = null;
	private static Logger log = LogManager.getLogger(LoginAction.class.getName());
	private static final SimpleDateFormat SAML_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	private String response = null;

	public String getResponse() {
		return response;
	}

	// getter setter for identity token
	private String idToken;

	public String getIdToken() {
		return idToken;
	}

	public void setIdToken(String idToken) {
		this.idToken = idToken;
	}

	// getter setter for access token
	private String accessToken;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	private String mobileInstanceId;
	public String getMobileInstanceId() {
		return mobileInstanceId;
	}
	public void setMobileInstanceId(String mobileInstanceId) {
		this.mobileInstanceId = mobileInstanceId;
	}


	private HashMap<String, String> signupinfo = new HashMap<String, String>();

	public boolean isSignup() {
		return isSignup;
	}

	public void setSignup(boolean isSignup) {
		this.isSignup = isSignup;
	}

	private boolean isSignup = false;

	private long appId;

	public long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}

	public void setSignupInfo(String key, String value) {
		signupinfo.put(key, value);
	}

	public HashMap<String, String> getSignupInfo() {
		return signupinfo;
	}

	public String getSignupInfo(String signupkey) {
		return signupinfo.get(signupkey);
	}

	public String apiLogin() throws Exception {

		account = new HashMap<>();
		account.put("org", AccountUtil.getCurrentOrg());
		account.put("user", AccountUtil.getCurrentUser());

		return SUCCESS;
	}

	private String search;
	public void setSearch(String search) {
		this.search = search;
	}
	public String getSearch() {
		return this.search;
	}

	@Getter
	@Setter
	private String appLinkName;

	public String apiLogout() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();

		String appDomain = request.getServerName();
		boolean portalUser = false;
		AppDomain appdomainObj = IAMAppUtil.getAppDomain(appDomain);
		if(appdomainObj != null && appdomainObj.getAppDomainTypeEnum() != AppDomainType.FACILIO) {
			portalUser = true;
		}
		// end user session
		try {
			String facilioToken = null;
			String deviceType = request.getHeader("X-Device-Type");
			boolean isMobile = !StringUtils.isNullOrEmpty(deviceType)
					&& ("android".equalsIgnoreCase(deviceType) || "ios".equalsIgnoreCase(deviceType));
			if (!StringUtils.isNullOrEmpty(deviceType)
					&& ("android".equalsIgnoreCase(deviceType) || "ios".equalsIgnoreCase(deviceType))) {
				if(org.apache.commons.lang3.StringUtils.isEmpty(mobileInstanceId) || mobileInstanceId.equals("null")) {
					throw new IllegalArgumentException("Mobile Instance Id cannot be null");
				}
				String headerToken = request.getHeader("Authorization");
	            if (headerToken != null && headerToken.trim().length() > 0) {
	                if (headerToken.startsWith("Bearer facilio ")) {
	                    facilioToken = headerToken.replace("Bearer facilio ", "");
	                } else if(headerToken.startsWith("Bearer Facilio ")) { // added this check for altayer emsol data // Todo remove this later
	                    facilioToken = headerToken.replace("Bearer Facilio ", "");
	                } else {
	                    facilioToken = request.getHeader("Authorization").replace("Bearer ", "");
	                }
		        }
			}
			else {
				facilioToken = FacilioCookie.getUserCookie(request,"fc.idToken.facilio");
				if (org.apache.commons.lang3.StringUtils.isEmpty(facilioToken)) {
					facilioToken = FacilioCookie.getUserCookie(request, "fc.idToken.facilioportal");
				}
			}

			if (org.apache.commons.lang3.StringUtils.isNotEmpty(facilioToken)) {
				IAMAccount iamAccount = IAMUserUtil.verifiyFacilioTokenv3(facilioToken, false, null);
				if(iamAccount != null && IAMUserUtil.logOut(iamAccount.getUser().getUid(), facilioToken)) {
					HttpSession session = request.getSession();
					session.invalidate();
					FacilioCookie.eraseUserCookie(request, response, "fc.idToken.facilio", null);
					FacilioCookie.eraseUserCookie(request, response, "fc.idToken.proxy", null);
					if(portalUser) {
						FacilioCookie.eraseUserCookie(request, response, "fc.idToken.facilioportal", null);
					}

					//deleting .facilio.com cookie(temp handling)
					FacilioCookie.eraseUserCookie(request, response,"fc.idToken.facilio","facilio.com");
					FacilioCookie.eraseUserCookie(request, response,"fc.idToken.facilio","facilio.in");
					FacilioCookie.eraseUserCookie(request, response, "fc.idToken.proxy", "facilio.com");
					FacilioCookie.eraseUserCookie(request, response, "fc.idToken.proxy", "facilio.in");

					FacilioCookie.eraseUserCookie(request, response, "fc.currentSite", null);
					FacilioCookie.eraseUserCookie(request, response, "fc.switchValue", null);
					FacilioCookie.eraseUserCookie(request, response, "fc.currentOrg", null);

					if(mobileInstanceId != null) {
						IAMUserUtil.removeUserMobileSetting(mobileInstanceId, appLinkName);
					}

					if (!isMobile) {
						boolean isSSOEnabled = IAMAppUtil.isSSoEnabled(request.getServerName(), AccountUtil.getCurrentOrg().getOrgId());
						if (isSSOEnabled) {
							String ssoLogoutRequestURL = SSOUtil.getSSOLogoutRequestURL();
							if (ssoLogoutRequestURL != null) {
								response.sendRedirect(ssoLogoutRequestURL);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.info("Exception occurred ", e);
			setResult("message", "failure");
			return SUCCESS;
		}

		setResult("message", "success");
		return SUCCESS;
	}

	private JSONObject signupData;

	public void setSignupData(JSONObject signupData) {
		this.signupData = signupData;
	}

	public JSONObject getSignupData() {
		return this.signupData;
	}

	public JSONObject acceptUserInvite(String inviteToken) throws Exception {
		String[] inviteIds = EncryptionUtil.decode(inviteToken).split("#");
		long ouid = Long.parseLong(inviteIds[0]);
		Long.parseLong(inviteIds[1]);

		long inviteLinkExpireTime = (7 * 24 * 60 * 60 * 1000); // 7 days in seconds

		JSONObject invitation = new JSONObject();

		User user = AccountUtil.getUserBean().getUser(ouid, false);
		if (user == null || ((System.currentTimeMillis() - user.getInvitedTime()) > inviteLinkExpireTime)) {
			invitation.put("error", "link_expired");
		} else {
			boolean acceptStatus = true;// AccountUtil.getUserBean().acceptInvite(ouid, null);
			invitation.put("userid", ouid);
			if (acceptStatus) {
				user.setUserVerified(true);
				AccountUtil.getUserBean().updateUser(user);
				invitation.put("accepted", true);
			} else {
				invitation.put("accepted", false);
			}
		}
		return invitation;
	}

	public String acceptInvite() throws Exception {

		JSONObject invitation = acceptUserInvite(getInviteToken());
		ActionContext.getContext().getValueStack().set("invitation", invitation);

		return SUCCESS;
	}

	private String emailaddress;

	private String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private String inviteToken;

	public String getInviteToken() {
		return this.inviteToken;
	}

	public void setInviteToken(String inviteToken) {
		this.inviteToken = inviteToken;
	}

	private HashMap<String, Object> account;

	public HashMap<String, Object> getAccount() {
		return account;
	}

	public void setAccount(HashMap<String, Object> account) {
		this.account = account;
	}

	private HashMap<String, Object> createAccountResp;

	public HashMap<String, Object> getCreateAccountResp() {
		return createAccountResp;
	}

	public void setCreateAccountResp(HashMap<String, Object> createAccountResp) {
		this.createAccountResp = createAccountResp;
	}

	public String portalAccount() throws Exception {

		account = new HashMap<>();

		//temp handling to show portal related info in client..will need portal info for each app
		HttpServletRequest request = ServletActionContext.getRequest();
		String appDomain = request.getServerName();
		AppDomain appDomainObj = IAMAppUtil.getAppDomain(appDomain);
		if(appDomainObj != null) {
			account.put("appDomain", appDomainObj);
		}

		account.put("org", AccountUtil.getCurrentOrg());
		account.put("user", AccountUtil.getCurrentUser());
		account.put("portalInfo", AccountUtil.getPortalInfo());
		List<User> users = AccountUtil.getOrgBean().getAppUsers(AccountUtil.getCurrentOrg().getOrgId(), ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP), true);
		Map<String, Object> data = new HashMap<>();
		
		
		try {
			Map<String, Object> config = new HashMap<>();
			config.put("ws_endpoint", WmsApi.getWebsocketEndpoint(AccountUtil.getCurrentUser().getId(), LiveSessionType.TENANT_PORTAL, LiveSessionSource.WEB));
			config.put("new_ws_endpoint", WmsApi.getNewWebsocketEndpoint(request.getServerName(), AccountUtil.getCurrentUser().getId(), LiveSessionType.TENANT_PORTAL, LiveSessionSource.WEB));
			account.put("config",config);	
		} catch (Exception e) {
			log.error("Error getting socket endpoint in portal",e);
		}

		if (AccountUtil.getCurrentUser() != null) {
			long securityPolicyId = AccountUtil.getCurrentUser().getSecurityPolicyId();
			if (securityPolicyId > 0) {
				SecurityPolicy securityPolicy = IAMUserUtil.getUserSecurityPolicy(AccountUtil.getCurrentUser().getUid());
				account.put("isMFAEnabled", securityPolicy.getIsTOTPEnabled());
				if (securityPolicy.getIsWebSessManagementEnabled() != null && securityPolicy.getIsWebSessManagementEnabled()) {
					Long userSessionId = AccountUtil.getCurrentAccount().getUserSessionId();
					if (userSessionId != null && userSessionId > 0) {
						long sessionExpiry = IAMUserUtil.getSessionExpiry(AccountUtil.getCurrentUser().getUid(), userSessionId);
						account.put("sessionExpiry", sessionExpiry);
					}
				}
			}
		}

		Long userSessionId = AccountUtil.getCurrentAccount().getUserSessionId();
		if (userSessionId != null && userSessionId > 0) {
			Map<String, Object> userSession = IAMUserUtil.getUserSession(userSessionId);
			account.put("sessionEndTime", userSession.get("endTime"));
			account.put("proxyLoginUrl", FacilioProperties.getPortalProxyUserUrl());
		}

		data.put("users", users);
		data.put("ticketStatus", TicketAPI.getAllStatus(false));
		data.put("ticketType", TicketAPI.getTypes(AccountUtil.getCurrentOrg().getOrgId()));
		data.put("ticketCategory", TicketAPI.getCategories(AccountUtil.getCurrentOrg().getOrgId()));
		data.put("ticketPriority", TicketAPI.getPriorties(AccountUtil.getCurrentOrg().getOrgId()));
		data.put("assetCategory", AssetsAPI.getCategoryList());
		if (AccountUtil.getCurrentAccount().isFromMobile()) {
			data.put("sites", CommonCommandUtil.getMySites());
		}
		else {
		data.put("sites", SpaceAPI.getAllSites());
		}
		data.put("orgInfo", CommonCommandUtil.getOrgInfo());
		data.put("orgPrefs", PreferenceAPI.getAllOrgPreferences());
		data.put("orgEnabledPrefs", PreferenceAPI.getEnabledOrgPreferences());


		// temp	
		if (AccountUtil.getCurrentOrg().getOrgId() == 213l) {
			data.put("buildingList", ReportsUtil.getBuildingMap());		
		}
		//should not be sending this.. making it available for now since its used for mobile  
		account.put("License", AccountUtil.getFeatureLicense().get(AccountUtil.LicenseMapping.GROUP1LICENSE.getLicenseKey()));
		account.put("data", data);
		return SUCCESS;
	}
	
	public String v2portalAccount() throws Exception {
		portalAccount();
		setResult("account", account);
		return SUCCESS;
	}

	public String currentAccount() throws Exception {
		
		HashMap<String, Object> appProps = new HashMap<>();
		HttpServletRequest request = ServletActionContext.getRequest(); 
		
		appProps.put("permissions", AccountConstants.ModulePermission.toMap());
		appProps.put("permissions_groups", AccountConstants.PermissionGroup.toMap());
		appProps.put("operators", CommonCommandUtil.getOperators());
		appProps.put(FacilioConstants.ContextNames.ALL_METRICS, CommonCommandUtil.getAllMetrics());
		appProps.put(FacilioConstants.ContextNames.ORGUNITS_LIST, CommonCommandUtil.orgUnitsList());
		appProps.put(FacilioConstants.ContextNames.METRICS_WITH_UNITS, CommonCommandUtil.metricWithUnits());
		
		account = new HashMap<>();
		account.put("org", AccountUtil.getCurrentOrg());
		account.put("user", AccountUtil.getCurrentUser());
		account.put("timezone",AccountUtil.getCurrentAccount().getTimeZone()); 
		
		List<User> users = AccountUtil.getOrgBean().getAppUsers(AccountUtil.getCurrentOrg().getOrgId(), ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP), true);
		List<Group> groups = AccountUtil.getGroupBean().getOrgGroups(AccountUtil.getCurrentOrg().getId(), true);
		List<Role> roles = AccountUtil.getRoleBean(AccountUtil.getCurrentOrg().getOrgId()).getRolesForApps(appId <= 0 ? null : Collections.singletonList(appId));
		List<Organization> orgs = AccountUtil.getUserBean().getOrgs(AccountUtil.getCurrentUser().getUid());
		Map<Long, Set<Long>> userSites = new HashMap<>();
		if (users != null) {
			userSites = AccountUtil.getUserBean().getUserSites(users.stream().map(i -> i.getOuid()).collect(Collectors.toList()));
		}
		
		Map<String, Object> data = new HashMap<>();
		data.put("users", users);
		data.put("groups", groups);
		data.put("roles", roles);
		data.put("orgs", orgs);
		
		data.put("orgInfo", CommonCommandUtil.getOrgInfo());
		data.put("orgPrefs", PreferenceAPI.getAllOrgPreferences());
		data.put("orgEnabledPrefs", PreferenceAPI.getEnabledOrgPreferences());


		data.put("ticketCategory", TicketAPI.getCategories(AccountUtil.getCurrentOrg().getOrgId()));
		data.put("ticketPriority", TicketAPI.getPriorties(AccountUtil.getCurrentOrg().getOrgId()));
		data.put("ticketType", TicketAPI.getTypes(AccountUtil.getCurrentOrg().getOrgId()));
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		if (module != null) {
			StateFlowRuleContext defaultStateFlow = StateFlowRulesAPI.getDefaultStateFlow(module);
			if (defaultStateFlow != null) {
				data.put("defaultWorkOrderStateFlowId", defaultStateFlow.getId());
			}
		}
		
		data.put("alarmSeverity", AlarmAPI.getAlarmSeverityList());
		data.put("assetCategory", AssetsAPI.getCategoryList());
		data.put("assetType", AssetsAPI.getTypeList());
//		data.put("operatingHour", BusinessHoursAPI.getBusinessHours());
		data.put("assetDepartment", AssetsAPI.getDepartmentList());
//		data.put("inventoryVendors", InventoryApi.getInventoryVendorList());
//		data.put("inventoryCategory", InventoryApi.getInventoryCategoryList());
		data.put("userSites", userSites);
		
		try {
		data.put("inventoryCategory", InventoryApi.getInventoryCategoryList());
		data.put("itemStatus", InventoryApi.getItemStatusList());
		data.put("toolStatus", InventoryApi.getToolStatusList());
		}catch (Exception e) {
			// TODO: handle exception
		}
		 
		data.put("serviceList", ReportsUtil.getPurposeMapping());
		data.put("buildingList", ReportsUtil.getBuildingMap());
		data.put("energyMeters", DeviceAPI.getAllMainEnergyMeters());
		data.put("calendarColor", TicketAPI.getCalendarColor());
		data.put(FacilioConstants.ContextNames.TICKET_TYPE, CommonCommandUtil.getPickList(FacilioConstants.ContextNames.TICKET_TYPE));
		data.put(FacilioConstants.ContextNames.SPACE_CATEGORY, CommonCommandUtil.getPickList(FacilioConstants.ContextNames.SPACE_CATEGORY));
		data.put(FacilioConstants.ContextNames.ASSET_CATEGORY, CommonCommandUtil.getPickList(FacilioConstants.ContextNames.ASSET_CATEGORY));
		data.put(FacilioConstants.ContextNames.SHIFTS, ShiftAPI.getAllShifts());
		
		
		Map<String, Collection<FacilioForm>> forms = FormsAPI.getMobileForms();
		data.put("forms", forms);
		data.put("ticketStatus", getTicketStatus());
		data.put("ticket_status", TicketAPI.getAllStatus(true));
		
		data.put("mysites", CommonCommandUtil.getMySites());
		data.put("buildings", SpaceAPI.getAllBuildings());
		data.put("sites", SpaceAPI.getAllSites());
		
		Map<String, Object> config = new HashMap<>();
		config.put("ws_endpoint", WmsApi.getWebsocketEndpoint(AccountUtil.getCurrentUser().getId(), LiveSessionType.APP, LiveSessionSource.WEB));
		config.put("payment_endpoint", getPaymentEndpoint());
		Properties buildinfo = (Properties)ServletActionContext.getServletContext().getAttribute("buildinfo");
		config.put("build", buildinfo);
		account.put("data", data);
		account.put("config", config);
		account.put("appProps", appProps);
		//should not be sending this.. making it available for now since its used for mobile  
		account.put("License", AccountUtil.getFeatureLicense().get(AccountUtil.LicenseMapping.GROUP1LICENSE.getLicenseKey()));
		
		return SUCCESS;
	}

	public String tvAccount() throws Exception {

		HashMap<String, Object> appProps = new HashMap<>();
		appProps.put("permissions", AccountConstants.ModulePermission.toMap());
		appProps.put("permissions_groups", AccountConstants.PermissionGroup.toMap());
		HttpServletRequest request = ServletActionContext.getRequest(); 
		HttpServletResponse httpResponse = ServletActionContext.getResponse();
		
		account = new HashMap<>();
		account.put("org", AccountUtil.getCurrentOrg());
		account.put("user", AccountUtil.getCurrentUser());
		List<User> users = AccountUtil.getOrgBean().getAppUsers(AccountUtil.getCurrentOrg().getOrgId(), ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP), true);
		List<Group> groups = AccountUtil.getGroupBean().getOrgGroups(AccountUtil.getCurrentOrg().getId(), true);
		List<Role> roles = AccountUtil.getRoleBean(AccountUtil.getCurrentOrg().getOrgId()).getRolesForApps(appId <= 0 ? null : Collections.singletonList(appId));
		List<Organization> orgs = AccountUtil.getUserBean().getOrgs(AccountUtil.getCurrentUser().getUid());

		Map<String, Object> data = new HashMap<>();
		data.put("users", users);
		data.put("groups", groups);
		data.put("roles", roles);
		data.put("orgs", orgs);

		data.put("orgInfo", CommonCommandUtil.getOrgInfo());

		data.put("ticketCategory", TicketAPI.getCategories(AccountUtil.getCurrentOrg().getOrgId()));
		data.put("ticketPriority", TicketAPI.getPriorties(AccountUtil.getCurrentOrg().getOrgId()));
		data.put("ticketType", TicketAPI.getTypes(AccountUtil.getCurrentOrg().getOrgId()));

		data.put("alarmSeverity", AlarmAPI.getAlarmSeverityList());
		data.put("assetCategory", AssetsAPI.getCategoryList());
		data.put("assetType", AssetsAPI.getTypeList());
		data.put("assetDepartment", AssetsAPI.getDepartmentList());
//		data.put("inventoryVendors", InventoryApi.getInventoryVendorList());
//		data.put("inventoryCategory", InventoryApi.getInventoryCategoryList());

		try {
		data.put("itemStatus", InventoryApi.getItemStatusList());
		data.put("toolStatus", InventoryApi.getToolStatusList());
		}catch (Exception e) {
			// TODO: handle exception
		}
		 
		data.put("serviceList", ReportsUtil.getPurposeMapping());
		data.put("buildingList", ReportsUtil.getBuildingMap());
		data.put("ticketStatus", TicketAPI.getAllStatus(false));
		data.put("energyMeters", DeviceAPI.getAllMainEnergyMeters());
		
		data.put(FacilioConstants.ContextNames.TICKET_TYPE,
				CommonCommandUtil.getPickList(FacilioConstants.ContextNames.TICKET_TYPE));
		data.put(FacilioConstants.ContextNames.SPACE_CATEGORY,
				CommonCommandUtil.getPickList(FacilioConstants.ContextNames.SPACE_CATEGORY));
		data.put(FacilioConstants.ContextNames.ASSET_CATEGORY,
				CommonCommandUtil.getPickList(FacilioConstants.ContextNames.ASSET_CATEGORY));

		RemoteScreenContext remoteScreen = AccountUtil.getCurrentAccount().getRemoteScreen();
		if (remoteScreen.getScreenId() != null && remoteScreen.getScreenId() > 0) {
			ScreenContext screenContext = ScreenUtil.getScreen(remoteScreen.getScreenId());
			screenContext.setScreenDashboards(ScreenUtil.getScreenDashboardRel(screenContext));
			remoteScreen.setScreenContext(screenContext);
			
			if (screenContext.getSiteId() != null) {
				FacilioCookie.addCurrentSiteCookie(String.valueOf(screenContext.getSiteId()), httpResponse);
			}
			else {
				FacilioCookie.eraseUserCookie(request, httpResponse, "fc.currentSite", null);
				FacilioCookie.eraseUserCookie(request, httpResponse, "fc.switchValue", null);
			}
		}
		data.put("connectedScreen", remoteScreen);

		Map<String, Object> config = new HashMap<>();
		config.put("ws_endpoint", WmsApi.getWebsocketEndpoint(remoteScreen.getId(), LiveSessionType.REMOTE_SCREEN, LiveSessionSource.WEB));
		config.put("new_ws_endpoint", WmsApi.getNewWebsocketEndpoint(request.getServerName(), remoteScreen.getId(), LiveSessionType.REMOTE_SCREEN, LiveSessionSource.WEB));

		account.put("data", data);
		account.put("config", config);
		account.put("appProps", appProps);
		//should not be sending this.. making it available for now since its used for mobile  
		account.put("License", AccountUtil.getFeatureLicense().get(AccountUtil.LicenseMapping.GROUP1LICENSE.getLicenseKey()));

		return SUCCESS;
	}

	public String deviceAccount() throws Exception{
		System.out.println("Device connect successfull");

		HttpServletRequest request = ServletActionContext.getRequest();

		account = new HashMap<>();
		HashMap<String, Object> appProps = new HashMap<>();
		appProps.put("permissions", AccountConstants.ModulePermission.toMap());
		appProps.put("permissions_groups", AccountConstants.PermissionGroup.toMap());
		appProps.put("operators", CommonCommandUtil.getOperators());
		appProps.put(FacilioConstants.ContextNames.ALL_METRICS, CommonCommandUtil.getAllMetrics());
		appProps.put(FacilioConstants.ContextNames.ORGUNITS_LIST, CommonCommandUtil.orgUnitsList());
		appProps.put(FacilioConstants.ContextNames.METRICS_WITH_UNITS, CommonCommandUtil.metricWithUnits());
		account.put("appProps", appProps);
		


		Map<String, Object> config = new HashMap<>();
		config.put("payment_endpoint", getPaymentEndpoint());
		Properties buildinfo = (Properties)ServletActionContext.getServletContext().getAttribute("buildinfo");
		config.put("build", buildinfo);
		account.put("config", config);
		
		account.put("org", AccountUtil.getCurrentOrg());
		account.put("user", AccountUtil.getCurrentUser());
		account.put("timezone",AccountUtil.getCurrentAccount().getTimeZone()); 

		//should not be sending this.. making it available for now since its used for mobile  
		account.put("License", AccountUtil.getFeatureLicense().get(AccountUtil.LicenseMapping.GROUP1LICENSE.getLicenseKey()));
		
		List<User> users = AccountUtil.getOrgBean().getAppUsers(AccountUtil.getCurrentOrg().getOrgId(), ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP), true);
		Map<Long, Set<Long>> userSites = new HashMap<>();
		if (users != null) {
			userSites = AccountUtil.getUserBean().getUserSites(users.stream().map(i -> i.getOuid()).collect(Collectors.toList()));
		}
		
		
		Map<String, Object> data = new HashMap<>();
		data.put("orgInfo", CommonCommandUtil.getOrgInfo());
		data.put("users", users);
		data.put("userSites", userSites);
		account.put("data", data);
		
		ConnectedDeviceContext connectedDevice = AccountUtil.getCurrentAccount().getConnectedDevice();
		DeviceContext device=DevicesAPI.getDevice(connectedDevice.getDeviceId());//to do , fetch parentDevice table details also in each kioskType API only,change websockets to use connectedDevice ID instead of device ID
		if (connectedDevice!= null && connectedDevice.getDeviceId()> 0) {
			data.put("device", device);
			
			if(device.getDeviceTypeEnum()==DeviceType.VISITOR_KIOSK)
			{
				VisitorKioskContext visitorKioskCtx=DevicesAPI.getVisitorKioskDetails(device.getId());
				data.put("visitorKiosk",visitorKioskCtx);
			}
			if(device.getDeviceTypeEnum()==DeviceType.FEEDBACK_KIOSK)
			{
				FeedbackKioskContext feedbackKiosk=DevicesAPI.getFeedbackKioskDetails(device.getId());
				data.put("feedbackKiosk",feedbackKiosk);
			}
			if(device.getDeviceTypeEnum()==DeviceType.SMART_CONTROL_KIOSK)
			{
				SmartControlKioskContext smartControlKiosk=DevicesAPI.getSmartControlKiosk(device.getId());
				data.put("smartControlKiosk",smartControlKiosk);
			}
			else
			if(device.getDeviceTypeEnum()==DeviceType.CUSTOM_KIOSK)
			{
				V3CustomKioskContext customKiosk =DevicesAPI.getCustomKioskDetails(device.getId());
				data.put(FacilioConstants.ContextNames.CUSTOM_KIOSK_DATA,customKiosk);

			}
			config.put("ws_endpoint", WmsApi.getWebsocketEndpoint(device.getId(), LiveSessionType.DEVICE, ((device.getDeviceTypeEnum()==DeviceType.VISITOR_KIOSK) ? LiveSessionSource.TABLET : LiveSessionSource.WEB)));
			config.put("new_ws_endpoint", WmsApi.getNewWebsocketEndpoint(request.getServerName(), device.getId(), LiveSessionType.DEVICE, ((device.getDeviceTypeEnum()==DeviceType.VISITOR_KIOSK) ? LiveSessionSource.TABLET : LiveSessionSource.WEB)));
		}
		
		

		
		setResult("account", account);
		

		

	
		return SUCCESS;

	}
	private String switchOrgDomain;

	public String getSwitchOrgDomain() {
		return switchOrgDomain;
	}

	public void setSwitchOrgDomain(String switchOrgDomain) {
		this.switchOrgDomain = switchOrgDomain;
	}

	public String switchCurrentAccount() throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();

		FacilioCookie.addOrgDomainCookie(getSwitchOrgDomain(), response);

		FacilioCookie.eraseUserCookie(request, response, "fc.currentSite", null);
		FacilioCookie.eraseUserCookie(request, response, "fc.switchValue", null);
		return SUCCESS;
	}

	public static Map<String, Object> getPaymentEndpoint() {
		String BaseUrl = FacilioProperties.getConfig("payment.url");
		String Standard = BaseUrl
				+ "facil-blossom?addons[id][0]=staff-basic&addons[quantity][0]=10&addons[id][1]=buildings&addons[quantity][1]=5";
		String Professional = BaseUrl
				+ "professional?addons[id][0]=staff-professional&addons[quantity][0]=10&addons[id][1]=building-professional&addons[quantity][1]=5";
		String Enterprise = BaseUrl
				+ "professional?addons[id][0]=staff-professional&addons[quantity][0]=10&addons[id][1]=building-professional&addons[quantity][1]=5";
		Map<String, Object> url = new HashMap<>();
		url.put("standard", Standard);
		url.put("professional", Professional);
		url.put("enterprise", Enterprise);
		return url;
	}

	private String generateSignedSAMLResponse(SAMLAttribute samlAttr) throws Exception {
		ClassLoader classLoader = LoginAction.class.getClassLoader();
		File samlXML = new File(classLoader.getResource(FacilioUtil.normalizePath("conf/saml/saml-response.xml")).getFile());
		String samlTemplate = SAMLUtil.getFileAsString(samlXML);

		Date dt = new Date();
		String samlDT = SAML_DATE_FORMAT.format(dt);
		String randomUUID_1 = UUID.randomUUID().toString();
		String randomUUID_2 = UUID.randomUUID().toString();
		String samlResponse = samlTemplate.replaceAll("--IssueInstant--|--AuthnInstant--", samlDT);
		samlResponse = samlResponse.replaceAll("--Issuer--", samlAttr.getIssuer());
		samlResponse = samlResponse.replaceAll("--ResponseID--", "_" + randomUUID_1);
		samlResponse = samlResponse.replaceAll("--AssertionID--", "SAML_" + randomUUID_2);
		samlResponse = samlResponse.replaceAll("--SessionIndex--", "SAML_" + randomUUID_2);
		samlResponse = samlResponse.replaceAll("--EmailID--", samlAttr.getEmail());
		samlResponse = samlResponse.replaceAll("--InResponseTo--", samlAttr.getInResponseTo());
		samlResponse = samlResponse.replaceAll("--Recipient--|--Destination--", samlAttr.getRecipient());
		samlResponse = samlResponse.replaceAll("--AudienceRestriction--", samlAttr.getIntendedAudience());
		samlResponse = samlResponse.replaceAll("--ConditionsNotBefore--", getNotBeforeDateAndTime());
		samlResponse = samlResponse.replaceAll("--ConditionsNotOnOrAfter--|--SubjectNotOnOrAfter--",
				getNotOnOrAfterDateAndTime());

		Document document = SAMLUtil.convertStringToDocument(samlResponse);

		if (samlAttr.getCustomAttr() != null) {

			NodeList assertions = document.getElementsByTagName("Assertion");
			if (assertions != null && assertions.getLength() > 0) {

				Element assertion = (Element) assertions.item(0);
				JSONObject customAttr = samlAttr.getCustomAttr();

				Iterator<String> keys = customAttr.keySet().iterator();
				Element attributeStatement = document.createElement("AttributeStatement");
				while (keys.hasNext()) {
					String attr = keys.next();
					String value = customAttr.get(attr).toString();
					Element cuatomAttrElement = newAttrTag(document, attr, value);
					attributeStatement.appendChild(cuatomAttrElement);
				}
				assertion.appendChild(attributeStatement);
			}
		}

		String Temp = SAMLUtil.convertDomToString(document);
		document = SAMLUtil.convertStringToDocument(Temp);

		document = signSAMLDocument(document, samlAttr.getPrivateKey(), samlAttr.getX509Certificate());

		samlResponse = SAMLUtil.convertDomToString(document);

		byte[] enc = org.apache.commons.codec.binary.Base64.encodeBase64(samlResponse.getBytes("UTF-8"));
		return new String(enc, "UTF-8");
	}

	public static Document signSAMLDocument(Document root, PrivateKey privKey, X509Certificate x509Cert)
			throws KeyException, CertificateException {

		XMLSignatureFactory xmlSigFactory = XMLSignatureFactory.getInstance("DOM");

		DOMSignContext domSignCtx = new DOMSignContext(privKey, root.getElementsByTagName("Assertion").item(0));
		domSignCtx.setDefaultNamespacePrefix("ds");

		domSignCtx.setNextSibling(root.getElementsByTagName("Subject").item(0));
		Element assertion = (Element) root.getElementsByTagName("Assertion").item(0);
		assertion.setIdAttributeNode(assertion.getAttributeNode("ID"), true);
		String reference_URI = assertion.getAttribute("ID");
		Reference ref = null;
		SignedInfo signedInfo = null;
		try {
			Transform transform1 = xmlSigFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null);
			Transform transform2 = xmlSigFactory.newTransform("http://www.w3.org/2001/10/xml-exc-c14n#",
					(TransformParameterSpec) null);
			ArrayList<Transform> listt = new ArrayList<Transform>();
			listt.add(transform1);
			listt.add(transform2);
			ref = xmlSigFactory.newReference("#" + reference_URI,
					xmlSigFactory.newDigestMethod(DigestMethod.SHA1, null), listt, null, null);
			signedInfo = xmlSigFactory.newSignedInfo(
					xmlSigFactory.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE,
							(C14NMethodParameterSpec) null),
					xmlSigFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null), Collections.singletonList(ref));

		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		} catch (InvalidAlgorithmParameterException ex) {
			ex.printStackTrace();
		}

		KeyInfoFactory keyInfoFactory = xmlSigFactory.getKeyInfoFactory();

		ArrayList x509Content = new ArrayList();
		x509Content.add(x509Cert);
		X509Data xd = keyInfoFactory.newX509Data(x509Content);
		KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(xd));

		XMLSignature xmlSignature = xmlSigFactory.newXMLSignature(signedInfo, keyInfo);
		try {
			// Sign the document
			xmlSignature.sign(domSignCtx);
		} catch (Exception e) {
			log.info("Exception occurred ", e);
		}
		return root;
	}

	private Element newAttrTag(Document doc, String attribute, String value) {
		Element attributeTag = doc.createElement("Attribute");
		attributeTag.setAttribute("Name", attribute);
		Element attributeValue = doc.createElement("AttributeValue");
		attributeValue.setAttribute("xsi:type", "xs:string");
		attributeValue.setTextContent(value);
		attributeTag.appendChild(attributeValue);
		return attributeTag;
	}

	private String getNotBeforeDateAndTime() {
		Calendar beforeCal = Calendar.getInstance();
		beforeCal.add(Calendar.MINUTE, -5);
		return SAML_DATE_FORMAT.format(beforeCal.getTime());
	}

	private String getNotOnOrAfterDateAndTime() {
		Calendar afterCal = Calendar.getInstance();
		afterCal.add(Calendar.MINUTE, 15);
		return SAML_DATE_FORMAT.format(afterCal.getTime());
	}

	private String decodeSAMLRequest(String encodedStr) {
		try {
			byte[] decoded = org.apache.commons.codec.binary.Base64.decodeBase64(encodedStr);
			Inflater inf = new Inflater(true);

			inf.setInput(decoded);
			byte[] message = new byte[5000];
			int resultLength = inf.inflate(message);
			inf.end();
			return new String(message, 0, resultLength, "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings({ "unchecked" })
	private JSONObject getTicketStatus() {
		try {
			JSONObject result = new JSONObject();
			List<FacilioStatus> ticketStatusList = TicketAPI.getAllStatus(false);
			for (FacilioStatus tsc : ticketStatusList) {
				result.put(tsc.getId(), tsc.getStatus());
			}
			return result;
		} catch (Exception e) {
			log.info("Exception occurred ", e);
		}
		return null;
	}

	public String getEmailaddress() {
		return emailaddress;
	}

	public void setEmailaddress(String emailaddress) {
		this.emailaddress = emailaddress;
	}

	/****************** V2 Api ******************/

	public String v2currentAccount() throws Exception {
		currentAccount();
		setResult("account", account);
		return SUCCESS;
	}
	public String fetchCurrentAccount() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();

		account = new HashMap<>();
		HashMap<String, Object> appProps = new HashMap<>();
		appProps.put("permissions", AccountConstants.ModulePermission.toMap());
		appProps.put("permissions_groups", AccountConstants.PermissionGroup.toMap());
		appProps.put("operators", CommonCommandUtil.getOperators());
		appProps.put(FacilioConstants.ContextNames.ALL_METRICS, CommonCommandUtil.getAllMetrics());
		appProps.put(FacilioConstants.ContextNames.ORGUNITS_LIST, CommonCommandUtil.orgUnitsList());
		appProps.put(FacilioConstants.ContextNames.METRICS_WITH_UNITS, CommonCommandUtil.metricWithUnits());
		account.put("appProps", appProps);

		Map<String, Object> config = new HashMap<>();
		config.put("ws_endpoint", WmsApi.getWebsocketEndpoint(AccountUtil.getCurrentUser().getId(), LiveSessionType.APP, LiveSessionSource.WEB));
		config.put("new_ws_endpoint", WmsApi.getNewWebsocketEndpoint(request.getServerName(), AccountUtil.getCurrentUser().getId(), LiveSessionType.APP, LiveSessionSource.WEB));

		config.put("payment_endpoint", getPaymentEndpoint());
		Properties buildinfo = (Properties) ServletActionContext.getServletContext().getAttribute("buildinfo");
		config.put("build", buildinfo);

		config.put("mailDomain", FacilioProperties.getMailDomain());
		account.put("config", config);

		account.put("org", AccountUtil.getCurrentOrg());
		account.put("user", AccountUtil.getCurrentUser());

		boolean isDev = false;

		Account currentAccount = AccountUtil.getCurrentAccount();
		if (currentAccount != null) {
			User user = currentAccount.getUser();
			isDev = user != null && user.isSuperAdmin();
		}

		if (!isDev) {
			List<Long> devAppIds = new ArrayList<>();
			long applicationIdForLinkName = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.DEVELOPER_APP);
			if (applicationIdForLinkName > 0) {
				devAppIds.add(applicationIdForLinkName);
			}

			if (!devAppIds.isEmpty()) {
				List<Role> rolesList = AccountUtil.getRoleBean(AccountUtil.getCurrentOrg().getOrgId()).getRolesForApps(devAppIds);
				RoleBean roleBean = AccountUtil.getRoleBean();
				List<OrgUserApp> rolesAppsMappingForUser = roleBean.getRolesAppsMappingForUser(AccountUtil.getCurrentAccount().getUser().getOuid());
				List<Long> roleIds = rolesAppsMappingForUser.stream().map(OrgUserApp::getRoleId).collect(Collectors.toList());
				Set<Long> roleSet = new HashSet<>(roleIds);
				isDev = !CollectionUtils.isEmpty(rolesList)
						&& rolesList.stream().anyMatch(i -> roleSet.contains(i.getRoleId()));
			}
		}

		account.put("isDev", isDev);

		if (AccountUtil.getCurrentUser() != null) {
			long securityPolicyId = AccountUtil.getCurrentUser().getSecurityPolicyId();
			if (securityPolicyId > 0) {
				SecurityPolicy securityPolicy = IAMUserUtil.getUserSecurityPolicy(AccountUtil.getCurrentUser().getUid());
				account.put("isMFAEnabled", securityPolicy.getIsTOTPEnabled());
				if (securityPolicy.getIsWebSessManagementEnabled() != null && securityPolicy.getIsWebSessManagementEnabled()) {
					Long userSessionId = AccountUtil.getCurrentAccount().getUserSessionId();
					if (userSessionId != null && userSessionId > 0) {
						long sessionExpiry = IAMUserUtil.getSessionExpiry(AccountUtil.getCurrentUser().getUid(), userSessionId);
						account.put("sessionExpiry", sessionExpiry);
					}
				}
			}
		}

		Long userSessionId = AccountUtil.getCurrentAccount().getUserSessionId();
		if (userSessionId != null && userSessionId > 0) {
			Map<String, Object> userSession = IAMUserUtil.getUserSession(userSessionId);
			account.put("sessionEndTime", userSession.get("endTime"));
			account.put("proxyLoginUrl", FacilioProperties.getProxyUrl());
		}

		account.put("timezone",AccountUtil.getCurrentAccount().getTimeZone()); 
		//should not be sending this.. making it available for now since its used for mobile  
		account.put("License", AccountUtil.getFeatureLicense().get(AccountUtil.LicenseMapping.GROUP1LICENSE.getLicenseKey()));
		
		List<User> users = null;

		ApplicationContext maintenanceApp = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
		if(maintenanceApp == null) {
			maintenanceApp = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
		}
		List<ApplicationContext> allApps = ApplicationApi.getAllApplicationsForDomain(maintenanceApp.getDomainType());
		if(CollectionUtils.isNotEmpty(allApps)) {
			List<Long> appIds = allApps.stream().map(ApplicationContext::getId).collect(Collectors.toList());
			users = AccountUtil.getOrgBean().getAppUsers(AccountUtil.getCurrentOrg().getOrgId(), -1, -1, true,
					false, 0, 5000, null, true, true, null, appIds, null, null);
		}

		Map<Long, Set<Long>> userSites = new HashMap<>();
		if (users != null) {
			userSites = AccountUtil.getUserBean().getUserSites(users.stream().map(i -> i.getOuid()).collect(Collectors.toList()));
		}
		
		Map<String, Object> data = new HashMap<>();
		data.put("orgInfo", CommonCommandUtil.getOrgInfo());
		data.put("orgPrefs", PreferenceAPI.getAllOrgPreferences());
		data.put("orgEnabledPrefs", PreferenceAPI.getEnabledOrgPreferences());
		data.put("users", users);
		data.put("userSites", userSites);

		data.put("currencyInfo", CurrencyUtil.getCurrencyInfo());

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		List<FacilioStatus> workorderTicketStatuses = TicketAPI.getAllStatus(module, false);
		data.put("ticketStatus", workorderTicketStatuses);

		data.put("ticketStatus",workorderTicketStatuses);

		account.put("data", data);
		
		setResult("account", account);
		return SUCCESS;
	}

	public String fetchFeatures() throws Exception {
		TreeMap<String, Boolean> features = AccountUtil.getFeatureLicenseMap(AccountUtil.getCurrentOrg().getOrgId());
		setResult("features", features);
		return SUCCESS;
	}
	
	public String getMySiteList() throws Exception {
		if (getPage() <= 0 || getPerPage() <= 0) {
			boolean omitBasespaceAPI = Boolean.valueOf(CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.OMIT_BASESPACE_API, Boolean.FALSE));
			if (omitBasespaceAPI) {
				List<BaseSpaceContext> mySites = new ArrayList<>();
				setResult("site", mySites);
				return SUCCESS;
			}
		}
		JSONObject pagination = new JSONObject();
		pagination.put("page", getPage());
		pagination.put("perPage", getPerPage());
		if (getPerPage() < 0) {
			pagination.put("perPage", 5000);
		}
		if (getSearch() != null) {
			setResult("site", CommonCommandUtil.getMySites(pagination, getSearch()));
		} else {
			setResult("site", CommonCommandUtil.getMySites(pagination, null));
		}

		return SUCCESS;
	}
	
	public String getAllSites() throws Exception {
		boolean omitBasespaceAPI = Boolean.valueOf(CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.OMIT_BASESPACE_API, Boolean.FALSE));
		if (omitBasespaceAPI) {
			List<SiteContext> allSites = new ArrayList<>();
			setResult("sites", allSites);
			return SUCCESS;
		}
		setResult("sites", SpaceAPI.getAllSites());
		return SUCCESS;
	}
	
	public String getOrgs() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest(); 
		List<Organization> orgs = AccountUtil.getUserBean().getOrgs(AccountUtil.getCurrentUser().getUid());
		setResult("Orgs", orgs);
		return SUCCESS;
	}
	
	public String getRoles() throws Exception {
		List<Role> roles = AccountUtil.getRoleBean(AccountUtil.getCurrentOrg().getOrgId()).getRolesForApps(appId <= 0 ? null : Collections.singletonList(appId));
		setResult("Roles", roles);
		return SUCCESS;
	}
	
	public String getGroups() throws Exception {
		List<Group> groups = AccountUtil.getGroupBean().getOrgGroups(AccountUtil.getCurrentOrg().getId(), true);
		setResult("groups", groups);
		return SUCCESS;
	}
	
	public String getAllBuildings() throws Exception {
		boolean omitBasespaceAPI = Boolean.valueOf(CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.OMIT_BASESPACE_API, Boolean.FALSE));
		if (omitBasespaceAPI) {
			List<BuildingContext> buildings = new ArrayList<>();
			setResult("buildings", buildings);
			return SUCCESS;
		}
		setResult("buildings", SpaceAPI.getAllBuildings());
		return SUCCESS;
	}
	
	public String getAllMainEnergyMeters() throws Exception {
		setResult("energyMeters", DeviceAPI.getAllMainEnergyMeters());
		return SUCCESS;
	}
	
	private String permalink;
	
	public void setPermalink(String permalink) {
		this.permalink = permalink;
	}
	
	public String getPermalink() {
		return this.permalink;
	}
	
	public String validatePermalink() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		
		IAMAccount iamAccount = IAMUserUtil.getPermalinkAccount(getPermalink(), null);
		if(iamAccount != null) {
			
			long appId = -1;
			try {
				//handling permalinks on super admin scope
				AccountUtil.setCurrentAccount(iamAccount.getOrg().getOrgId());
				appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
			}
			catch(Exception e) {
				account =null;
				return ERROR;
			}
			Account permalinkAccount = AccountUtil.getUserBean(iamAccount.getOrg().getOrgId()).getPermalinkAccount(appId, iamAccount);
			if(permalinkAccount != null && permalinkAccount.getOrg() != null && permalinkAccount.getUser() != null) {
				
				AccountUtil.setCurrentAccount(permalinkAccount);
				account = new HashMap<>();
				account.put("org", permalinkAccount.getOrg());
				account.put("user", permalinkAccount.getUser());
				return SUCCESS;
			}
		}
		account = null;
		return ERROR;
	}
}