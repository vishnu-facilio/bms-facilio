package com.facilio.auth.actions;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.dto.*;
import com.facilio.accounts.sso.DomainSSO;
import com.facilio.bmsconsole.actions.*;
import com.facilio.bmsconsole.context.EmployeeContext;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.util.AESEncryption;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.iam.accounts.context.SecurityPolicy;
import com.facilio.iam.accounts.exceptions.SecurityPolicyException;
import com.facilio.iam.accounts.util.*;
import com.facilio.services.email.EmailClient;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.common.base.Throwables;
import lombok.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.dto.AppDomain.GroupType;
import com.facilio.accounts.sso.AccountSSO;
import com.facilio.accounts.sso.SSOUtil;
import com.facilio.accounts.sso.SamlSSOConfig;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.aws.util.FederatedIdentityUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.PortalInfoContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.auth.SAMLServiceProvider;
import com.facilio.fw.auth.SAMLServiceProvider.SAMLResponse;
import com.facilio.iam.accounts.exceptions.AccountException;
import com.facilio.modules.FieldUtil;
import com.facilio.util.RequestUtil;
import com.opensymphony.xwork2.ActionContext;

import io.sentry.SentryClient;
import io.sentry.SentryClientFactory;
import io.sentry.context.Context;
import io.sentry.event.UserBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.var;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import static com.facilio.iam.accounts.exceptions.AccountException.ErrorCode.USER_DEACTIVATED_FROM_THE_ORG;

