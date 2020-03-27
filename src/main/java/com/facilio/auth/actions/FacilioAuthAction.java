package com.facilio.auth.actions;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.util.StringUtils;
import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.aws.util.FederatedIdentityUtil;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.actions.PortalInfoAction;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.PortalInfoContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.exceptions.AccountException;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.modules.FieldUtil;
import com.facilio.util.AuthenticationUtil;
import com.opensymphony.xwork2.ActionContext;

import io.sentry.SentryClient;
import io.sentry.SentryClientFactory;
import io.sentry.context.Context;
import io.sentry.event.UserBuilder;

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = cryptWithMD5(password);
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
		this.newPassword = cryptWithMD5(newPassword);
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
		signupInfo.put("servername", request.getServerName());
		signupInfo.put("password", password);
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

	public String validateLoginv3() {

		boolean portalUser = false;
		HttpServletRequest request = ServletActionContext.getRequest();
		

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
				if (!StringUtils.isNullOrEmpty(deviceType)
						&& ("android".equalsIgnoreCase(deviceType) || "ios".equalsIgnoreCase(deviceType))) {
					userType = "mobile";
				}

				LOGGER.info("validateLogin() : appdomainName : " + appDomainName);
				
				authtoken = IAMUserUtil.verifyLoginPasswordv3(getUsername(), getPassword(), request.getServerName(), userAgent, userType,
							ipAddress);
				setJsonresponse("token", authtoken);
				setJsonresponse("username", getUsername());

				addAuthCookies(authtoken, portalUser, false, request);

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
				return ERROR;
			}
			return SUCCESS;
		}
		setJsonresponse("message", "Invalid username or password");
		return ERROR;
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
					if(StringUtils.isNullOrEmpty(awsDomain)) {
						awsDomain = "facilio";
					}
					if(!domainNameArray[1].equals(awsDomain) ) {
						domainName = domainNameArray[0];
					}
				}
				
				ipAddress = (ipAddress == null || "".equals(ipAddress.trim())) ? request.getRemoteAddr() : ipAddress;
	            String userType = "web";
				String deviceType = request.getHeader("X-Device-Type");
				if (!StringUtils.isNullOrEmpty(deviceType)
						&& ("android".equalsIgnoreCase(deviceType) || "ios".equalsIgnoreCase(deviceType))) {
					userType = "mobile";
				}
	
				LOGGER.info("validateLogin() : domainName : " + domainName);
				
				authtoken = IAMUserUtil.verifyLoginWithoutPassword(email, userAgent, userType, ipAddress, request.getServerName());
				setResult("token", authtoken);
				setResult("username", email);
	
				addAuthCookies(authtoken, portalUser, false, request);
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
		}
		return SUCCESS;
	}

	public String loadWebView() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		String authtoken = request.getParameter("authtoken");
		String serviceurl = request.getParameter("serviceurl");
		String isPortal = request.getParameter("isPortal");
		String isDevice = request.getParameter("isDevice");
		String currentOrg = request.getParameter("currentOrg");
		boolean isPotalUser = false;
		boolean isDeviceUser = false;
		if(org.apache.commons.lang3.StringUtils.isNotEmpty(isPortal) && isPortal.contentEquals("true")) {
			isPotalUser = true;
		}
		if(org.apache.commons.lang3.StringUtils.isNotEmpty(isDevice) && isDevice.contentEquals("true")) {
			isDeviceUser = true;
		}
		
		addAuthCookies(authtoken, isPotalUser, isDeviceUser, request);

		Cookie token = new Cookie("fc.idToken", "facilio");
		token.setMaxAge(60 * 60 * 24 * 30); // Make the cookie last a year
		token.setPath("/");
		token.setHttpOnly(false);
		if (!(FacilioProperties.isDevelopment() || FacilioProperties.isOnpremise())) {
			token.setSecure(true);
		}
		token.setDomain(request.getServerName());
		response.addCookie(token);
		
		if (org.apache.commons.lang3.StringUtils.isNotEmpty(currentOrg)) {
			AuthenticationUtil.addDomainCookie(currentOrg, response);
		}

		try {
			response.sendRedirect(serviceurl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	private void addAuthCookies(String authtoken, boolean portalUser, boolean isDeviceUser, HttpServletRequest request) {
		HttpServletResponse response = ServletActionContext.getResponse();
		Cookie cookie = new Cookie("fc.idToken.facilio", authtoken);
		if (portalUser) {
			cookie = new Cookie("fc.idToken.facilioportal", authtoken);
		}
		if (isDeviceUser) {
			cookie = new Cookie("fc.deviceTokenNew", authtoken);
		}
		String parentdomain = request.getServerName().replaceAll("app.", "").replaceAll("demo.", "");
		cookie.setMaxAge(60 * 60 * 24 * 30); // Make the cookie last a year
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		if (!(FacilioProperties.isDevelopment() || FacilioProperties.isOnpremise())) {
			cookie.setSecure(true);
		}
		cookie.setDomain(parentdomain);
		response.addCookie(cookie);

		Cookie authmodel = new Cookie("fc.authtype", "facilio");
		authmodel.setMaxAge(60 * 60 * 24 * 30); // Make the cookie last a year
		authmodel.setPath("/");
		authmodel.setHttpOnly(false);
		if (!(FacilioProperties.isDevelopment() || FacilioProperties.isOnpremise())) {
			authmodel.setSecure(true);
		}

		authmodel.setDomain(parentdomain);
		response.addCookie(authmodel);
	}

	public String validateInviteLink() throws Exception {

		JSONObject invitation = new JSONObject();
		HttpServletRequest request = ServletActionContext.getRequest();

		User user = AccountUtil.getUserBean().validateUserInvite(getInviteToken());
		if(user != null) {
			User us = AccountUtil.getUserBean(user.getOrgId()).getUser(-1, user.getOrgId(), user.getUid());
			Organization org = AccountUtil.getOrgBean().getOrg(user.getOrgId());
			AccountUtil.cleanCurrentAccount();
			AccountUtil.setCurrentAccount(new Account(org, us));
		}
		
		if (user != null) {
			Organization org = AccountUtil.getOrgBean().getOrg(user.getOrgId());
			invitation.put("email", user.getEmail());
			invitation.put("orgname", org.getName());
			invitation.put("userid", user.getOuid());
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
		context.setUser(new UserBuilder().setEmail("issues@facilio.com").build());
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

	public String resetPassword() throws Exception {
		JSONObject invitation = new JSONObject();
		HttpServletRequest request = ServletActionContext.getRequest();

		if (getInviteToken() != null) {
			IAMUser iamUser = IAMUserUtil.resetPassword(getInviteToken(), getPassword());
			User user = new User(iamUser);
			if (user.getUid() > 0) {
				invitation.put("status", "success");
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
			if (user != null) {
				AccountUtil.getUserBean().sendResetPasswordLinkv2(user, request.getServerName());
				invitation.put("status", "success");
			} else {
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
			if (!StringUtils.isNullOrEmpty(deviceType)
					&& ("android".equalsIgnoreCase(deviceType) || "ios".equalsIgnoreCase(deviceType))) {
				userType = "mobile";
			}
			
			Boolean changePassword = IAMUserUtil.changePassword(getPassword(), getNewPassword(), user.getUid(), AccountUtil.getCurrentOrg().getOrgId(), userType);
			if (changePassword != null && changePassword) {
				setJsonresponse("message", "Password changed successfully");
				setJsonresponse("status", "success");
				return SUCCESS;
			}
			setJsonresponse("message", "Current Password is incorrect");
			setJsonresponse("status", "failure");
			return ERROR;
		}
		catch(Exception e) {
			setJsonresponse("status", "failure");
			return ERROR;
			
		}
	}

	public String acceptUserInvite() throws Exception {
		
		HttpServletRequest request = ServletActionContext.getRequest(); 
		
		if(AccountUtil.getUserBean().acceptInvite(getInviteToken(), getPassword())) {
			return SUCCESS;
		}
		return ERROR;
	}

	public static String cryptWithMD5(String pass) {
		try {
			md = MessageDigest.getInstance("MD5");
			byte[] passBytes = pass.getBytes();
			md.reset();
			byte[] digested = md.digest(passBytes);
			StringBuilder sb = new StringBuilder();
			for (byte aDigested : digested) {
				sb.append(Integer.toHexString(0xff & aDigested));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException ex) {
			LOGGER.log(Level.INFO, "Exception ", ex);
		}
		return null;
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
		
		return addPortalUser(getUsername(), getPassword(), getEmailaddress(), req.getServerName());
	}

	private String addPortalUser(String username, String password, String emailaddress, String appDomain)
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
				AppDomain appDomainObj = IAMAppUtil.getAppDomain(appDomain);
				if(appDomainObj == null) {
					setJsonresponse("message", "Invalid app domain");
					return ERROR;
				}
				long applicationId = ApplicationApi.getApplicationIdForApp(appDomainObj);
				user.setApplicationId(applicationId);
				user.setAppDomain(appDomainObj);
				
				AccountUtil.getTransactionalUserBean().inviteRequester(AccountUtil.getCurrentOrg().getId(), user, true, true, appDomainObj.getIdentifier(), true);
				
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
			AppDomain appdomainObj = IAMAppUtil.getAppDomain(appDomain);
			if(appdomainObj != null && appdomainObj.getAppDomainTypeEnum() != AppDomainType.FACILIO) {
				facilioToken = FacilioCookie.getUserCookie(request, "fc.idToken.facilioportal");
			}
			else {
				facilioToken = FacilioCookie.getUserCookie(request, "fc.idToken.facilio");
			}
			if (facilioToken != null) {
				User currentUser = AccountUtil.getCurrentUser();
				if (currentUser != null) {
					IAMUserUtil.logOut(currentUser.getUid(), facilioToken
							);
				}
			}
		} catch (Exception e) {
			return ERROR;
		}

		boolean portalUser = false;
		String appDomain = request.getServerName();
		AppDomain appdomainObj = IAMAppUtil.getAppDomain(appDomain);
		if(appdomainObj != null && appdomainObj.getAppDomainTypeEnum() != AppDomainType.FACILIO) {
			portalUser = true;
		}
	
		
		HttpSession session = request.getSession();
		session.invalidate();
		String parentdomain = request.getServerName().replaceAll("app.", "").replaceAll("demo.", "");
		FacilioCookie.eraseUserCookie(request, response, portalUser ? "fc.idToken.facilioportal" : "fc.idToken.facilio", parentdomain);
		FacilioCookie.eraseUserCookie(request, response, "fc.authtype", null);
		FacilioCookie.eraseUserCookie(request, response, "fc.currentSite", null);
		FacilioCookie.eraseUserCookie(request, response, "fc.currentOrg", null);
		return SUCCESS;
	}

}