public class FacilioAuthAction extends FacilioAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(FacilioAuthAction.class.getName());

	private String username = null;
	private String password = null;
	private String inviteToken = null;
	private Map jsonresponse = new HashMap();
	private JSONObject signupData;
	private String response = null;
	private String emailaddress;
	private HashMap<String, Object> account;
	private String phone;
	private String companyname;
	private String domainname;
	private String timezone;
	private String newPassword;
	private String title;
	private static MessageDigest md;
	
	@Getter @Setter
	private String language;

	// Sentry instance for logging posts in issue tracking group
	private static SentryClient sentry = SentryClientFactory.sentryClient("https://2fcce956a9b14116ab7bd8ca7db8591d@hentry.facilio.in/4");

	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	JSONArray entry;

	public JSONArray getEntry() {
		return entry;
	}

	public void setEntry(JSONArray entry) {
		this.entry = entry;
	}

	public String getUsername() {
		if (username != null) {
			return username;
		} else {
			return getEmailaddress();
		}
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Getter
	@Setter
	private String rawPassword;

	public String getPassword() {
		return password;
	}
	@Getter
	@Setter
	private String confirmPass;

	@Getter
	@Setter
	private long applicationId;

	public void setPassword(String password) {
		this.password = PasswordHashUtil.cryptWithMD5(password);
	}

	private String appDomain;
	
	public String getAppDomain() {
		if(org.apache.commons.lang3.StringUtils.isNotEmpty(appDomain)) {
			return appDomain;
		}
		return AccountUtil.getDefaultAppDomain();
	}

	public void setAppDomain(String appDomain) {
		this.appDomain = appDomain;
	}

	public String getInviteToken() {
		return inviteToken;
	}

	public void setInviteToken(String inviteToken) {
		this.inviteToken = inviteToken;
	}

	public Map<String, Object> getJsonresponse() {
		return jsonresponse;
	}

	public void setJsonresponse(Map<String, Object> jsonresponse) {
		this.jsonresponse = jsonresponse;
	}

	public JSONObject getSignupData() {
		return signupData;
	}

	public void setSignupData(JSONObject signupData) {
		this.signupData = signupData;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getEmailaddress() {
		if (emailaddress != null) {
			return emailaddress;
		} else {
			if (username == null) {
				return null;
			}
			return getUsername();
		}
	}

	public void setEmailaddress(String emailaddress) {
		this.emailaddress = emailaddress;
	}

	public HashMap<String, Object> getAccount() {
		return account;
	}

	public void setAccount(HashMap<String, Object> account) {
		this.account = account;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCompanyname() {
		return companyname;
	}

	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}

	public String getDomainname() {
		return domainname;
	}

	public void setDomainname(String domainname) {
		this.domainname = domainname;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = PasswordHashUtil.cryptWithMD5(newPassword);
	}

	@Override
	public void setSkipModuleCriteria ( boolean skipModuleCriteria ) {
		super.setSkipModuleCriteria(skipModuleCriteria);
	}

	public static MessageDigest getMd() {
		return md;
	}

	public static void setMd(MessageDigest md) {
		FacilioAuthAction.md = md;
	}

	private void setJsonresponse(String key, Object value) {
		this.jsonresponse.put(key, value);
	}

	private String getTitle() {
		return title;
	}

	private void setTitle(String title) {
		this.title = title;
	}
	
	
	private boolean emailVerificationNeeded;
	
	public boolean isEmailVerificationNeeded() {
		return emailVerificationNeeded;
	}

	public void setEmailVerificationNeeded(boolean emailVerificationNeeded) {
		this.emailVerificationNeeded = emailVerificationNeeded;
	}
	
	private String appDomainName;
	
	public String getAppDomainName() {
		return appDomainName;
	}

	public void setAppDomainName(String appDomainName) {
		this.appDomainName = appDomainName;
	}

	private String dbName;
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	private String dataSource;
	public String getDataSource() {
		return dataSource;
	}
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String signupUser() throws Exception {

		LOGGER.info("signupUser() : username :" + getUsername() + ", password :" + getPassword() + ", email : "
				+ getEmailaddress());
		HttpServletRequest request = ServletActionContext.getRequest();
		LOGGER.info("### addFacilioUser() :" + emailaddress);

		JSONObject signupInfo = new JSONObject();
		signupInfo.put("name", getUsername());
		signupInfo.put("email", emailaddress);
		signupInfo.put("cognitoId", "facilio");
		signupInfo.put("phone", getPhone());
		signupInfo.put("companyname", getCompanyname());
		signupInfo.put("domainname", getDomainname());
		signupInfo.put("isFacilioAuth", true);
		signupInfo.put("timezone", getTimezone());
		signupInfo.put("language", getLanguage());
		signupInfo.put("servername", request.getServerName());
		signupInfo.put("password", password);
		signupInfo.put("dataSource", dataSource);
		signupInfo.put("dbName", dbName);
		FacilioContext signupContext = new FacilioContext();
		signupContext.put(FacilioConstants.ContextNames.SIGNUP_INFO, signupInfo);
		Locale locale = request.getLocale();
		IAMAccount iamAccount = null;
		try {
			iamAccount = IAMOrgUtil.signUpOrg(signupInfo, locale);
			Account account = new Account(iamAccount.getOrg(), new User(iamAccount.getUser()));
			
			AccountUtil.setCurrentAccount(account);
		
			if (account != null && account.getOrg().getOrgId() > 0) {
				signupContext.put("orgId", account.getOrg().getOrgId());
				FacilioChain c = TransactionChainFactory.getOrgSignupChain();
				c.execute(signupContext);
			}
		}
		catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception while signing up, ", e);
			Exception ex = e;
			while (ex != null) {
				if (ex instanceof AccountException) {
					setJsonresponse("message", ex.getMessage());
					break;
				}
				ex = (Exception) ex.getCause();
			}
			if(iamAccount != null && iamAccount.getOrg() != null && iamAccount.getOrg().getOrgId() > 0) {
				IAMOrgUtil.rollBackSignedUpOrg(iamAccount.getOrg().getOrgId(), iamAccount.getUser().getUid());
			}
			setJsonresponse("errorcode", "1");
			return ERROR;
		}
		setJsonresponse("message", "success");
		return SUCCESS;
	}

	private String lookUpType;

	public void setLookUpType(String lookUpType) {
		this.lookUpType = lookUpType;
	}

	public String getLookUpType() {
		return this.lookUpType;
	}

	public String assertDomainWithDigest() {
		try {
			AppDomain.GroupType groupType;
			if (getLookUpType().equalsIgnoreCase("service") || getLookUpType().equalsIgnoreCase("tenant")) {
				groupType = AppDomain.GroupType.TENANT_OCCUPANT_PORTAL;
			} else {
				groupType = AppDomain.GroupType.FACILIO;
			}
			String domain = IAMUserUtil.validateDigestAndDomain(getDomain(), getDigest(), groupType);
			if (org.apache.commons.lang3.StringUtils.isEmpty(domain)) {
				setJsonresponse("code", 2);
				setResponseCode(2);
			} else {
				setJsonresponse("code", 1);
			}
		} catch (Exception e) {
			setJsonresponse("code", 3);
			setResponseCode(3);
			return ERROR;
		}
		return SUCCESS;
	}

	public String vendorlookup() {
		String username = getUsername();
		AppDomain.GroupType groupType = AppDomain.GroupType.VENDOR_PORTAL;
		try {
			Map<String, Object> loginModes = IAMUserUtil.getLoginModes(username, AppDomainType.VENDOR_PORTAL, groupType);
			setJsonresponse(loginModes);
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception while user lookup ", e);
			setJsonresponse("errorcode", "2");
			return ERROR;
		}
		return SUCCESS;
	}

	public String servicelookup() {
		String username = getUsername();
		AppDomain.GroupType groupType = AppDomain.GroupType.TENANT_OCCUPANT_PORTAL;
		try {
			Map<String, Object> loginModes = IAMUserUtil.getLoginModes(username, AppDomainType.SERVICE_PORTAL, groupType);
			setJsonresponse(loginModes);
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception while user lookup ", e);
			setJsonresponse("errorcode", "2");
			return ERROR;
		}
		return SUCCESS;
	}

	public String employeelookup() {
		String username = getUsername();
		AppDomain.GroupType groupType = AppDomain.GroupType.EMPLOYEE_PORTAL;
		try {
			Map<String, Object> loginModes = IAMUserUtil.getLoginModes(username, AppDomainType.EMPLOYEE_PORTAL, groupType);
			setJsonresponse(loginModes);
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception while user lookup ", e);
			setJsonresponse("errorcode", "2");
			return ERROR;
		}
		return SUCCESS;
	}

	public String clientlookup() {
		String username = getUsername();
		AppDomain.GroupType groupType = AppDomain.GroupType.CLIENT_PORTAL;
		try {
			Map<String, Object> loginModes = IAMUserUtil.getLoginModes(username, AppDomainType.CLIENT_PORTAL, groupType);
			setJsonresponse(loginModes);
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception while user lookup ", e);
			setJsonresponse("errorcode", "2");
			return ERROR;
		}
		return SUCCESS;
	}
	@SneakyThrows
	public String lookup() {
		try {
			
			GroupType groupType = null;
			if (!StringUtils.isEmpty(getLookUpType())) {
				if (getLookUpType().equals("service") || getLookUpType().equals("tenant")) {
					groupType = GroupType.TENANT_OCCUPANT_PORTAL;
				} else if (getLookUpType().equals("vendor")) {
					groupType = GroupType.VENDOR_PORTAL;
				} else if (getLookUpType().equals("client")) {
					groupType = GroupType.CLIENT_PORTAL;
				}
			}
			
			String baseUrl = checkDcAndGetRedirectUrl(groupType);
			if (baseUrl != null) {
				Map<String, Object> result = new HashMap<>();
				result.put("code", 2);
				result.put("baseUrl", baseUrl);
				setJsonresponse(result);
				return SUCCESS;
			}
			
			if (groupType != null) {
				if (groupType == GroupType.TENANT_OCCUPANT_PORTAL) {
					return servicelookup();
				} else if (groupType == GroupType.VENDOR_PORTAL) {
					return vendorlookup();
				} else if (groupType == GroupType.EMPLOYEE_PORTAL) {
					return employeelookup();
				} else if (groupType == GroupType.CLIENT_PORTAL) {
					return clientlookup();
				}
			}
	
			String username = getUsername();
			String domain = getDomain();
			HttpServletRequest request = ServletActionContext.getRequest();
		
			AppDomain appdomainObj = IAMAppUtil.getAppDomain(request.getServerName());
			Map<String, Object> loginModes = IAMUserUtil.getLoginModes(username, domain, appdomainObj);
			setJsonresponse(loginModes);
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception while user lookup ", e);
			setResponseCode(2);
			setJsonresponse("errorcode", "2");
			return ERROR;
		}
		return SUCCESS;
	}

	@Getter
	@Setter
	private String proxiedUserName;

	@Getter
	@Setter
	private String credential;

	@Getter
	@Setter
	private String g_csrf_token;

	public String authorizeproxyuser() throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();

		String g_csrf_tokenCookie = FacilioCookie.getUserCookie(request, "g_csrf_token");

		if (!g_csrf_token.equals(g_csrf_tokenCookie)) {
			setJsonresponse("errorcode", "1");
			setJsonresponse("message", "Not Permitted");
			return ERROR;
		}

		String clientPath = "/auth/proxyuser";
		String isPortalProxy = FacilioCookie.getUserCookie(request, "fc.portalproxy");
		String isMobilePortalProxy = FacilioCookie.getUserCookie(request, "fc.mobileportalproxy");
		String isMobileproxy = FacilioCookie.getUserCookie(request,"fc.ismobileproxy");
		if ("true".equals(isPortalProxy)) {
			clientPath = "/auth/portalproxyuser";
		} else if ("true".equals(isMobilePortalProxy)) {
			clientPath = "/auth/mobile/portalproxyuser";
		} else if ("true".equals(isMobileproxy)){
			clientPath = "/auth/mobile/proxyuser";
		}

		if (FacilioProperties.isOnpremise()) {
			response.sendRedirect(SSOUtil.getCurrentAppURL() + clientPath +"?message="+"Not allowed");
		}
		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
				// Specify the CLIENT_ID of the app that accesses the backend:
				.setAudience(Collections.singletonList("749943625822-unl69f9ufcga8tkitsoairfbmncam341.apps.googleusercontent.com"))
				// Or, if multiple clients access the backend:
				//.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
				.build();

		GoogleIdToken idToken = verifier.verify(credential);
		if (idToken != null) {
			Payload payload = idToken.getPayload();

			// Print user identifier
			String email = payload.getEmail();
			boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
			if (!emailVerified) {
				response.sendRedirect(SSOUtil.getCurrentAppURL() + clientPath + "?message="+"Email is not verified");
				return ERROR;
			}

			boolean inProxyList = IAMUserUtil.isUserInProxyList(email);
			if (!inProxyList) {
				response.sendRedirect(SSOUtil.getCurrentAppURL() + clientPath + "?message=You don't have access to proceed. Raise a request to proxy-users@facilio.com.");
				return ERROR;
			}

			String token = IAMUserUtil.generateProxyUserSessionToken(email);

			response.sendRedirect(SSOUtil.getCurrentAppURL() + clientPath + "?token="+token);
			return SUCCESS;
		} else {
			setJsonresponse("errorcode", "1");
			setJsonresponse("message", "Invalid token");
			return ERROR;
		}
	}

	@Getter
	@Setter
	private String portalUrl;

	public String proxyUser() throws Exception {
		String token = getToken();
		String email = IAMUserUtil.decodeProxyUserToken(token);

		if (StringUtils.isEmpty(email)) {
			setJsonresponse("errorcode", "1");
			setJsonresponse("message", "Invalid Token");
			return ERROR;
		}

		if (!IAMUserUtil.isUserInProxyList(email)) {
			setJsonresponse("errorcode", "2");
			setJsonresponse("message", "You don't have access to proceed. Raise a request to proxy-users@facilio.com.");
			return ERROR;
		}

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();

		String servername = request.getServerName();
		boolean isPortalProxy = "true".equals(FacilioCookie.getUserCookie(request, "fc.portalproxy"));
		boolean isMobileProxy = "true".equals(FacilioCookie.getUserCookie(request,"fc.isWebView"));
		boolean isMobilePortalProxy = "true".equals(FacilioCookie.getUserCookie(request,"fc.mobileportalproxy"));


		if (isMobileProxy) {
			String appDomain = request.getServerName();
			AppDomain appdomainObj = IAMAppUtil.getAppDomain(appDomain);
			setLookupTypeForMobileProxy(appdomainObj);
			if (StringUtils.isNotEmpty(getLookUpType())) {
					setPortalWebViewCookies(getLookUpType());
			} else {
					setWebViewCookies();
				}
		}
		if (isMobilePortalProxy) {
			AppDomain appDomain = IAMAppUtil.getAppDomain(portalUrl);
			String clientPath = "/auth/mobile/portalproxyuser";
			if (appDomain == null) {
				response.sendRedirect(SSOUtil.getCurrentAppURL() + clientPath + "?message="+"Invalid portal url");
				return ERROR;
			}
			clientPath = "/auth/mobile/proxyuser";
			setLookupTypeForMobileProxy(appDomain);
			setServicePortalWebViewCookies();
			setJsonresponse("redirectUrl", SSOUtil.getProtocol() + "://" + portalUrl + clientPath + "?token="+token);

		}
		 else if (!isPortalProxy) {
			String userAgent = request.getHeader("User-Agent");
			userAgent = userAgent != null ? userAgent : "";
			String ipAddress = request.getHeader("X-Forwarded-For");
			ipAddress = (ipAddress == null || "".equals(ipAddress.trim())) ? request.getRemoteAddr() : ipAddress;
			String userType = "web";
			if (isMobileProxy) {
				userType = "mobile";
			}

			FacilioCookie.eraseUserCookie(request, response,"fc.idToken.facilio","facilio.com");
			FacilioCookie.eraseUserCookie(request, response,"fc.idToken.facilio","facilio.in");
			FacilioCookie.eraseUserCookie(request, response,"fc.idToken.proxy","facilio.in");
			FacilioCookie.eraseUserCookie(request, response,"fc.idToken.proxy","facilio.com");

			Map<String, Object> proxyProps = IAMUserUtil.generatePropsForWithoutPassword(proxiedUserName, userAgent, userType, ipAddress, servername, true);

			String proxyToken = IAMUserUtil.addProxySession(email, proxiedUserName, (long) proxyProps.get("sessionId"));
			String appDomain = request.getServerName();
			AppDomain appdomainObj = IAMAppUtil.getAppDomain(appDomain);
			List<Map<String, Object>> userData = IAMUserUtil.getUserData(proxiedUserName, appdomainObj.getGroupTypeEnum());
			Map<String, Object> userMap = userData.get(0);
			Organization defaultOrg = IAMUserUtil.getDefaultOrg((long) userMap.get("uid"));
			setDomain(defaultOrg.getDomain());
			addAuthCookies((String) proxyProps.get("token"), false, false, request, isMobileProxy, proxyToken);
		} else {
			AppDomain appDomain = IAMAppUtil.getAppDomain(portalUrl);
			String clientPath = "/auth/portalproxyuser";
			if (appDomain == null) {
				response.sendRedirect(SSOUtil.getCurrentAppURL() + clientPath + "?message="+"Invalid portal url");
				return ERROR;
			}
			clientPath = "/auth/proxyuser";

			setJsonresponse("redirectUrl", SSOUtil.getProtocol() + "://" + portalUrl + clientPath + "?token="+token);
		}

		return SUCCESS;
	}

	public String loginWithUserNameAndPassword() throws Exception {
		if (StringUtils.isNotEmpty(lookUpType)) {
			validateLoginPortal();
			setServicePortalWebViewCookies();
		} else {
			validateLoginv3();
			HttpServletRequest request = ServletActionContext.getRequest();
			String isWebView = FacilioCookie.getUserCookie(request, "fc.isWebView");
			if ("true".equalsIgnoreCase(isWebView)) {
				setWebViewCookies();
			}
		}
		return SUCCESS;
	}

	private String digest;

	public String vendorLoginWithPasswordAndDigest() throws Exception {
		String digest = getDigest();
		String emailFromDigest = null;
		try {
			emailFromDigest = IAMUserUtil.getEmailFromDigest(digest);
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception while validating password, ", e);
			Exception ex = e;
			while (ex != null) {
				if (ex instanceof AccountException) {
					setJsonresponse("message", ex.getMessage());
					break;
				}
				ex = (Exception) ex.getCause();
			}
			setJsonresponse("errorcode", "2");
			return ERROR;
		}

		if (StringUtils.isEmpty(emailFromDigest) || StringUtils.isEmpty(getPassword())) {
			setJsonresponse("message", "Invalid username or password");
			return ERROR;
		}

		try {
			LOGGER.info("validateLogin() : username : " + getUsername()
			);
			String authtoken = null;
			AppDomain appdomainObj = null;
			Organization org = null;
			if (StringUtils.isNotEmpty(getDomain())) {
				org = IAMOrgUtil.getOrg(getDomain());
			}
			if (getLookUpType().equalsIgnoreCase("service")) {
				if (org == null) {
					List<Map<String, Object>> userData = IAMUserUtil.getUserData(emailFromDigest, AppDomain.GroupType.TENANT_OCCUPANT_PORTAL);
					List<Long> orgIds = new ArrayList<>();
					userData.forEach(i -> orgIds.add((long) i.get("orgId")));
					org = IAMOrgUtil.getOrg(orgIds.get(0));
					setDomain(org.getDomain());
				}
				appdomainObj = IAMAppUtil.getAppDomainForType(2, org.getOrgId()).get(0);
			} else if (getLookUpType().equalsIgnoreCase("tenant")) {
				if (org == null) {
					List<Map<String, Object>> userData = IAMUserUtil.getUserData(emailFromDigest, AppDomain.GroupType.TENANT_OCCUPANT_PORTAL);
					List<Long> orgIds = new ArrayList<>();
					userData.forEach(i -> orgIds.add((long) i.get("orgId")));
					org = IAMOrgUtil.getOrg(orgIds.get(0));
					setDomain(org.getDomain());
				}
				appdomainObj = IAMAppUtil.getAppDomainForType(3, org.getOrgId()).get(0);
			} else if (getLookUpType().equalsIgnoreCase("vendor")) {
				if (org == null) {
					List<Map<String, Object>> userData = IAMUserUtil.getUserData(emailFromDigest, AppDomain.GroupType.VENDOR_PORTAL);
					List<Long> orgIds = new ArrayList<>();
					userData.forEach(i -> orgIds.add((long) i.get("orgId")));
					org = IAMOrgUtil.getOrg(orgIds.get(0));
					setDomain(org.getDomain());
				}
				appdomainObj = IAMAppUtil.getAppDomainForType(4, org.getOrgId()).get(0);
			} else if (getLookUpType().equalsIgnoreCase(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP)) {
				if (org == null) {
					List<Map<String, Object>> userData = IAMUserUtil.getUserData(emailFromDigest, AppDomain.GroupType.EMPLOYEE_PORTAL);
					List<Long> orgIds = new ArrayList<>();
					userData.forEach(i -> orgIds.add((long) i.get("orgId")));
					org = IAMOrgUtil.getOrg(orgIds.get(0));
					setDomain(org.getDomain());
				}
				appdomainObj = IAMAppUtil.getAppDomainForType(7, org.getOrgId()).get(0);
			}

			HttpServletRequest request = ServletActionContext.getRequest();
			HttpServletResponse resp = ServletActionContext.getResponse();
			String userAgent = request.getHeader("User-Agent");
			userAgent = userAgent != null ? userAgent : "";
			String ipAddress = request.getHeader("X-Forwarded-For");
			ipAddress = (ipAddress == null || "".equals(ipAddress.trim())) ? request.getRemoteAddr() : ipAddress;
			String userType = "mobile";

			authtoken = IAMUserUtil.verifyLoginPasswordv3(emailFromDigest, getPassword(), appdomainObj.getDomain(), userAgent, userType, ipAddress);
			setJsonresponse("token", authtoken);
			setJsonresponse("username", emailFromDigest);

			//deleting .facilio.com cookie(temp handling)
			FacilioCookie.eraseUserCookie(request, resp,"fc.idToken.facilio","facilio.com");
			FacilioCookie.eraseUserCookie(request, resp,"fc.idToken.facilio","facilio.in");
			FacilioCookie.eraseUserCookie(request, resp,"fc.idToken.proxy","facilio.in");
			FacilioCookie.eraseUserCookie(request, resp,"fc.idToken.proxy","facilio.com");

			addAuthCookies(authtoken, false, false, request, true);
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception while validating password, ", e);
			Exception ex = e;
			while (ex != null) {
				if (ex instanceof AccountException) {
					setJsonresponse("message", ex.getMessage());
					break;
				}
				ex = (Exception) ex.getCause();
			}
			setJsonresponse("errorcode", "1");
			return ERROR;
		}
		setServicePortalWebViewCookies();
		return SUCCESS;
	}

	public String serviceLoginWithPasswordAndDigest() throws Exception {
		String digest = getDigest();
		String emailFromDigest = null;
		try {
			emailFromDigest = IAMUserUtil.getEmailFromDigest(digest);
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception while validating password, ", e);
			Exception ex = e;
			while (ex != null) {
				if (ex instanceof AccountException) {
					setJsonresponse("message", ex.getMessage());
					break;
				}
				ex = (Exception) ex.getCause();
			}
			setJsonresponse("errorcode", "2");
			setResponseCode(2);
			return ERROR;
		}

		if (StringUtils.isEmpty(emailFromDigest) || StringUtils.isEmpty(getPassword())) {
			setJsonresponse("message", "Invalid username or password");
			return ERROR;
		}

		try {
			LOGGER.info("validateLogin() : username : " + getUsername()
			);
			String authtoken = null;
			AppDomain appdomainObj = null;
			Organization org = null;
			if (StringUtils.isNotEmpty(getDomain())) {
				org = IAMOrgUtil.getOrg(getDomain());
			}
			if (getLookUpType().equalsIgnoreCase("service")) {
				if (org == null) {
					List<Map<String, Object>> userData = IAMUserUtil.getUserData(emailFromDigest, AppDomain.GroupType.TENANT_OCCUPANT_PORTAL);
					List<Long> orgIds = new ArrayList<>();
					userData.forEach(i -> orgIds.add((long) i.get("orgId")));
					org = IAMOrgUtil.getOrg(orgIds.get(0));
					setDomain(org.getDomain());
				}
				appdomainObj = IAMAppUtil.getAppDomainForType(2, org.getOrgId()).get(0);
			} else if (getLookUpType().equalsIgnoreCase("tenant")) {
				if (org == null) {
					List<Map<String, Object>> userData = IAMUserUtil.getUserData(emailFromDigest, AppDomain.GroupType.TENANT_OCCUPANT_PORTAL);
					List<Long> orgIds = new ArrayList<>();
					userData.forEach(i -> orgIds.add((long) i.get("orgId")));
					org = IAMOrgUtil.getOrg(orgIds.get(0));
					setDomain(org.getDomain());
				}
				appdomainObj = IAMAppUtil.getAppDomainForType(3, org.getOrgId()).get(0);
			} else if (getLookUpType().equalsIgnoreCase("vendor")) {
				if (org == null) {
					List<Map<String, Object>> userData = IAMUserUtil.getUserData(emailFromDigest, AppDomain.GroupType.VENDOR_PORTAL);
					List<Long> orgIds = new ArrayList<>();
					userData.forEach(i -> orgIds.add((long) i.get("orgId")));
					org = IAMOrgUtil.getOrg(orgIds.get(0));
					setDomain(org.getDomain());
				}
				appdomainObj = IAMAppUtil.getAppDomainForType(4, org.getOrgId()).get(0);
			} else if (getLookUpType().equalsIgnoreCase(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP)) {
				if (org == null) {
					List<Map<String, Object>> userData = IAMUserUtil.getUserData(emailFromDigest, AppDomain.GroupType.EMPLOYEE_PORTAL);
					List<Long> orgIds = new ArrayList<>();
					userData.forEach(i -> orgIds.add((long) i.get("orgId")));
					org = IAMOrgUtil.getOrg(orgIds.get(0));
					setDomain(org.getDomain());
				}
				appdomainObj = IAMAppUtil.getAppDomainForType(7, org.getOrgId()).get(0);
			} else if (getLookUpType().equalsIgnoreCase("client")) {
				if (org == null) {
					List<Map<String, Object>> userData = IAMUserUtil.getUserData(emailFromDigest, GroupType.CLIENT_PORTAL);
					List<Long> orgIds = new ArrayList<>();
					userData.forEach(i -> orgIds.add((long) i.get("orgId")));
					org = IAMOrgUtil.getOrg(orgIds.get(0));
					setDomain(org.getDomain());
				}
				appdomainObj = IAMAppUtil.getAppDomainForType(5, org.getOrgId()).get(0);
			}

			HttpServletRequest request = ServletActionContext.getRequest();
			HttpServletResponse resp = ServletActionContext.getResponse();
			String userAgent = request.getHeader("User-Agent");
			userAgent = userAgent != null ? userAgent : "";
			String ipAddress = request.getHeader("X-Forwarded-For");
			ipAddress = (ipAddress == null || "".equals(ipAddress.trim())) ? request.getRemoteAddr() : ipAddress;
			String userType = "mobile";

			SecurityPolicy securityPolicy = null;

			List<Map<String, Object>> userData = IAMUserUtil.getUserData(emailFromDigest, appdomainObj.getGroupTypeEnum());
			Map<String, Object> userMap = userData.get(0);
			Organization defaultOrg = IAMUserUtil.getDefaultOrg((long) userMap.get("uid"));
			setDomain(defaultOrg.getDomain());
			LOGGER.log(Level.INFO,"validateLogin() : default org id : " + defaultOrg.getOrgId());
			securityPolicy = IAMUserUtil.getUserSecurityPolicy(emailFromDigest, appdomainObj.getGroupTypeEnum());
			LOGGER.log(Level.INFO,"validateLogin() : security policy is null : " + (securityPolicy == null));
			LOGGER.log(Level.INFO,"validateLogin() : security policy id : " + (securityPolicy != null ? securityPolicy.getId(): -1L));

			boolean hasMfaSettings;
			Boolean totpEnabled = securityPolicy == null ? null : securityPolicy.getIsTOTPEnabled();
			hasMfaSettings = totpEnabled != null && totpEnabled;

			if (!hasMfaSettings) {
				authtoken = IAMUserUtil.verifyLoginPasswordv3(emailFromDigest, getPassword(), appdomainObj.getDomain(), userAgent, userType, ipAddress);

				var resetRequired = false;
				if (securityPolicy != null && securityPolicy.getIsPwdPolicyEnabled() != null && securityPolicy.getIsPwdPolicyEnabled() && securityPolicy.getPwdMinAge() != null) {
					resetRequired = handlePasswordPolicy(securityPolicy, emailFromDigest, appdomainObj.getGroupTypeEnum());
				}

				if (!resetRequired && StringUtils.isNotEmpty(authtoken)) {
					setJsonresponse("token", authtoken);
					setJsonresponse("username", emailFromDigest);

					//deleting .facilio.com cookie(temp handling)
					FacilioCookie.eraseUserCookie(request, resp,"fc.idToken.facilio","facilio.com");
					FacilioCookie.eraseUserCookie(request, resp,"fc.idToken.facilio","facilio.in");
					FacilioCookie.eraseUserCookie(request, resp,"fc.idToken.proxy","facilio.in");
					FacilioCookie.eraseUserCookie(request, resp,"fc.idToken.proxy","facilio.com");

					addAuthCookies(authtoken, false, false, request, true);
				}
			} else {
				IAMUserUtil.validateLoginv3(emailFromDigest, getPassword(), appdomainObj.getDomain(), userAgent, userType, ipAddress, false);
				Map<String, Object> userMfaSettings = IAMUserUtil.getUserMfaSettings(emailFromDigest, appdomainObj.getGroupTypeEnum());
				Boolean totpStatus = (Boolean) userMfaSettings.get("totpStatus");
				boolean isTotpEnabled = totpStatus != null && totpStatus;
				if (!isTotpEnabled) {
					setJsonresponse("isOrgTotpEnabled", true);
					setJsonresponse("isMFASetupRequired", true);
					setJsonresponse("mfaConfigToken", IAMUserUtil.generateMFAConfigSessionToken(emailFromDigest, emailFromDigest+"_" +appdomainObj.getGroupType()));
					setJsonresponse("username", emailFromDigest);
				} else {
					String jwt = IAMUserUtil.generateTotpSessionToken(emailFromDigest, emailFromDigest+"_" +appdomainObj.getGroupType());
					setJsonresponse("isOrgTotpEnabled", true);
					setJsonresponse("isMFASetupRequired", false);
					setJsonresponse("totpToken", jwt);
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception while validating password, ", e);
			Exception ex = e;
			while (ex != null) {
				if (ex instanceof AccountException) {
					setJsonresponse("message", ex.getMessage());
					break;
				}
				ex = (Exception) ex.getCause();
			}
			setJsonresponse("errorcode", "1");
			return ERROR;
		}
		setServicePortalWebViewCookies();
		return SUCCESS;
	}

	public String loginWithPasswordAndDigest() throws Exception {
		if (!StringUtils.isEmpty(getLookUpType()) && !getLookUpType().equalsIgnoreCase("workq")) {
//			if (getLookUpType().equals("service") || getLookUpType().equalsIgnoreCase("tenant")) {
			return serviceLoginWithPasswordAndDigest();
//			} else if (getLookUpType().equals("vendor")) {
//				return vendorLoginWithPasswordAndDigest();
//			}
		}
		String digest = getDigest();
		String emailFromDigest = null;
		try {
			emailFromDigest = IAMUserUtil.getEmailFromDigest(digest);
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception while validating password, ", e);
			Exception ex = e;
			while (ex != null) {
				if (ex instanceof AccountException) {
					setJsonresponse("message", ex.getMessage());
					break;
				}
				ex = (Exception) ex.getCause();
			}
			setJsonresponse("errorcode", "2");
			setResponseCode(2);
			return ERROR;
		}
		setUsername(emailFromDigest);
		validateLoginv3();

		HttpServletRequest request = ServletActionContext.getRequest();
		String isWebView = FacilioCookie.getUserCookie(request, "fc.isWebView");
		if ("true".equalsIgnoreCase(isWebView)) {
			setWebViewCookies();
		}
		return SUCCESS;
	}

	private void setServicePortalWebViewCookies() throws Exception {
		setPortalWebViewCookies(getLookUpType());
	}

	private void setPortalWebViewCookies(String mobileAppType) throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();
		Cookie schemeCookie = null;
		if (mobileAppType.equalsIgnoreCase("service")) {
			schemeCookie = new Cookie("fc.mobile.scheme", FacilioProperties.getMobileServiceportalAppScheme());
		} else if (mobileAppType.equalsIgnoreCase("tenant")) {
			schemeCookie = new Cookie("fc.mobile.scheme", FacilioProperties.getMobileTenantportalAppScheme());
		} else if (mobileAppType.equalsIgnoreCase("vendor")) {
			schemeCookie = new Cookie("fc.mobile.scheme", FacilioProperties.getMobileVendorportalAppScheme());
		} else if (mobileAppType.equalsIgnoreCase("facilio")) {
			schemeCookie = new Cookie("fc.mobile.scheme", FacilioProperties.getMobileMainAppScheme());
		} else if (mobileAppType.equalsIgnoreCase("workQ")) {
			schemeCookie = new Cookie("fc.mobile.scheme", FacilioProperties.getWorkQAppScheme());
		} else if (mobileAppType.equalsIgnoreCase("client")) {
			schemeCookie = new Cookie("fc.mobile.scheme", FacilioProperties.getMobileClientportalAppScheme());
		}

		setTempCookieProperties(schemeCookie, false);
		response.addCookie(schemeCookie);
	}

	public void setWebViewCookies() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		AppDomain appDomainObj = IAMAppUtil.getAppDomain(request.getServerName());
		setWebViewCookies(appDomainObj);
	}

	private void setWebViewCookies(AppDomain appDomainObj) throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();
		String scheme = "";
		if ("workQ".equalsIgnoreCase(getLookUpType())) {
			Cookie schemeCookie = new Cookie("fc.mobile.scheme", FacilioProperties.getWorkQAppScheme());
			setTempCookieProperties(schemeCookie, false);
			response.addCookie(schemeCookie);
		} else if (appDomainObj.getAppDomainTypeEnum() == AppDomainType.FACILIO) {
			String mainAppscheme = FacilioProperties.getMobileMainAppScheme();
			if (appDomainObj.getDomainType() == AppDomain.DomainType.DEFAULT.getIndex()) {
				scheme = mainAppscheme;
			} else {
				Organization org = IAMOrgUtil.getOrg(appDomainObj.getOrgId());
				scheme = org.getDomain()+"-"+mainAppscheme;
			}
			Cookie schemeCookie = new Cookie("fc.mobile.scheme", scheme);
			setTempCookieProperties(schemeCookie, false);
			response.addCookie(schemeCookie);
		}
	}

	public  String validateLoginPortal() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse resp = ServletActionContext.getResponse();

		if (getUsername() == null || getPassword() == null || getDomain() == null) {
			return ERROR;
		}

		Organization org = IAMOrgUtil.getOrg(getDomain());
		AppDomain appdomainObj = null;
		if ("service".equalsIgnoreCase(getLookUpType())) {
			appdomainObj = IAMAppUtil.getAppDomainForType(2, org.getOrgId()).get(0);
		} else if ("tenant".equalsIgnoreCase(getLookUpType())) {
			appdomainObj = IAMAppUtil.getAppDomainForType(3, org.getOrgId()).get(0);
		} else if ("vendor".equalsIgnoreCase(getLookUpType())) {
			appdomainObj = IAMAppUtil.getAppDomainForType(4, org.getOrgId()).get(0);
		} else if (FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP.equalsIgnoreCase(getLookUpType())) {
			appdomainObj = IAMAppUtil.getAppDomainForType(7, org.getOrgId()).get(0);
		} else if ("client".equalsIgnoreCase(getLookUpType())) {
			appdomainObj = IAMAppUtil.getAppDomainForType(5, org.getOrgId()).get(0);
		}


		try {
			LOGGER.info("validateLogin() : username : " + getUsername()
			);
			String authtoken = null;
			String userAgent = request.getHeader("User-Agent");
			userAgent = userAgent != null ? userAgent : "";
			String ipAddress = request.getHeader("X-Forwarded-For");
			ipAddress = (ipAddress == null || "".equals(ipAddress.trim())) ? request.getRemoteAddr() : ipAddress;
			String userType = "mobile";
			authtoken = IAMUserUtil.verifyLoginPasswordv3(getUsername(), getPassword(), appdomainObj.getDomain(), userAgent, userType, ipAddress);
			setJsonresponse("token", authtoken);
			setJsonresponse("username", getUsername());

			//deleting .facilio.com cookie(temp handling)
			FacilioCookie.eraseUserCookie(request, resp,"fc.idToken.facilio","facilio.com");
			FacilioCookie.eraseUserCookie(request, resp,"fc.idToken.facilio","facilio.in");
			FacilioCookie.eraseUserCookie(request, resp,"fc.idToken.proxy","facilio.in");
			FacilioCookie.eraseUserCookie(request, resp,"fc.idToken.proxy","facilio.com");

			addAuthCookies(authtoken, false, false, request, "mobile".equals(userType));
		}
		catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception while validating password, ", e);
			Exception ex = e;
			while (ex != null) {
				if (ex instanceof AccountException) {
					setJsonresponse("message", ex.getMessage());
					break;
				}
				ex = (Exception) ex.getCause();
			}
			setJsonresponse("errorcode", "1");
			setResponseCode(2);
			return ERROR;
		}
		return SUCCESS;
	}

	private String totp;

	public String verifyTotp() throws Exception {
		String digest = getDigest();
		String emailFromDigest = null;
		try {
			emailFromDigest = IAMUserUtil.getEmailFromDigest(digest, IAMAccountConstants.SessionType.TOTP_SESSION);
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception while validating totp digest, ", e);
			Exception ex = e;
			while (ex != null) {
				if (ex instanceof AccountException) {
					setJsonresponse("message", ex.getMessage());
					break;
				}
				ex = (Exception) ex.getCause();
			}
			setResponseCode(2);
			setJsonresponse("errorcode", "2");
			return ERROR;
		}

		AppDomain.GroupType groupType = AppDomain.GroupType.FACILIO;
		if (StringUtils.isNotEmpty(getLookUpType())) {
			if (getLookUpType().equalsIgnoreCase("service") || getLookUpType().equalsIgnoreCase("tenant")) {
				groupType = AppDomain.GroupType.TENANT_OCCUPANT_PORTAL;
			} else if (getLookUpType().equalsIgnoreCase("vendor")) {
				groupType = AppDomain.GroupType.VENDOR_PORTAL;
			} else if (getLookUpType().equalsIgnoreCase(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP)) {
				groupType = AppDomain.GroupType.EMPLOYEE_PORTAL;
			} else if (getLookUpType().equalsIgnoreCase("client")) {
				groupType = AppDomain.GroupType.CLIENT_PORTAL;
			}else {
				groupType = AppDomain.GroupType.FACILIO;
			}
		} else {
			HttpServletRequest request = ServletActionContext.getRequest();
			AppDomain appdomainObj = IAMAppUtil.getAppDomain(request.getServerName());
			groupType = appdomainObj.getGroupTypeEnum();
		}

		emailFromDigest = StringUtils.removeEnd(emailFromDigest, "_"+groupType.getIndex());
		List<Map<String, Object>> userData = IAMUserUtil.getUserData(emailFromDigest, groupType);
		var userInfo = userData.get(0);

		boolean isVerified = IAMUserUtil.totpChecking(this.totp, (long) userInfo.get("uid"));
		if (!isVerified) {
			setJsonresponse("errorcode", 1);
			setJsonresponse("message", "Invalid verification code");
			LOGGER.log(Level.INFO, "Invalid username or password " + emailFromDigest);
			return SUCCESS;
		} else {
			Organization defaultOrg = IAMUserUtil.getDefaultOrg((long) userInfo.get("uid"));
			setDomain(defaultOrg.getDomain());
			var securityPolicy = IAMUserUtil.getUserSecurityPolicy(emailFromDigest, groupType);
			var resetRequired = false;
			if (securityPolicy != null && securityPolicy.getIsPwdPolicyEnabled() != null && securityPolicy.getIsPwdPolicyEnabled() && securityPolicy.getPwdMinAge() != null) {
				resetRequired = handlePasswordPolicy(securityPolicy, emailFromDigest, groupType);
			}

			HttpServletRequest request = ServletActionContext.getRequest();

			var serverName = request.getServerName();
			if (StringUtils.isNotEmpty(getLookUpType())) {
				if (getLookUpType().equalsIgnoreCase("service")) {
					List<AppDomain> appDomain = IAMAppUtil.getAppDomain(AppDomainType.SERVICE_PORTAL, defaultOrg.getOrgId());
					serverName = appDomain.stream().filter(i -> i.getDomainTypeEnum() == AppDomain.DomainType.DEFAULT).findAny().get().getDomain();
				} else if (getLookUpType().equalsIgnoreCase("tenant"))  {
					List<AppDomain> appDomain = IAMAppUtil.getAppDomain(AppDomainType.TENANT_PORTAL, defaultOrg.getOrgId());
					serverName = appDomain.stream().filter(i -> i.getDomainTypeEnum() == AppDomain.DomainType.DEFAULT).findAny().get().getDomain();
				} else if (getLookUpType().equalsIgnoreCase("vendor")) {
					List<AppDomain> appDomain = IAMAppUtil.getAppDomain(AppDomainType.VENDOR_PORTAL, defaultOrg.getOrgId());
					serverName = appDomain.stream().filter(i -> i.getDomainTypeEnum() == AppDomain.DomainType.DEFAULT).findAny().get().getDomain();
				} else if (getLookUpType().equalsIgnoreCase("workq")) {
					List<AppDomain> appDomain = IAMAppUtil.getAppDomain(AppDomainType.FACILIO, defaultOrg.getOrgId());
					serverName = appDomain.stream().filter(i -> i.getDomainTypeEnum() == AppDomain.DomainType.DEFAULT).findAny().get().getDomain();
				} else if (getLookUpType().equalsIgnoreCase(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP)) {
					List<AppDomain> appDomain = IAMAppUtil.getAppDomain(AppDomainType.EMPLOYEE_PORTAL, defaultOrg.getOrgId());
					serverName = appDomain.stream().filter(i -> i.getDomainTypeEnum() == AppDomain.DomainType.DEFAULT).findAny().get().getDomain();
				} else if (getLookUpType().equalsIgnoreCase("client")) {
					List<AppDomain> appDomain = IAMAppUtil.getAppDomain(AppDomainType.CLIENT_PORTAL, defaultOrg.getOrgId());
					serverName = appDomain.stream().filter(i -> i.getDomainTypeEnum() == AppDomain.DomainType.DEFAULT).findAny().get().getDomain();
				}
			}

			if (!resetRequired) {
				HttpServletResponse response = ServletActionContext.getResponse();
				String userAgent = request.getHeader("User-Agent");

				String userType = "web";
				String deviceType = request.getHeader("X-Device-Type");
				if (!StringUtils.isEmpty(deviceType)
						&& ("android".equalsIgnoreCase(deviceType) || "ios".equalsIgnoreCase(deviceType) || "webview".equalsIgnoreCase(deviceType))) {
					userType = "mobile";
				}

				String ipAddress = request.getHeader("X-Forwarded-For");

				String authtoken = IAMUserUtil.verifyLoginWithoutPassword(emailFromDigest, userAgent, userType, ipAddress, serverName, null);
				setJsonresponse("token", authtoken);
				setJsonresponse("username", emailFromDigest);

				//deleting .facilio.com cookie(temp handling)
				FacilioCookie.eraseUserCookie(request, response,"fc.idToken.facilio","facilio.com");
				FacilioCookie.eraseUserCookie(request, response,"fc.idToken.facilio","facilio.in");
				FacilioCookie.eraseUserCookie(request, response,"fc.idToken.proxy","facilio.in");
				FacilioCookie.eraseUserCookie(request, response,"fc.idToken.proxy","facilio.com");

				String appDomain = request.getServerName();
				AppDomain appdomainObj = IAMAppUtil.getAppDomain(appDomain);
				boolean portalUser = false;
				if(appdomainObj != null && appdomainObj.getAppDomainTypeEnum() != AppDomainType.FACILIO) {
					portalUser = true;
				}

				addAuthCookies(authtoken, portalUser, false, request, "mobile".equals(userType));

				String isWebView = FacilioCookie.getUserCookie(request, "fc.isWebView");
				if ("true".equalsIgnoreCase(isWebView)) {
					if (StringUtils.isNotEmpty(getLookUpType()) && "mobile".equals(userType)) {
						setPortalWebViewCookies(getLookUpType());
					} else {
						setWebViewCookies();
					}
				}
			}
		}
		return SUCCESS;
	}

	public String validateLoginv3() {

		boolean portalUser = false;
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse resp = ServletActionContext.getResponse();


		if (getUsername() != null && getPassword() != null) {
			try {
				LOGGER.info("validateLogin() : username : " + getUsername() 
						);
				String authtoken = null;
				String appDomain = request.getServerName();
				AppDomain appdomainObj = IAMAppUtil.getAppDomain(appDomain);
				if(appdomainObj != null && appdomainObj.getAppDomainTypeEnum() != AppDomainType.FACILIO) {
					portalUser = true;
				}
				
				String userAgent = request.getHeader("User-Agent");
				userAgent = userAgent != null ? userAgent : "";
				String ipAddress = request.getHeader("X-Forwarded-For");
				ipAddress = (ipAddress == null || "".equals(ipAddress.trim())) ? request.getRemoteAddr() : ipAddress;
                String userType = "web";
				String deviceType = request.getHeader("X-Device-Type");
				String isWebView = FacilioCookie.getUserCookie(request, "fc.isWebView");
				if (!StringUtils.isEmpty(deviceType)
						&& ("android".equalsIgnoreCase(deviceType) || "ios".equalsIgnoreCase(deviceType) || "webview".equalsIgnoreCase(deviceType))) {
					userType = "mobile";
				}

				if ("true".equalsIgnoreCase(isWebView)) {
					userType = "mobile";
				}

				SecurityPolicy securityPolicy = null;

				List<Map<String, Object>> userData = IAMUserUtil.getUserData(getUsername(), appdomainObj.getGroupTypeEnum());
				Map<String, Object> userMap = userData.get(0);
				Organization defaultOrg = IAMUserUtil.getDefaultOrg((long) userMap.get("uid"));
				setDomain(defaultOrg.getDomain());
				LOGGER.log(Level.INFO,"validateLogin() : default org id : " + defaultOrg.getOrgId());
				securityPolicy = IAMUserUtil.getUserSecurityPolicy(getUsername(), appdomainObj.getGroupTypeEnum());
				LOGGER.log(Level.INFO,"validateLogin() : security policy is null : " + (securityPolicy == null));
				LOGGER.log(Level.INFO,"validateLogin() : security policy id : " + (securityPolicy != null ? securityPolicy.getId(): -1L));

				boolean hasMfaSettings;
				Boolean totpEnabled = securityPolicy == null ? null : securityPolicy.getIsTOTPEnabled();
				hasMfaSettings = totpEnabled != null && totpEnabled;

				if (!hasMfaSettings) {
					authtoken = IAMUserUtil.verifyLoginPasswordv3(getUsername(), getPassword(), request.getServerName(), userAgent, userType,
							ipAddress);

					var resetRequired = false;
					if (securityPolicy != null && securityPolicy.getIsPwdPolicyEnabled() != null && securityPolicy.getIsPwdPolicyEnabled() && securityPolicy.getPwdMinAge() != null) {
						resetRequired = handlePasswordPolicy(securityPolicy, getUsername(), appdomainObj.getGroupTypeEnum());
					}

					if (!resetRequired && StringUtils.isNotEmpty(authtoken)) {
						setJsonresponse("token", authtoken);
						setJsonresponse("username", getUsername());

						//deleting .facilio.com cookie(temp handling)
						FacilioCookie.eraseUserCookie(request, resp,"fc.idToken.facilio","facilio.com");
						FacilioCookie.eraseUserCookie(request, resp,"fc.idToken.facilio","facilio.in");
						FacilioCookie.eraseUserCookie(request, resp,"fc.idToken.proxy","facilio.in");
						FacilioCookie.eraseUserCookie(request, resp,"fc.idToken.proxy","facilio.com");


						addAuthCookies(authtoken, portalUser, false, request, "mobile".equals(userType));
					}
				} else {
					IAMUserUtil.validateLoginv3(getUsername(), getPassword(), request.getServerName(), userAgent, userType, ipAddress, false);
					Map<String, Object> userMfaSettings = IAMUserUtil.getUserMfaSettings(getUsername(), appdomainObj.getGroupTypeEnum());
					Boolean totpStatus = (Boolean) userMfaSettings.get("totpStatus");
					boolean isTotpEnabled = totpStatus != null && totpStatus;
					if (!isTotpEnabled) {
						setJsonresponse("isOrgTotpEnabled", true);
						setJsonresponse("isMFASetupRequired", true);
						setJsonresponse("mfaConfigToken", IAMUserUtil.generateMFAConfigSessionToken(getUsername(), getUsername() + "_" + appdomainObj.getGroupType()));
						setJsonresponse("username", getUsername());
					} else {
						String jwt = IAMUserUtil.generateTotpSessionToken(getUsername(), getUsername() + "_" + appdomainObj.getGroupType());
						setJsonresponse("isOrgTotpEnabled", true);
						setJsonresponse("isMFASetupRequired", false);
						setJsonresponse("totpToken", jwt);
					}
				}
			} 
			catch (Exception e) {
				LOGGER.log(Level.INFO, "Exception while validating password, ", e);
				Exception ex = e;
				while (ex != null) {
					if (ex instanceof AccountException) {
						setJsonresponse("message", ex.getMessage());
						break;
					}
					ex = (Exception) ex.getCause();
				}
				setJsonresponse("errorcode", "1");
				setResponseCode(2);
				return ERROR;
			}
			return SUCCESS;
		}
		setJsonresponse("message", "Invalid username or password");
		LOGGER.log(Level.INFO, "Invalid username or password " + getUsername());
		return ERROR;
	}

	private boolean handlePasswordPolicy(SecurityPolicy securityPolicy, String emailaddress, GroupType groupType) throws Exception {
		Integer pwdMinAge = securityPolicy.getPwdMinAge();
		if (pwdMinAge == null) {
			return false;
		}
		List<Map<String, Object>> userData = IAMUserUtil.getUserData(emailaddress, groupType);
		Long pwdLastUpdatedTime = (Long) userData.get(0).get("pwdLastUpdatedTime");
		if (pwdLastUpdatedTime != null) {
			Instant pwdLastUpdatedInstant = Instant.ofEpochMilli(pwdLastUpdatedTime);
			Instant currentTimeInstant = Instant.ofEpochMilli(System.currentTimeMillis());
			Duration elapsedDuration = Duration.between(pwdLastUpdatedInstant, currentTimeInstant);
			var allowedDuration = Duration.ofDays(pwdMinAge);
			if (elapsedDuration.compareTo(allowedDuration) >= 0) {
				String resetToken = IAMUserUtil.generatePWDPolicyPWDResetToken(emailaddress, groupType);
				setJsonresponse("pwdPolicyResetToken", resetToken);
				return true;
			}
		}
		return false;
	}

	public String getMfaSettingsUsingDigest() throws Exception {
		String emailFromDigest = null;
		try {
			emailFromDigest = IAMUserUtil.getEmailFromDigest(getMfaConfigToken(), IAMAccountConstants.SessionType.MFA_CONFIG_SESSION);
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception while validating totp digest, ", e);
			Exception ex = e;
			while (ex != null) {
				if (ex instanceof AccountException) {
					setJsonresponse("message", ex.getMessage());
					break;
				}
				ex = (Exception) ex.getCause();
			}
			setJsonresponse("errorcode", "2");
			return ERROR;
		}

		AppDomain.GroupType groupType;
		if (StringUtils.isNotEmpty(getLookUpType())) {
			if (getLookUpType().equalsIgnoreCase("service") || getLookUpType().equalsIgnoreCase("tenant")) {
				groupType = AppDomain.GroupType.TENANT_OCCUPANT_PORTAL;
			} else if (getLookUpType().equalsIgnoreCase("vendor")) {
				groupType = AppDomain.GroupType.VENDOR_PORTAL;
			} else if (getLookUpType().equalsIgnoreCase(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP)) {
				groupType = AppDomain.GroupType.EMPLOYEE_PORTAL;
			} else if (getLookUpType().equalsIgnoreCase("client")) {
				groupType = AppDomain.GroupType.CLIENT_PORTAL;
			}else {
				groupType = AppDomain.GroupType.FACILIO;
			}
		} else {
			HttpServletRequest request = ServletActionContext.getRequest();
			AppDomain appdomainObj = IAMAppUtil.getAppDomain(request.getServerName());
			groupType = appdomainObj.getGroupTypeEnum();
		}
		emailFromDigest = StringUtils.removeEnd(emailFromDigest, "_"+groupType.getIndex());
		Map<String, Object> userMfaSettings = IAMUserUtil.getUserMfaSettings(emailFromDigest, groupType);
		SettingsMfa settingsMfa = new SettingsMfa();
		settingsMfa.generateMfaData((Long) userMfaSettings.get("userId"));

		setJsonresponse("totpSecret", settingsMfa.getTotpSecret());
		setJsonresponse("qrCode", settingsMfa.getQrCode());

		return SUCCESS;
	}

	private String mfaConfigToken;
	public String configureMFAUsingDigest() throws Exception {
		String emailFromDigest = null;
		try {
			emailFromDigest = IAMUserUtil.getEmailFromDigest(getMfaConfigToken(), IAMAccountConstants.SessionType.MFA_CONFIG_SESSION);
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception while validating totp digest, ", e);
			Exception ex = e;
			while (ex != null) {
				if (ex instanceof AccountException) {
					setJsonresponse("message", ex.getMessage());
					break;
				}
				ex = (Exception) ex.getCause();
			}
			setJsonresponse("errorcode", "2");
			return ERROR;
		}

		AppDomain.GroupType groupType;
		HttpServletRequest request = ServletActionContext.getRequest();
		String serverName = request.getServerName();
		if (StringUtils.isNotEmpty(getLookUpType())) {
			if (getLookUpType().equalsIgnoreCase("service") || getLookUpType().equalsIgnoreCase("tenant")) {
				groupType = AppDomain.GroupType.TENANT_OCCUPANT_PORTAL;
			} else if (getLookUpType().equalsIgnoreCase("vendor")) {
				groupType = AppDomain.GroupType.VENDOR_PORTAL;
			} else if (getLookUpType().equalsIgnoreCase(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP)) {
				groupType = AppDomain.GroupType.EMPLOYEE_PORTAL;
			} else if (getLookUpType().equalsIgnoreCase("client")) {
				groupType = AppDomain.GroupType.CLIENT_PORTAL;
			} else {
				groupType = AppDomain.GroupType.FACILIO;
			}
		} else {
			AppDomain appdomainObj = IAMAppUtil.getAppDomain(request.getServerName());
			groupType = appdomainObj.getGroupTypeEnum();
		}
		emailFromDigest = StringUtils.removeEnd(emailFromDigest, "_"+groupType.getIndex());
		Map<String, Object> userMfaSettings = IAMUserUtil.getUserMfaSettings(emailFromDigest, groupType);
		if(IAMUserUtil.totpChecking(totp, (long) userMfaSettings.get("userId"))){
			IAMUserUtil.updateUserMfaSettingsStatus((long) userMfaSettings.get("userId"),true);
			Organization org = IAMUserUtil.getDefaultOrg((long) userMfaSettings.get("userId"));
			setDomain(org.getDomain());
			var securityPolicy = IAMUserUtil.getUserSecurityPolicy(emailFromDigest, groupType);
			var resetRequired = false;
			if (securityPolicy != null && securityPolicy.getIsPwdPolicyEnabled() != null && securityPolicy.getIsPwdPolicyEnabled() && securityPolicy.getPwdMinAge() != null) {
				resetRequired = handlePasswordPolicy(securityPolicy, emailFromDigest, groupType);
			}

			if (StringUtils.isNotEmpty(getLookUpType())) {
				if (getLookUpType().equalsIgnoreCase("service")) {
					List<AppDomain> appDomain = IAMAppUtil.getAppDomain(AppDomainType.SERVICE_PORTAL, org.getOrgId());
					serverName = appDomain.stream().filter(i -> i.getDomainTypeEnum() == AppDomain.DomainType.DEFAULT).findAny().get().getDomain();
				} else if (getLookUpType().equalsIgnoreCase("tenant"))  {
					List<AppDomain> appDomain = IAMAppUtil.getAppDomain(AppDomainType.TENANT_PORTAL, org.getOrgId());
					serverName = appDomain.stream().filter(i -> i.getDomainTypeEnum() == AppDomain.DomainType.DEFAULT).findAny().get().getDomain();
				} else if (getLookUpType().equalsIgnoreCase("vendor")) {
					List<AppDomain> appDomain = IAMAppUtil.getAppDomain(AppDomainType.VENDOR_PORTAL, org.getOrgId());
					serverName = appDomain.stream().filter(i -> i.getDomainTypeEnum() == AppDomain.DomainType.DEFAULT).findAny().get().getDomain();
				} else if (getLookUpType().equalsIgnoreCase("workq")) {
					List<AppDomain> appDomain = IAMAppUtil.getAppDomain(AppDomainType.FACILIO, org.getOrgId());
					serverName = appDomain.stream().filter(i -> i.getDomainTypeEnum() == AppDomain.DomainType.DEFAULT).findAny().get().getDomain();
				}  else if (getLookUpType().equalsIgnoreCase(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP)) {
					List<AppDomain> appDomain = IAMAppUtil.getAppDomain(AppDomainType.EMPLOYEE_PORTAL, org.getOrgId());
					serverName = appDomain.stream().filter(i -> i.getDomainTypeEnum() == AppDomain.DomainType.DEFAULT).findAny().get().getDomain();
				}
			}

			if (!resetRequired) {
				HttpServletResponse response = ServletActionContext.getResponse();
				String userAgent = request.getHeader("User-Agent");

				String userType = "web";
				String deviceType = request.getHeader("X-Device-Type");
				if (!StringUtils.isEmpty(deviceType)
						&& ("android".equalsIgnoreCase(deviceType) || "ios".equalsIgnoreCase(deviceType) || "webview".equalsIgnoreCase(deviceType))) {
					userType = "mobile";
				}

				String ipAddress = request.getHeader("X-Forwarded-For");

				String authtoken = IAMUserUtil.verifyLoginWithoutPassword(emailFromDigest, userAgent, userType, ipAddress, serverName, null);
				setJsonresponse("token", authtoken);
				setJsonresponse("username", emailFromDigest);

				//deleting .facilio.com cookie(temp handling)
				FacilioCookie.eraseUserCookie(request, response,"fc.idToken.facilio","facilio.com");
				FacilioCookie.eraseUserCookie(request, response,"fc.idToken.facilio","facilio.in");
				FacilioCookie.eraseUserCookie(request, response,"fc.idToken.proxy","facilio.in");
				FacilioCookie.eraseUserCookie(request, response,"fc.idToken.proxy","facilio.com");

				String appDomain = request.getServerName();
				AppDomain appdomainObj = IAMAppUtil.getAppDomain(appDomain);
				boolean portalUser = false;
				if(appdomainObj != null && appdomainObj.getAppDomainTypeEnum() != AppDomainType.FACILIO) {
					portalUser = true;
				}

				addAuthCookies(authtoken, portalUser, false, request, "mobile".equals(userType));

				String isWebView = FacilioCookie.getUserCookie(request, "fc.isWebView");
				if ("true".equalsIgnoreCase(isWebView)) {
					if (StringUtils.isNotEmpty(getLookUpType()) && "mobile".equals(userType)) {
						setPortalWebViewCookies(getLookUpType());
					} else {
						setWebViewCookies();
					}
				}
			}
		} else{
			LOGGER.log(Level.INFO, "invalid verification code " + emailFromDigest);
			setJsonresponse("message", "Invalid verification code");
		}

		return SUCCESS;
	}
	
	private String idToken;
	
	public void setIdToken(String idToken) {
		this.idToken = idToken;
	}
	
	public String getIdToken() {
		return this.idToken;
	}
	
	public String googleSignIn() throws Exception {
		
		JSONObject payload = FederatedIdentityUtil.verifyGooogeIdToken(idToken);
		HttpServletRequest request = ServletActionContext.getRequest();

		String isWebView = FacilioCookie.getUserCookie(request, "fc.isWebView");
		
		if (payload != null) {
			String email = (String) payload.get("email");
			String hostedDomain = (String) payload.get("hd");
			String name = (String) payload.get("name");
			String locale = (String) payload.get("locale");
		
			try {
				LOGGER.info("validateGoogleSignIn()");
				String authtoken = null;
				boolean portalUser = false;
				String appDomain = request.getServerName();
				AppDomain appdomainObj = IAMAppUtil.getAppDomain(appDomain);
				if(appdomainObj != null && appdomainObj.getAppDomainTypeEnum() != AppDomainType.FACILIO) {
					portalUser = true;
				}
				
				String userAgent = request.getHeader("User-Agent");
				userAgent = userAgent != null ? userAgent : "";
				userAgent = "googleauth:" + userAgent;
				String ipAddress = request.getHeader("X-Forwarded-For");
				String serverName = request.getServerName();
				String[] domainNameArray = serverName.split("\\.");
				
				String domainName = "app";
				if (domainNameArray.length > 2) {
					String awsDomain = FacilioProperties.getDomain();
					if(StringUtils.isEmpty(awsDomain)) {
						awsDomain = "facilio";
					}
					if(!domainNameArray[1].equals(awsDomain) ) {
						domainName = domainNameArray[0];
					}
				}
				
				ipAddress = (ipAddress == null || "".equals(ipAddress.trim())) ? request.getRemoteAddr() : ipAddress;
	            String userType = "web";
				String deviceType = request.getHeader("X-Device-Type");
				if (!StringUtils.isEmpty(deviceType)
						&& ("android".equalsIgnoreCase(deviceType) || "ios".equalsIgnoreCase(deviceType))) {
					userType = "mobile";
				}
	
				LOGGER.info("validateLogin() : domainName : " + domainName);
				
				authtoken = IAMUserUtil.verifyLoginWithoutPassword(email, userAgent, userType, ipAddress, request.getServerName(), IAMAccountConstants.SocialLogin.GOOGLE);
				setResult("token", authtoken);
				setResult("username", email);
	
				addAuthCookies(authtoken, portalUser, false, request, "true".equalsIgnoreCase(isWebView));

				if ("true".equalsIgnoreCase(isWebView)) {
					setWebViewCookies();
					return SUCCESS;
				}
			} 
			catch (Exception e) {
				CommonCommandUtil.emailNewLead("New Lead - Google User", name, email, locale, hostedDomain);
				
				LOGGER.log(Level.INFO, "Exception while validating google signin, ", e);
				setResponseCode(1);
				Exception ex = e;
				while (ex != null) {
					if (ex instanceof AccountException) {
						setResult("message", ex.getMessage());
						break;
					}
					ex = (Exception) ex.getCause();
				}
				return ERROR;
			}
		}
		else {
			setResponseCode(1);
			setResult("message", "Invalid idToken!");
			LOGGER.info("Invalid idToken!");
		}
		return SUCCESS;
	}
	
	private String ssoToken;
	
	public void setSsoToken(String ssoToken) {
		this.ssoToken = ssoToken;
	}
	
	public String getSsoToken() {
		return this.ssoToken;
	}

	public String domainSSOSignIn() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();


		String samlResponse = request.getParameter("SAMLResponse");
		String relayState = request.getParameter("RelayState");

		try {
			if (getSsoToken() == null || samlResponse == null) {
				setResponseCode(1);
				setResult("message", "Invalid SSO access.");
				LOGGER.severe("missing sso token and saml response");
				return ERROR;
			}

			String str = SSOUtil.base64Decode(getSsoToken());
			long appDomainId = Long.parseLong(str.split("_")[0]);
			long ssoId = Long.parseLong(str.split("_")[1]);

			DomainSSO sso = IAMOrgUtil.getDomainSSODetails(ssoId);
			if (sso == null || sso.getIsActive() == null || !sso.getIsActive()) {
				setResponseCode(1);
				setResult("message", "Invalid SSO access.");
				LOGGER.severe("domain sso inactive/missing");
				return ERROR;
			}
			var isCreateUser = sso.getIsCreateUser() != null && sso.getIsCreateUser();

			SamlSSOConfig ssoConfig = (SamlSSOConfig) sso.getSSOConfig();

			SAMLServiceProvider samlClient = new SAMLServiceProvider(SSOUtil.getSPMetadataURL(sso), SSOUtil.getSPAcsURL(sso), ssoConfig.getEntityId(), ssoConfig.getLoginUrl(), ssoConfig.getCertificate());
			SAMLResponse resp = samlClient.validateSAMLResponse(samlResponse, "POST");

			String email = resp.getNameId();
			String name = findUserName(resp);

			if (StringUtils.isEmpty(name)) {
				name = email;
			}

			String appDomain = request.getServerName();
			AppDomain appdomainObj = IAMAppUtil.getAppDomain(appDomain);
			var orgId = appdomainObj.getOrgId();
			try {
				LOGGER.info("validateSSOSignIn()");
				String authtoken = null;
				boolean portalUser = false;
				if(appdomainObj != null && appdomainObj.getAppDomainTypeEnum() != AppDomainType.FACILIO) {
					portalUser = true;
				}

				String userAgent = request.getHeader("User-Agent");
				userAgent = userAgent != null ? userAgent : "";
				userAgent = "sso:" + userAgent;
				String ipAddress = request.getHeader("X-Forwarded-For");
				String serverName = request.getServerName();
				String[] domainNameArray = serverName.split("\\.");

				String domainName = "app";
				if (domainNameArray.length > 2) {
					String awsDomain = FacilioProperties.getDomain();
					if(StringUtils.isEmpty(awsDomain)) {
						awsDomain = "facilio";
					}
					if(!domainNameArray[1].equals(awsDomain) ) {
						domainName = domainNameArray[0];
					}
				}

				ipAddress = (ipAddress == null || "".equals(ipAddress.trim())) ? request.getRemoteAddr() : ipAddress;
				String userType = "web";
				String deviceType = request.getHeader("X-Device-Type");
				if (!StringUtils.isEmpty(deviceType)
						&& ("android".equalsIgnoreCase(deviceType) || "ios".equalsIgnoreCase(deviceType))) {
					userType = "mobile";
				}

				LOGGER.info("validateLogin() : domainName : " + domainName);

				authtoken = IAMUserUtil.verifyLoginWithoutPassword(email, userAgent, userType, ipAddress, request.getServerName(), null);
				setResult("token", authtoken);
				setResult("username", email);
				boolean isWebView = false;
				if (relayState != null && (relayState.indexOf("http") >= 0)) {
					setResult("url", relayState);
				}
				else {
					if (relayState != null && relayState.startsWith("mobile")) {
						isWebView = true;
						Organization org = IAMOrgUtil.getOrg(appdomainObj.getOrgId());
						setDomain(org.getDomain());
						if ("mobile-facilio".equals(relayState)) {
							setPortalWebViewCookies("facilio");
						} else if ("mobile-serviceportal".equals(relayState)) {
							setLookUpType("service");
							setPortalWebViewCookies("service");
						} else if ("mobile-tenantportal".equals(relayState)) {
							setLookUpType("tenant");
							setPortalWebViewCookies("tenant");
						} else if ("mobile-vendorportal".equals(relayState)) {
							setLookUpType("vendor");
							setPortalWebViewCookies("vendor");
						}  else if ("mobile-clientportal".equals(relayState)) {
							setLookUpType("client");
							setPortalWebViewCookies("client");
						}
						setResult("url", SSOUtil.getLoginSuccessURL(true));
					} else {
						setResult("url", SSOUtil.getLoginSuccessURL(false));
					}
				}

				addAuthCookies(authtoken, portalUser, false, request, isWebView || "mobile".equals(userType));
				FacilioCookie.addLoggedInCookie(response);
			}
			catch (Exception e) {
				if (isCreateUser
						&& (Throwables.getRootCause(e) instanceof AccountException)
						&& ((AccountException) Throwables.getRootCause(e)).getErrorCode() == USER_DEACTIVATED_FROM_THE_ORG) {
					LOGGER.log(Level.INFO, "Creating portal user");
					return createPortalUserAndLogin(email, name, orgId);
				}
				LOGGER.log(Level.INFO, "Exception while validating sso signin, ", e);
				setResponseCode(1);
				Exception ex = e;
				while (ex != null) {
					if (ex instanceof AccountException) {
						setResult("message", ex.getMessage());
						break;
					}
					ex = (Exception) ex.getCause();
				}
				return ERROR;
			}
		}
		catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception while validating SAMLResponse, ", e);
			setResponseCode(1);
			setResult("message", "Invalid SSO access.");
			return ERROR;
		}
		return SUCCESS;
	}

	private String findUserName(SAMLResponse resp) {
		Map<String, String> attributes = resp.getAttributes();
		var name = "";
		if (MapUtils.isNotEmpty(attributes)) {
			Set<String> keys = attributes.keySet();
			var found = false;
			for (String key: keys) {
				if (key.equalsIgnoreCase("name")) {
					found = true;
					name = attributes.get(key);
					break;
				}
			}

			if (!found) {
				for (String key: keys) {
					if (key.equalsIgnoreCase("firstname")) {
						name = attributes.get(key);
						break;
					}
				}

				for (String key: keys) {
					if (key.equalsIgnoreCase("lastname")) {
						name = " " + attributes.get(key);
						break;
					}
				}
			}
		}
		return name;
	}

	private String createPortalUserAndLogin(String email, String name, long orgId) throws Exception {
		try {
			createPortalUser(email, name, orgId);
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, "Exception while creating portal user.");
			throw ex;
		}
		LOGGER.log(Level.INFO, "Created portal user");
		return domainSSOSignIn();
	}

	private void createPortalUser(String emailaddress, String name, long orgId) throws Exception {
		PeopleAction peopleAction = new PeopleAction();
		EmployeeAction employeeAction = new EmployeeAction();
		PeopleContext pplContext = PeopleAPI.getPeople(emailaddress,true);
		if (pplContext == null) {
			EmployeeContext empContext = new EmployeeContext();
			empContext.setEmail(emailaddress);
			empContext.setName(name);
			empContext.setIsOccupantPortalAccess(true);
			HashMap<String, Long> roleMap = new HashMap<>();
			RoleBean roleBean = AccountUtil.getRoleBean();
			Role role = roleBean.getRole(orgId, FacilioConstants.DefaultRoleNames.OCCUPANT_USER);
			roleMap.put(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP, role.getId());
			empContext.setRolesMap(roleMap);
			employeeAction.setEmployees(Arrays.asList(empContext));
			employeeAction.addEmployees();
		} else {
			pplContext.setIsOccupantPortalAccess(true);
			RoleBean roleBean = AccountUtil.getRoleBean();
			HashMap<String, Long> roleMap = new HashMap<>();
			Role role = roleBean.getRole(orgId, FacilioConstants.DefaultRoleNames.OCCUPANT_USER);
			roleMap.put(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP, role.getId());
			pplContext.setRolesMap(roleMap);
			peopleAction.setPeopleList(Arrays.asList(pplContext));
			peopleAction.updatePeople(true);
		}
	}

	public String ssoSignIn() throws Exception {
		
		HttpServletRequest request = ServletActionContext.getRequest();
		
		String samlResponse = request.getParameter("SAMLResponse");
		String relayState = request.getParameter("RelayState");
		
		try {
			if (getSsoToken() == null || samlResponse == null) {
				setResponseCode(1);
				setResult("message", "Invalid SSO access.");
				return ERROR;
			}
			
			String str = SSOUtil.base64Decode(getSsoToken());
			long orgId = Long.parseLong(str.split("_")[0]);
			long ssoId = Long.parseLong(str.split("_")[1]);

			Organization org = IAMOrgUtil.getOrg(orgId);
			setDomain(org.getDomain());
			
			AccountSSO sso = IAMOrgUtil.getAccountSSO(orgId);
			if (sso == null || sso.getIsActive() == null || !sso.getIsActive()) {
				setResponseCode(1);
				setResult("message", "Invalid SSO access.");
				return ERROR;
			}
			
			SamlSSOConfig ssoConfig = (SamlSSOConfig) sso.getSSOConfig();
			
			SAMLServiceProvider samlClient = new SAMLServiceProvider(SSOUtil.getSPMetadataURL(sso), SSOUtil.getSPAcsURL(sso), ssoConfig.getEntityId(), ssoConfig.getLoginUrl(), ssoConfig.getCertificate());
			SAMLResponse resp = samlClient.validateSAMLResponse(samlResponse, "POST");
			
			String email = resp.getNameId();
			
			try {
				LOGGER.info("validateSSOSignIn()");
				String authtoken = null;
				boolean portalUser = false;
				String appDomain = request.getServerName();
				AppDomain appdomainObj = IAMAppUtil.getAppDomain(appDomain);
				if(appdomainObj != null && appdomainObj.getAppDomainTypeEnum() != AppDomainType.FACILIO) {
					portalUser = true;
				}
				
				String userAgent = request.getHeader("User-Agent");
				userAgent = userAgent != null ? userAgent : "";
				userAgent = "sso:" + userAgent;
				String ipAddress = request.getHeader("X-Forwarded-For");
				String serverName = request.getServerName();
				String[] domainNameArray = serverName.split("\\.");
				
				String domainName = "app";
				if (domainNameArray.length > 2) {
					String awsDomain = FacilioProperties.getDomain();
					if(StringUtils.isEmpty(awsDomain)) {
						awsDomain = "facilio";
					}
					if(!domainNameArray[1].equals(awsDomain) ) {
						domainName = domainNameArray[0];
					}
				}
				
				ipAddress = (ipAddress == null || "".equals(ipAddress.trim())) ? request.getRemoteAddr() : ipAddress;
	            String userType = "web";
				String deviceType = request.getHeader("X-Device-Type");
				if (!StringUtils.isEmpty(deviceType)
						&& ("android".equalsIgnoreCase(deviceType) || "ios".equalsIgnoreCase(deviceType))) {
					userType = "mobile";
				}
	
				LOGGER.info("validateLogin() : domainName : " + domainName);
				
				authtoken = IAMUserUtil.verifyLoginWithoutPassword(email, userAgent, userType, ipAddress, request.getServerName(), null);
				setResult("token", authtoken);
				setResult("username", email);
				boolean isWebView = false;
				if (relayState != null && (relayState.indexOf("http") >= 0)) {
					setResult("url", relayState);
				}
				else {
					if (relayState != null && relayState.startsWith("mobile")) {
						isWebView = true;
						if ("mobile-facilio".equals(relayState)) {
							setPortalWebViewCookies("facilio");
						}else if("mobile-workQ".equals(relayState)){
							setPortalWebViewCookies("workQ");
						}else if ("mobile-serviceportal".equals(relayState)) {
							setPortalWebViewCookies("service");
						} else if ("mobile-tenantportal".equals(relayState)) {
							setPortalWebViewCookies("tenant");
						} else if ("mobile-vendorportal".equals(relayState)) {
							setPortalWebViewCookies("vendor");
						}
						setResult("url", SSOUtil.getLoginSuccessURL(true));
					} else {
						setResult("url", SSOUtil.getLoginSuccessURL(false));
					}
				}
	
				addAuthCookies(authtoken, portalUser, false, request, isWebView || "mobile".equals(userType));
			} 
			catch (Exception e) {
				LOGGER.log(Level.INFO, "Exception while validating sso signin, ", e);
				setResponseCode(1);
				Exception ex = e;
				while (ex != null) {
					if (ex instanceof AccountException) {
						setResult("message", ex.getMessage());
						break;
					}
					ex = (Exception) ex.getCause();
				}
				return ERROR;
			}
		}
		catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception while validating SAMLResponse, ", e);
			setResponseCode(1);
			setResult("message", "Invalid SSO access.");
			return ERROR;
		}
		return SUCCESS;
	}
	
	private String domain;
	
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public String domainDetails() throws Exception {
		if (getDomain() == null) {
			setResponseCode(1);
			setResult("message", "Invalid domain");
			return ERROR;
		}
		
		Organization org = IAMOrgUtil.getOrg(getDomain());
		if (org == null) {
			setResponseCode(1);
			setResult("message", "Invalid domain");
			return ERROR;
		}
		
		JSONObject domainDetails = new JSONObject();
		domainDetails.put("logo_url", org.getLogoUrl());
		
		AccountSSO sso = IAMOrgUtil.getAccountSSO(getDomain());
		if (sso != null && sso.getIsActive()) {
			domainDetails.put("sso_enabled", true);
			domainDetails.put("sso_url", SSOUtil.getSSOEndpoint(org.getDomain()));
		}
		
		setResult("domainDetails", domainDetails);
		
		return SUCCESS;
	}

	public String loadWebView() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		String authtoken = request.getParameter("authtoken");
		String serviceurl = request.getParameter("serviceurl");
		String isPortal = request.getParameter("isPortal");
		String isDevice = request.getParameter("isDevice");
		String currentOrg = request.getParameter("currentOrg");
		String currentSite = request.getParameter("currentSite");
		String proxyToken = request.getParameter("proxyToken");
		boolean isPotalUser = false;
		boolean isDeviceUser = false;
		if(org.apache.commons.lang3.StringUtils.isNotEmpty(isPortal) && isPortal.contentEquals("true")) {
			isPotalUser = true;
		}
		if(org.apache.commons.lang3.StringUtils.isNotEmpty(isDevice) && isDevice.contentEquals("true")) {
			isDeviceUser = true;
		}
		if (isDeviceUser) {
			addDeviceAuthCookies(authtoken, request);
		}
		else if(org.apache.commons.lang3.StringUtils.isNotEmpty(proxyToken)){
			addAuthCookies(authtoken, isPotalUser, isDeviceUser, request, false,proxyToken);
		}
		else {
			addAuthCookies(authtoken, isPotalUser, isDeviceUser, request);
		}
		
		if (org.apache.commons.lang3.StringUtils.isNotEmpty(currentOrg)) {
			FacilioCookie.addOrgDomainCookie(currentOrg, response);
		}
		if (org.apache.commons.lang3.StringUtils.isNotEmpty(currentSite)) {
			FacilioCookie.addCurrentSiteCookie(currentSite, response);
		}

		FacilioCookie.addLoggedInCookie(response);

		try {
			response.sendRedirect(serviceurl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private void addAuthCookies(String authtoken, boolean portalUser, boolean isDeviceUser, HttpServletRequest request) throws Exception {
		addAuthCookies(authtoken, portalUser, isDeviceUser, request, false);
	}

	private void addAuthCookies(String authtoken, boolean portalUser, boolean isDeviceUser, HttpServletRequest request, boolean isMobile) throws Exception {
		addAuthCookies(authtoken, portalUser, isDeviceUser, request, isMobile, null);
	}
	
	private void addAuthCookies(String authtoken, boolean portalUser, boolean isDeviceUser, HttpServletRequest request, boolean isMobile, String proxyToken) throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();
		Cookie cookie = new Cookie("fc.idToken.facilio", authtoken);
		Cookie proxyCookie = null;
		if (StringUtils.isNotEmpty(proxyToken)) {
			proxyCookie = new Cookie("fc.idToken.proxy", proxyToken);
		}

		if (isDeviceUser) {
			cookie = new Cookie("fc.deviceTokenNew", authtoken);
		}

		setCookieProperties(cookie,true);
		if (proxyCookie != null) {
			setCookieProperties(proxyCookie, true);
		}

		FacilioCookie.addOrgDomainCookie(getDomain(), response);

		if (isMobile && StringUtils.isNotEmpty(proxyToken)) {
			JSONObject jsonObject = getMobileProxyAuthJSON("token", authtoken, "proxyToken",proxyToken, "homePath", "/app/mobile/login", "domain", getDomain(), "baseUrl", getBaseUrl());
			Cookie mobileTokenCookie = new Cookie("fc.mobile.idToken.facilio", new AESEncryption().encrypt(jsonObject.toJSONString()));
			setTempCookieProperties(mobileTokenCookie, false);
			response.addCookie(mobileTokenCookie);
		}
		else if(isMobile){
			JSONObject jsonObject = getMobileAuthJSON("token", authtoken,  "homePath", "/app/mobile/login", "domain", getDomain(), "baseUrl", getBaseUrl());
			Cookie mobileTokenCookie = new Cookie("fc.mobile.idToken.facilio", new AESEncryption().encrypt(jsonObject.toJSONString()));
			setTempCookieProperties(mobileTokenCookie, false);
			response.addCookie(mobileTokenCookie);
		}

		if (FacilioProperties.isDevelopment()) {
			if (FacilioProperties.isOnpremise()) {

				var cookieString = "fc.idToken.facilio=" + authtoken + "; Max-Age=604800; Path=/; HttpOnly;";
				response.addHeader("Set-Cookie", cookieString);
				if (proxyCookie != null) {
					var proxyCookieString = "fc.idToken.proxy=" + proxyToken + "; Max-Age=604800; Path=/; HttpOnly;";
					response.addHeader("Set-Cookie", proxyCookieString);
				}
			} else {
				var cookieString = "fc.idToken.facilio=" + authtoken + "; Max-Age=604800; Path=/; HttpOnly; SameSite=Lax";
				response.addHeader("Set-Cookie", cookieString);
				if (proxyCookie != null) {
					var proxyCookieString = "fc.idToken.proxy=" + proxyToken + "; Max-Age=604800; Path=/; HttpOnly; SameSite=Lax";
					response.addHeader("Set-Cookie", proxyCookieString);
				}
				if (portalUser) {
					var portalCookie = "fc.idToken.facilioportal=" + authtoken + "; Max-Age=604800; Path=/; HttpOnly; SameSite=Lax";
					response.addHeader("Set-Cookie", portalCookie);
				}
			}
		} else if ("stage".equals(FacilioProperties.getEnvironment()) || "stage2".equals(FacilioProperties.getEnvironment())) {
			LOGGER.log(Level.SEVERE, "Stage login");
			var cookieString = "fc.idToken.facilio=" + authtoken + "; Max-Age=604800; Path=/; Secure; HttpOnly; SameSite=None";
			response.addHeader("Set-Cookie", cookieString);
			if (proxyCookie != null) {
				var proxyCookieString = "fc.idToken.proxy=" + proxyToken + "; Max-Age=604800; Path=/; Secure; HttpOnly; SameSite=None";
				response.addHeader("Set-Cookie", proxyCookieString);
			}
		} else {
			response.addCookie(cookie);
			if (proxyCookie != null) {
				response.addCookie(proxyCookie);
			}

				//Can be removed once service portal file api is not used.
			if (portalUser) {
				Cookie portalCookie = new Cookie("fc.idToken.facilioportal", authtoken);
				setCookieProperties(portalCookie, true);
				response.addCookie(portalCookie);
			}
		}
	}


	private JSONObject getMobileAuthJSON(String token, String authtoken, String homePath, String value, String domain, String Domain, String baseUrl, String BaseUrl) throws Exception {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(token, authtoken);
		jsonObject.put(homePath, value);
		jsonObject.put(domain, Domain);
		jsonObject.put(baseUrl, BaseUrl);
		return jsonObject;
	}
	private JSONObject getMobileProxyAuthJSON(String token, String authtoken, String proxyToken, String proxyUserToken, String homePath, String value, String domain, String Domain, String baseUrl, String BaseUrl) throws Exception {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(token, authtoken);
		jsonObject.put(proxyToken,proxyUserToken);
		jsonObject.put(homePath, value);
		jsonObject.put(domain, Domain);
		jsonObject.put(baseUrl, BaseUrl);
		return jsonObject;
	}

	private String checkDcAndGetRedirectUrl(GroupType groupType) throws Exception {
		Integer dc = IAMUserUtil.lookupUserDC(getUsername(), groupType);
		if (dc == null) {
			return null;
		}
		String baseUrl = DCUtil.getMainAppDomain(dc);
		return getProtocol() + "://" + baseUrl;
	}
	
	private String getBaseUrl() throws Exception {
		StringBuilder baseUrl = new StringBuilder(getProtocol()).append("://");
		Organization org = IAMOrgUtil.getOrg(getDomain());
		if (StringUtils.isNotEmpty(getLookUpType())) {
			if (getLookUpType().equalsIgnoreCase("service") ) {
				List<AppDomain> appDomain = IAMAppUtil.getAppDomain(AppDomainType.SERVICE_PORTAL, org.getOrgId());
				baseUrl.append(appDomain.stream().filter(i -> i.getDomainTypeEnum() == AppDomain.DomainType.DEFAULT).findAny().get().getDomain());
			} else if (getLookUpType().equalsIgnoreCase("tenant")) {
				List<AppDomain> appDomain = IAMAppUtil.getAppDomain(AppDomainType.TENANT_PORTAL, org.getOrgId());
				baseUrl.append(appDomain.stream().filter(i -> i.getDomainTypeEnum() == AppDomain.DomainType.DEFAULT).findAny().get().getDomain());
			} else if (getLookUpType().equalsIgnoreCase("vendor")) {
				List<AppDomain> appDomain = IAMAppUtil.getAppDomain(AppDomainType.VENDOR_PORTAL, org.getOrgId());
				baseUrl.append(appDomain.stream().filter(i -> i.getDomainTypeEnum() == AppDomain.DomainType.DEFAULT).findAny().get().getDomain());
			} else if (getLookUpType().equalsIgnoreCase("workq")) {
				List<AppDomain> appDomain = IAMAppUtil.getAppDomain(AppDomainType.FACILIO, org.getOrgId());
				baseUrl.append(appDomain.stream().filter(i -> i.getDomainTypeEnum() == AppDomain.DomainType.DEFAULT).findAny().get().getDomain());
			} else if (getLookUpType().equalsIgnoreCase(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP)) {
				List<AppDomain> appDomain = IAMAppUtil.getAppDomain(AppDomainType.EMPLOYEE_PORTAL, org.getOrgId());
				baseUrl.append(appDomain.stream().filter(i -> i.getDomainTypeEnum() == AppDomain.DomainType.DEFAULT).findAny().get().getDomain());
			} else if (getLookUpType().equalsIgnoreCase("client")) {
				List<AppDomain> appDomain = IAMAppUtil.getAppDomain(AppDomainType.CLIENT_PORTAL, org.getOrgId());
				baseUrl.append(appDomain.stream().filter(i -> i.getDomainTypeEnum() == AppDomain.DomainType.DEFAULT).findAny().get().getDomain());
			}
		}
		else {
			List<AppDomain> appDomain = IAMAppUtil.getAppDomain(AppDomainType.FACILIO, org.getOrgId());
			baseUrl.append(appDomain.stream().filter(i -> i.getDomainTypeEnum() == AppDomain.DomainType.DEFAULT).findAny().get().getDomain());
		}

		String s = baseUrl.toString();

		LOGGER.log(Level.SEVERE, "[base url] " + s);
		return s;
	}
	
	private String getProtocol() {
		HttpServletRequest request = ActionContext.getContext() != null ? ServletActionContext.getRequest() : null;
		return RequestUtil.getProtocol(request);
	}

	private void setTempCookieProperties(Cookie cookie, boolean authModel) {
		cookie.setMaxAge(60 * 60); // Make the cookie last an hour
		cookie.setPath("/");
		cookie.setHttpOnly(authModel);
		if (!(FacilioProperties.isDevelopment() || FacilioProperties.isOnpremise())) {
			cookie.setSecure(true);
		}
	}

	private void setCookieProperties(Cookie cookie, boolean authModel) {
		cookie.setMaxAge(FacilioProperties.getAuthTokenCookieLifespan()); // Making the cookie last 1 week only be default
		cookie.setPath("/");
		cookie.setHttpOnly(authModel);
		if (!(FacilioProperties.isDevelopment() || FacilioProperties.isOnpremise())) {
			cookie.setSecure(true);
		}
	}
	public String validateInviteLink() throws Exception {

		JSONObject invitation = new JSONObject();
		HttpServletRequest request = ServletActionContext.getRequest();
		Organization org = null;
		User user = AccountUtil.getUserBean().validateUserInvite(getInviteToken());
		if(user != null) {
			user = AccountUtil.getUserBean(user.getOrgId()).getUser(-1, user.getOrgId(), user.getUid(), null);
			org = AccountUtil.getOrgBean().getOrg(user.getOrgId());
			AccountUtil.cleanCurrentAccount();
			AccountUtil.setCurrentAccount(new Account(org, user));
		} else {
			LOGGER.severe("user is empty");
		}
		
		if (user != null && !user.isInviteAcceptStatus()) {
			invitation.put("email", user.getEmail());
			invitation.put("orgname", org.getName());
			invitation.put("userid", user.getOuid());
			invitation.put("isVerified", user.isUserVerified());
			
		} else {
			invitation.put("error", "link_expired");
		}

		setResult("invitation", invitation);

		return SUCCESS;
	}

	public void postToSentry() throws Exception {
		Map<String, Object> data = (Map<String, Object>) entry.get(0);
		List changes = (List) data.get("changes");
		Map<String, Object> change = (Map<String, Object>) changes.get(0);
		Map<String, Object> value = (Map<String, Object>) change.get("value");

		String message = value.get("message").toString();
		String permalink = value.get("permalink_url").toString();
		String postId = value.get("post_id").toString();
		String from = value.get("from").toString();

		Context context = sentry.getContext();
		context.clear();
		context.setUser(new UserBuilder().setEmail(EmailClient.getFromEmail("issues")).build());
		context.addTag("url", permalink);
		context.addTag("postedBy", from);
		context.addTag("postId", postId);

		sentry.sendMessage(message);
	}

	public String postIssueResponse() throws Exception {

		HttpServletResponse response2 = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();

		if (request.getParameterValues("hub.challenge") != null) {
			setJsonresponse("message", "post issue verification response received successfully");
			String str[] = request.getParameterValues("hub.challenge");
			if (str[0] != null) {
				response2.getWriter().write(str[0]);
			}
			return NONE;
		} else {
			try {
				try {
					postToSentry();
				} catch(Exception e) {
					LOGGER.log(Level.INFO, "Error while posting issue to sentry", e);
				}

				JSONParser parser = new JSONParser();

				Map<String, Object> jbJsonObj = (Map<String, Object>) entry.get(0);
				List test1 = (List) jbJsonObj.get("changes");
				Map<String, Object> jbJsonObj1 = (Map<String, Object>) test1.get(0);
				Map<String, Object> jbJsonObj2 = (Map<String, Object>) jbJsonObj1.get("value");
				String jb = jbJsonObj2.get("message").toString();
				String link = (String) jbJsonObj2.get("permalink_url");
				link = "<a href= '" + link + "' >" + link + "</a>";

				String line = null;
				String url = "https://facilio.freshrelease.com/DEMO/issues";
				String description = link, key = null, blockedReason = null;
				List<String> tags = new ArrayList<String>();
				Map<String, Object> customField = new HashMap<>();

				Integer createrId = null, position = null, etaFlag = null, storyPoints = null, issueTypeId = 161,
						ownerId = null, parentId = null, epicId = null, priorityId = 252, projectId = null,
						subProjectId = null, reporterId = null, sprintId = null, statusId = null, releaseId = null;

				List<Integer> documentIds = new ArrayList<Integer>();
				boolean resolved = false, blocked = false, following = false;

				FacilioAuthAction issue = new FacilioAuthAction();
				LOGGER.info("jbstring" + jb.toString());

				if (jb.toString().equals("")) {
					issue.setTitle("Sample Test");
				} else {
					issue.setTitle(jb.toString());
				}

				if (issue.getTitle().equals("")) {
					issue.setTitle("test");
				}
				JSONObject jget = new JSONObject();

				jget.put("description", link);
				jget.put("key", key);
				jget.put("story_points", storyPoints);
				jget.put("title", issue.getTitle());
				jget.put("resolved", resolved);
				jget.put("blocked", blocked);
				jget.put("following", following);
				jget.put("blocked_reason", blockedReason);
				jget.put("tags", tags);
				jget.put("eta_flag", etaFlag);
				jget.put("position", position);
				jget.put("custom_field", customField);
				jget.put("document_ids", documentIds);
				jget.put("creater_id", createrId);
				jget.put("issue_type_id", issueTypeId);
				jget.put("owner_id", ownerId);
				jget.put("parent_id", parentId);
				jget.put("epic_id", epicId);
				jget.put("priority_id", priorityId);
				jget.put("project_id", projectId);
				jget.put("sub_project_id", subProjectId);
				jget.put("reporter_id", reporterId);
				jget.put("sprint_id", sprintId);
				jget.put("status_id", statusId);
				jget.put("release_id", releaseId);

				JSONObject newjget = new JSONObject();
				newjget.put("issue", jget);

				String body = newjget.toJSONString();
				Map<String, String> headers = new HashMap<>();
				headers.put("Authorization", "Token token=93LalYD_wbiIA1qD0sXiOQ");
				headers.put("Content-Type", "application/json");
				http("POST", url, headers, body);
				LOGGER.info("postIssueResponse" + jb.toString());

				setJsonresponse("message", "post issue response recieved successfully");

			} catch (Exception e) {
				setJsonresponse("message", "Error while reading post issue response");
				LOGGER.log(Level.INFO, "Error while reading post issue response", e);
			}
		}

		return SUCCESS;

	}

	private void http(String method, String url, Map headers, String body) {
		try {
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			con.setRequestMethod(method);

			// these are auth headers
			for (Object head : headers.keySet()) {
				con.setRequestProperty(head.toString(), headers.get(head).toString());
			}

			con.setDoOutput(true);
			try(DataOutputStream wr = new DataOutputStream(con.getOutputStream()); BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));) {
				wr.writeBytes(body);
				wr.flush();
				wr.close();
				
				int responseCode = con.getResponseCode();
				LOGGER.info("\nSending 'POST' request to URL : " + url);
				LOGGER.info("Post parameters : " + body);
				LOGGER.info("Response Code : " + responseCode);

				
				String inputLine;
				StringBuffer response = new StringBuffer();
				LOGGER.info("response code is " + con.getResponseCode());
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
			}
			LOGGER.info("issue response in freshrelease" + response.toString());
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Error while posting post issue response", e);
		}

	}

	public String verifyEmail() throws Exception {

		JSONObject invitation = new JSONObject();
		HttpServletRequest request = ServletActionContext.getRequest();

		User user = AccountUtil.getUserBean().verifyEmail(getInviteToken());
		if (user == null) {
			invitation.put("error", "link_expired");
		} else {
			invitation.put("email", user.getEmail());
			invitation.put("accepted", true);
		}
		ActionContext.getContext().getValueStack().set("invitation", invitation);

		return SUCCESS;
	}

	public String resetExpiredPassword() throws Exception {
		IAMUser user = null;
		try {
			user = IAMUserUtil.resetExpiredPassword(getDigest(), getRawPassword(),getConfirmPass());
		} catch (SecurityPolicyException secEx) {
			setJsonresponse("status", "failure");
			setJsonresponse("message", secEx.getMessage());
			return ERROR;
		} catch (Exception ex) {
			Throwable cause = ex.getCause();
			if (cause instanceof SecurityPolicyException) {
				setJsonresponse("status", "failure");
				setJsonresponse("message", cause.getMessage());
			}
			return ERROR;
		}

		AppDomain.GroupType groupType;
		if (StringUtils.isNotEmpty(getLookUpType())) {
			if (getLookUpType().equalsIgnoreCase("service") || getLookUpType().equalsIgnoreCase("tenant")) {
				groupType = AppDomain.GroupType.TENANT_OCCUPANT_PORTAL;
			} else if (getLookUpType().equalsIgnoreCase("vendor")) {
				groupType = AppDomain.GroupType.VENDOR_PORTAL;
			} else if (getLookUpType().equalsIgnoreCase(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP)) {
				groupType = AppDomain.GroupType.EMPLOYEE_PORTAL;
			} else if (getLookUpType().equalsIgnoreCase("client")) {
				groupType = AppDomain.GroupType.CLIENT_PORTAL;
			} else {
				groupType = AppDomain.GroupType.FACILIO;
			}
		} else {
			HttpServletRequest request = ServletActionContext.getRequest();
			AppDomain appdomainObj = IAMAppUtil.getAppDomain(request.getServerName());
			groupType = appdomainObj.getGroupTypeEnum();
		}
		Map<String, Object> userMfaSettings = IAMUserUtil.getUserMfaSettings(user.getUserName(), groupType);
		Organization org = IAMUserUtil.getDefaultOrg((long) userMfaSettings.get("userId"));
		setDomain(org.getDomain());

		HttpServletRequest request = ServletActionContext.getRequest();
		String serverName = request.getServerName();
		if (StringUtils.isNotEmpty(getLookUpType())) {
			if (getLookUpType().equalsIgnoreCase("service")) {
				List<AppDomain> appDomain = IAMAppUtil.getAppDomain(AppDomainType.SERVICE_PORTAL, org.getOrgId());
				serverName = appDomain.stream().filter(i -> i.getDomainTypeEnum() == AppDomain.DomainType.DEFAULT).findAny().get().getDomain();
			} else if (getLookUpType().equalsIgnoreCase("tenant"))  {
				List<AppDomain> appDomain = IAMAppUtil.getAppDomain(AppDomainType.TENANT_PORTAL, org.getOrgId());
				serverName = appDomain.stream().filter(i -> i.getDomainTypeEnum() == AppDomain.DomainType.DEFAULT).findAny().get().getDomain();
			} else if (getLookUpType().equalsIgnoreCase("vendor")) {
				List<AppDomain> appDomain = IAMAppUtil.getAppDomain(AppDomainType.VENDOR_PORTAL, org.getOrgId());
				serverName = appDomain.stream().filter(i -> i.getDomainTypeEnum() == AppDomain.DomainType.DEFAULT).findAny().get().getDomain();
			} else if (getLookUpType().equalsIgnoreCase("workq")) {
				List<AppDomain> appDomain = IAMAppUtil.getAppDomain(AppDomainType.FACILIO, org.getOrgId());
				serverName = appDomain.stream().filter(i -> i.getDomainTypeEnum() == AppDomain.DomainType.DEFAULT).findAny().get().getDomain();
			} else if (getLookUpType().equalsIgnoreCase(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP)) {
				List<AppDomain> appDomain = IAMAppUtil.getAppDomain(AppDomainType.EMPLOYEE_PORTAL, org.getOrgId());
				serverName = appDomain.stream().filter(i -> i.getDomainTypeEnum() == AppDomain.DomainType.DEFAULT).findAny().get().getDomain();
			} else if (getLookUpType().equalsIgnoreCase("client")) {
				List<AppDomain> appDomain = IAMAppUtil.getAppDomain(AppDomainType.CLIENT_PORTAL, org.getOrgId());
				serverName = appDomain.stream().filter(i -> i.getDomainTypeEnum() == AppDomain.DomainType.DEFAULT).findAny().get().getDomain();
			}
		}

		HttpServletResponse response = ServletActionContext.getResponse();
		String userAgent = request.getHeader("User-Agent");

		String userType = "web";
		String deviceType = request.getHeader("X-Device-Type");
		if (!StringUtils.isEmpty(deviceType)
				&& ("android".equalsIgnoreCase(deviceType) || "ios".equalsIgnoreCase(deviceType) || "webview".equalsIgnoreCase(deviceType))) {
			userType = "mobile";
		}

		String ipAddress = request.getHeader("X-Forwarded-For");

		String authtoken = IAMUserUtil.verifyLoginWithoutPassword(user.getEmail(), userAgent, userType, ipAddress, serverName, null);
		setJsonresponse("token", authtoken);
		setJsonresponse("username", user.getEmail());

		//deleting .facilio.com cookie(temp handling)
		FacilioCookie.eraseUserCookie(request, response,"fc.idToken.facilio","facilio.com");
		FacilioCookie.eraseUserCookie(request, response,"fc.idToken.facilio","facilio.in");
		FacilioCookie.eraseUserCookie(request, response,"fc.idToken.proxy","facilio.in");
		FacilioCookie.eraseUserCookie(request, response,"fc.idToken.proxy","facilio.com");

		String appDomain = request.getServerName();
		AppDomain appdomainObj = IAMAppUtil.getAppDomain(appDomain);
		boolean portalUser = false;
		if(appdomainObj != null && appdomainObj.getAppDomainTypeEnum() != AppDomainType.FACILIO) {
			portalUser = true;
		}

		Organization defaultOrg = IAMUserUtil.getDefaultOrg(user.getUid());
		setDomain(defaultOrg.getDomain());

		addAuthCookies(authtoken, portalUser, false, request, "mobile".equals(userType));

		String isWebView = FacilioCookie.getUserCookie(request, "fc.isWebView");
		if ("true".equalsIgnoreCase(isWebView)) {
			if (StringUtils.isNotEmpty(getLookUpType()) && "mobile".equals(userType)) {
				setPortalWebViewCookies(getLookUpType());
			} else {
				setWebViewCookies();
			}
		}

		return SUCCESS;
	}

	public String resetPassword() throws Exception {
		JSONObject invitation = new JSONObject();
		HttpServletRequest request = ServletActionContext.getRequest();

		if (getInviteToken() != null) {
			IAMUser iamUser = IAMUserUtil.resetPassword(getInviteToken(), getRawPassword(),getConfirmPass());
			if (iamUser == null) {
				invitation.put("message", "Link has been expired");
				ActionContext.getContext().getValueStack().set("invitation", invitation);
				return ERROR;
			} else {
				User user = new User(iamUser);
				if (user.getUid() > 0) {
					invitation.put("status", "success");
				}
			}
		} else {
			User user = null;
			AppDomain appDomain = IAMAppUtil.getAppDomain(request.getServerName());
			if(appDomain == null) {
				invitation.put("status", "failed");
				invitation.put("message", "Invalid app domain");
			}
			Map<String, Object> userMap = IAMUserUtil.getUserForUsername(getEmailaddress(), -1, appDomain.getIdentifier());
			if(MapUtils.isNotEmpty(userMap)) {
				user = FieldUtil.getAsBeanFromMap(userMap, User.class);
			}

			if (user != null && user.isUserVerified()) {
				AccountUtil.getUserBean().sendResetPasswordLinkv2(user, request.getServerName());
				invitation.put("status", "success");
			} else {
				if (user != null && !user.isUserVerified()) {
					LOGGER.log(Level.SEVERE, "User not verified.");
				}
				invitation.put("status", "failed");
				invitation.put("message", "Invalid user");
			}
		}
		ActionContext.getContext().getValueStack().set("invitation", invitation);
		return SUCCESS;
	}

	public String changePassword(){
		try {
			User user = AccountUtil.getCurrentUser();
			HttpServletRequest request = ServletActionContext.getRequest();
			
			String userType = "web";
			String deviceType = request.getHeader("X-Device-Type");
			if (!StringUtils.isEmpty(deviceType)
					&& ("android".equalsIgnoreCase(deviceType) || "ios".equalsIgnoreCase(deviceType))) {
				userType = "mobile";
			}
			
			Boolean changePassword = IAMUserUtil.changePassword(getPassword(), getRawPassword(), user.getUid(), AccountUtil.getCurrentOrg().getOrgId(), userType);
			if (changePassword != null && changePassword) {
				setJsonresponse("messages", "Password changed successfully");
				setJsonresponse("status", "success");
				return SUCCESS;
			}
			setJsonresponse("messages", "Current Password is incorrect");
			setJsonresponse("status", "failure");
			return ERROR;
		}
		catch(SecurityPolicyException secEx) {
			setJsonresponse("status", "failure");
			setJsonresponse("messages", secEx.getMessage());
			return ERROR;
		}
		catch(Exception e) {
			Throwable cause = e.getCause();
			if (cause instanceof SecurityPolicyException) {
				setJsonresponse("status", "failure");
				setJsonresponse("messages", cause.getMessage());
			} else {
				setJsonresponse("status", "failure");
			}
			return ERROR;
		}
	}

	public String acceptUserInvite() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			if(AccountUtil.getUserBean().acceptInvite(getInviteToken(), getRawPassword())) {
				return SUCCESS;
			}
		} catch(SecurityPolicyException secEx) {
			setJsonresponse("status", "failure");
			setJsonresponse("messages", secEx.getMessage());
			return "invalid";
		} catch(Exception e) {
			setJsonresponse("status", "failure");
			Throwable cause = e.getCause();
			while (cause != null) {
				if (cause instanceof SecurityPolicyException) {
					setJsonresponse("status", "failure");
					setJsonresponse("messages", cause.getMessage());
					break;
				}
				cause = cause.getCause();
			}
		}
		return "invalid";
	}

	public static String cryptWithMD5(String pass) {
		return PasswordHashUtil.cryptWithMD5(pass);
	}

	public String generateAuthToken() {
		LOGGER.info("generateAuthToken() : username :" + getUsername());
		try {
			String token = IAMUserUtil.generateAuthToken(getUsername(), getPassword(), AccountUtil.getDefaultAppDomain());
			if (token != null) {
				LOGGER.info("Response token is " + token);
				setJsonresponse("authtoken", token);
				setJsonresponse("username", getUsername());
			} else {
				setJsonresponse("message", "Invalid username / password");
			}
			return SUCCESS;
		} catch (Exception e) {
			setJsonresponse("message", "Invalid username / password");
			return ERROR;
		}
	}

	public String generatePortalAuthToken() {
		LOGGER.info("generatePortalAuthToken() : username :" + getUsername());
		HttpServletRequest request = ServletActionContext.getRequest(); 
		
		try {
			String token = IAMUserUtil.generateAuthToken(getUsername(), getPassword(), request.getServerName());
			if (token != null) {
				LOGGER.info("Response token is " + token);
				setJsonresponse("authtoken", token);
				setJsonresponse("username", getUsername());
			} else {
				setJsonresponse("message", "Invalid username / password");
			}
			return SUCCESS;
		} catch (Exception e) {
			setJsonresponse("message", "Invalid username / password");
			return ERROR;
		}

	}

	public String signupPortalUser() throws Exception {
		LOGGER.info("signupUser() : username :" + getUsername() + ", password :" + password + ", email : "
				+ getEmailaddress() );
		HttpServletRequest req = ServletActionContext.getRequest();
		
		return addPortalUser(getUsername(), getPassword(), getEmailaddress(), FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
	}

	private String addPortalUser(String username, String password, String emailaddress, String linkname)
			throws Exception {
		LOGGER.info("### addPortalUser() :" + emailaddress);

		HttpServletRequest req = ServletActionContext.getRequest();
		User user = new User();
		user.setName(username);
		user.setEmail(emailaddress);
		user.setPassword(password);
		
		//if(emailVerificationNeeded) {
			user.setUserVerified(false);
			user.setInviteAcceptStatus(false);
			user.setInvitedTime(System.currentTimeMillis());
//		}
//		else {
//			user.setUserVerified(true);
//			user.setInviteAcceptStatus(true);
//			user.setInvitedTime(System.currentTimeMillis());
//		}

		PortalInfoAction authAction = new PortalInfoAction();
		authAction.getPortalInfo();
		PortalInfoContext portalInfo = authAction.getProtalInfo();

		boolean opensignup = portalInfo.isSignup_allowed(); // SIGNUP_ALLOWED
		boolean anydomain_allowedforsignup = portalInfo.is_anyDomain_allowed();

		if (!opensignup) {
			setJsonresponse("message", "Signup not allowed for this portal");
			setResponseCode(1);
			return SUCCESS;
		}

		boolean whitelisteddomain = false;

		if (opensignup && !anydomain_allowedforsignup) {
			String domains = portalInfo.getWhiteListed_domains();

			ArrayList<String> items = new ArrayList<String>();
			if (domains != null) {
				items = new ArrayList<>(Arrays.asList(domains.split(",")));
			}
			for (String item : items) {
				if (emailaddress.endsWith(item.trim())) {
					whitelisteddomain = true;
					break;
				}
			}
			if (!anydomain_allowedforsignup && !whitelisteddomain) {
				setJsonresponse("message", "Only whitelisted domains allowed");
				setResponseCode(1);
				return SUCCESS;
			}

		}
		if (anydomain_allowedforsignup || opensignup || whitelisteddomain) {
			try {
				long applicationId = ApplicationApi.getApplicationIdForLinkName(linkname);
				user.setApplicationId(applicationId);
				AppDomain appDomainObj = ApplicationApi.getAppDomainForApplication(applicationId);
				user.setAppDomain(appDomainObj);

				AccountUtil.getTransactionalUserBean().inviteRequester(AccountUtil.getCurrentOrg().getId(), user, false, true, appDomainObj.getIdentifier(), true, true);

				LOGGER.info("user signup done " + user);
			} catch (InvocationTargetException ie) {
				Throwable e = ie.getTargetException();
				if (e.getMessage() != null && e.getMessage().equals("Email Already Registered")) {
					setJsonresponse("message", "Email Already Registered");
					setResponseCode(1);
					return SUCCESS;
				} else {
					throw ie;
				}
			}
			setJsonresponse("message", "success");
			return SUCCESS;
		}
		return ERROR;
	}

	public String changePortalPassword() {
		String result = changePassword();
		return result;
	}

	public String v2apiLogout() throws Exception {
		apiLogout();
		return SUCCESS;
	}

	public String apiLogout() throws Exception {

		
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();

		// end user session
		try {
			
			String facilioToken = null;
			String appDomain = request.getServerName();
			boolean portalUser = false;
			AppDomain appdomainObj = IAMAppUtil.getAppDomain(appDomain);
			if(appdomainObj != null && appdomainObj.getAppDomainTypeEnum() != AppDomainType.FACILIO) {
				facilioToken = FacilioCookie.getUserCookie(request, "fc.idToken.facilioportal");
				portalUser = true;
			}
			else {
				facilioToken = FacilioCookie.getUserCookie(request, "fc.idToken.facilio");
			}
			if (facilioToken != null) {
				User currentUser = AccountUtil.getCurrentUser();
				if (currentUser != null) {
					if(IAMUserUtil.logOut(currentUser.getUid(), facilioToken
							)) {
						HttpSession session = request.getSession();
						session.invalidate();
						FacilioCookie.eraseUserCookie(request, response, "fc.idToken.facilio", null);
						FacilioCookie.eraseUserCookie(request, response,"fc.idToken.proxy",null);
						if(portalUser) {
							FacilioCookie.eraseUserCookie(request, response, "fc.idToken.facilioportal", null);
						}
						FacilioCookie.eraseUserCookie(request, response, "fc.currentSite", null);
						FacilioCookie.eraseUserCookie(request, response, "fc.switchValue", null);
						FacilioCookie.eraseUserCookie(request, response, "fc.currentOrg", null);
						
					}
				}
			}
		} catch (Exception e) {
			return ERROR;
		}

		return SUCCESS;
	}

	public String sendResetPassword() throws Exception {
		long appId = getApplicationId();
		AppDomain appDomainForApplication = ApplicationApi.getAppDomainForApplication(appId);
		Map<String, Object> userMap = IAMUserUtil.getUserForUsername(getEmailaddress(), -1, appDomainForApplication.getIdentifier());
		User user = FieldUtil.getAsBeanFromMap(userMap, User.class);
		AccountUtil.getUserBean().sendResetPasswordLinkv2(user, appDomainForApplication.getDomain());
		jsonresponse.put("message", "success");
		return SUCCESS;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	private String service;

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getTotp() {
		return totp;
	}

	public void setTotp(String totp) {
		this.totp = totp;
	}

	public String getMfaConfigToken() {
		return mfaConfigToken;
	}

	public void setMfaConfigToken(String mfaConfigToken) {
		this.mfaConfigToken = mfaConfigToken;
	}

	public String fetchLogo() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		AppDomain appDomain = IAMAppUtil.getAppDomain(request.getServerName());
		String logoURL = null;
		String domain = null;
		Long orgId = null;
		if (appDomain != null && appDomain.getOrgId() > 0) {
			Organization org = IAMOrgUtil.getOrg(appDomain.getOrgId());
			if (org != null) {
				logoURL = org.getLogoUrl();
				domain = org.getDomain();
				orgId = org.getOrgId();
			}
		}
		setResult("url", logoURL);
		setResult("domain",domain);
		setResult("id",orgId);
		return SUCCESS;
	}

	public String getLogo() throws Exception {
		String domain = getDomain();
		Organization org = IAMOrgUtil.getOrg(domain);
		String logoURL = null;
		if (org != null) {
			logoURL = org.getLogoUrl();
			if(logoURL != null && !logoURL.isEmpty()){
				CloseableHttpClient c = HttpClients.createDefault();
				try{
					CloseableHttpResponse resp = c.execute(new HttpGet(logoURL));
					HttpEntity entity = resp.getEntity();
					setContentType("application/octet-image");
					if(entity != null){
						resultStream = entity.getContent();
					}
				} catch (Exception e){
					throw e;
				}
				return SUCCESS;
			}
		}
		return ERROR;
	}
	@Getter
	@Setter
	private InputStream resultStream;

	public String fetchPrivacyPolicy() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		AppDomain appDomain = IAMAppUtil.getAppDomain(request.getServerName());
		AppDomainLink domainLink = null;
		if (appDomain != null) {
			domainLink = IAMOrgUtil.getDomainLink(appDomain, AppDomainLink.LinkType.PRIVACY_POLICY);
		}
		setResult("privacyPolicy", domainLink);
		return SUCCESS;
	}

	public String fetchTermsOfService() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		AppDomain appDomain = IAMAppUtil.getAppDomain(request.getServerName());
		AppDomainLink domainLink = null;
		if (appDomain != null) {
			domainLink = IAMOrgUtil.getDomainLink(appDomain, AppDomainLink.LinkType.TERMS_OF_USE);
		}
		setResult("termsOfService", domainLink);
		return SUCCESS;
	}

	private String linkType;
	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}
	public String getLinkType() {
		return this.linkType;
	}
	public String fetchDomainLink() throws Exception {
		AppDomainLink.LinkType domainLinkType = null;
		if (getLinkType() != null) {
			domainLinkType = AppDomainLink.LinkType.valueOf(getLinkType());
		}
		if (domainLinkType == null) {
			setResponseCode(1);
			setResult("message", "linkType param missing.");
			return SUCCESS;
		}

		HttpServletRequest request = ServletActionContext.getRequest();
		AppDomain appDomain = IAMAppUtil.getAppDomain(request.getServerName());
		AppDomainLink domainLink = null;
		if (appDomain != null) {
			domainLink = IAMOrgUtil.getDomainLink(appDomain, domainLinkType);
		}
		setResult("domainlink", domainLink);
		return SUCCESS;
	}
	private void addDeviceAuthCookies(String authtoken, HttpServletRequest request) throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();
		Cookie cookie = new Cookie("fc.deviceTokenNew", authtoken);
		setCookieProperties(cookie,true);
		response.addCookie(cookie);
	}

	private void setLookupTypeForMobileProxy(AppDomain appdomainObj)throws Exception {
		if (appdomainObj.getAppDomainTypeEnum() == AppDomainType.FACILIO) {
			setLookUpType("workq");
		} else if (appdomainObj.getAppDomainTypeEnum() == AppDomainType.SERVICE_PORTAL) {
			setLookUpType("service");
		} else if (appdomainObj.getAppDomainTypeEnum() == AppDomainType.TENANT_PORTAL) {
			setLookUpType("tenant");
		} else if (appdomainObj.getAppDomainTypeEnum() == AppDomainType.VENDOR_PORTAL) {
			setLookUpType("vendor");
		}
	}
}
