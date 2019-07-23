
package com.facilio.bmsconsole.interceptors;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Parameter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.facilio.accounts.dto.Account;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.fw.auth.LoginUtil;
import com.facilio.screen.context.RemoteScreenContext;
import com.facilio.screen.util.ScreenUtil;
import com.facilio.util.AuthenticationUtil;
import com.iam.accounts.util.AuthUtill;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class AuthInterceptor extends AbstractInterceptor {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(AuthInterceptor.class.getName());

	@Override
	public void init() {
		super.init();
	}

	@Override
	public String intercept(ActionInvocation arg0) throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		try {

			if (getPermalinkToken(request) != null) {
				String token = getPermalinkToken(request);
				
				List<String> urlsToValidate = new ArrayList<>();
				
				String reqURL = request.getRequestURI();
				urlsToValidate.add(reqURL);
				
				String referrer = request.getHeader(HttpHeaders.REFERER);
				if (referrer != null && !"".equals(referrer.trim())) {
					URL url = new URL(referrer);
					urlsToValidate.add(url.getPath());
				}
				
				if (AccountUtil.getUserBean().verifyPermalinkForURL(token, urlsToValidate)) {
					DecodedJWT decodedjwt = AuthUtill.validateJWT(token, "auth0");

					String[] tokens = null;
					if (decodedjwt.getSubject().contains(AuthUtill.JWT_DELIMITER)) {
						tokens = decodedjwt.getSubject().split(AuthUtill.JWT_DELIMITER)[0].split("-");
					}
					else {
						tokens = decodedjwt.getSubject().split("_")[0].split("-");
					}
					long orgId = Long.parseLong(tokens[0]);
					long ouid = Long.parseLong(tokens[1]);
					
					Account currentAccount = new Account(AccountUtil.getOrgBean().getOrg(orgId), AccountUtil.getUserBean().getUserInternal(ouid, false));
					
					AccountUtil.cleanCurrentAccount();
					AccountUtil.setCurrentAccount(currentAccount);
					request.setAttribute("ORGID", currentAccount.getOrg().getOrgId());
					request.setAttribute("USERID", currentAccount.getUser().getOuid());
					
					try {
						AccountUtil.setReqUri(request.getRequestURI());
						AccountUtil.setRequestParams(request.getParameterMap());
						return arg0.invoke();
					} catch (Exception e) {
						System.out.println("exception code 154");

						LOGGER.log(Level.SEVERE, "error thrown from action class", e);
						throw e;
					}
				}
				else {
					return Action.ERROR;
				}
			}
			else if (!isRemoteScreenMode(request)) {
				String currentOrgDomain = FacilioCookie.getUserCookie(request, "fc.currentOrg");
				if (currentOrgDomain == null) {
					currentOrgDomain = request.getHeader("X-Current-Org"); 
				}
			
				String email = AuthenticationUtil.validateToken(request, false);
				
				Account currentAccount = null;
				currentAccount = LoginUtil.getAccount(email, false);
				
				if (currentAccount != null) {
					AccountUtil.cleanCurrentAccount();
					AccountUtil.setCurrentAccount(currentAccount);

					List<Long> accessibleSpace = null;
					if (currentAccount.getUser() != null) {
						accessibleSpace = currentAccount.getUser().getAccessibleSpace();
					}
					if (currentAccount != null) {
						List<Long> sites = CommonCommandUtil.getMySiteIds();
						if (sites != null && sites.size() == 1) {
							currentAccount.setCurrentSiteId(sites.get(0));
							if (accessibleSpace == null) {
								accessibleSpace = new ArrayList<>();
								accessibleSpace.add(sites.get(0));
								currentAccount.getUser().setAccessibleSpace(accessibleSpace);
							}
						} else {
							String currentSite = request.getHeader("X-current-site");
							long currentSiteId = -1;
							if (currentSite != null && !currentSite.isEmpty()) {
								try {
									currentSiteId = Long.valueOf(currentSite);
								} catch (NumberFormatException ex) {
									// ignore if header value is wrong
								}
								if (currentSiteId != -1 && sites != null && !sites.isEmpty()) {
									boolean found = false;
									for (long id: sites) {
										if (id == currentSiteId) {
											found = true;
											break;
										}
									}
									if (!found) {
										throw new IllegalArgumentException("Invalid Site.");
									}
								}
								currentAccount.setCurrentSiteId(currentSiteId);
								if (currentSiteId != -1 && accessibleSpace == null) {
									accessibleSpace = new ArrayList<>();
									accessibleSpace.add(currentSiteId);
									currentAccount.getUser().setAccessibleSpace(accessibleSpace);
								}
							}
						}
					}
					request.setAttribute("ORGID", currentAccount.getOrg().getOrgId());
					request.setAttribute("USERID", currentAccount.getUser().getOuid());
										

					String timezoneVar = null;
					if (AccountUtil.getCurrentAccount().getCurrentSiteId() > 0)
					{
						SiteContext site = SpaceAPI.getSiteSpace(AccountUtil.getCurrentAccount().getCurrentSiteId());
						if(site != null) {
							timezoneVar = site.getTimeZone();
						}
					}
					if (StringUtils.isEmpty(timezoneVar))
					{
						timezoneVar = AccountUtil.getCurrentOrg().getTimezone();
					}
					AccountUtil.setTimeZone(timezoneVar);
					
					
					Parameter action = ActionContext.getContext().getParameters().get("permission");
					Parameter moduleName = ActionContext.getContext().getParameters().get("moduleName");
					if (action != null && action.getValue() != null && moduleName != null && moduleName.getValue() != null && !isAuthorizedAccess(moduleName.getValue(), action.getValue())) {
						return "unauthorized";
					}

					String lang = currentAccount.getUser().getLanguage();
					Locale localeObj = null;
					if (lang == null || lang.trim().isEmpty()) {
						localeObj = request.getLocale();
					} else {
						localeObj = new Locale(lang);
					}

					String timezone = currentAccount.getUser().getTimezone();
					TimeZone timezoneObj = null;
					if (timezone == null || timezone.trim().isEmpty()) {
						Calendar calendar = Calendar.getInstance(localeObj);
						timezoneObj = calendar.getTimeZone();
					} else {
						timezoneObj = TimeZone.getTimeZone(timezone);
					}
					ActionContext.getContext().getSession().put("TIMEZONE", timezoneObj);
				} else {
					String authRequired = ActionContext.getContext().getParameters().get("auth").getValue();
					if (authRequired == null || "".equalsIgnoreCase(authRequired.trim()) || "true".equalsIgnoreCase(authRequired)) {
						return Action.LOGIN;
					}
				}

				if (request.getRequestURL().indexOf("/admin") != -1) {
					if (currentAccount != null) {
						String useremail = currentAccount.getUser().getEmail();
						if (! useremail.endsWith(AwsUtil.getConfig("admin.domain"))) {
							LOGGER.log(Level.SEVERE, "you are not allowed to access this page from");
							return Action.LOGIN;
						}
					}
				}
			}
			else {
				boolean authStatus = handleRemoteScreenAuth(request);
				if (!authStatus) {
					LOGGER.log(Level.SEVERE, "you are not allowed to access this page from remote screen.");
					return Action.ERROR;
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "error in auth interceptor", e);
			return Action.LOGIN;
		}

		try {
			AccountUtil.setReqUri(request.getRequestURI());
            AccountUtil.setRequestParams(request.getParameterMap());
			return arg0.invoke();
		} catch (Exception e) {
			System.out.println("exception code 154");

			LOGGER.log(Level.SEVERE, "error thrown from action class", e);
			throw e;
		}
	}

	private boolean isAuthorizedAccess(String moduleName, String action) throws Exception {
		
		if (action == null || "".equals(action.trim())) {
			return true;
		}

		if(AccountUtil.getCurrentUser() == null) {
		    return false;
        }

		return PermissionUtil.currentUserHasPermission(moduleName, action);
	}
	
	private boolean isRemoteScreenMode(HttpServletRequest request) {
		String remoteScreenHeader = request.getHeader("X-Remote-Screen");
		String deviceToken = FacilioCookie.getUserCookie(request, "fc.deviceToken");
		
		String facilioToken1 = FacilioCookie.getUserCookie(request, "fc.idToken.facilio");
		String facilioToken2 = FacilioCookie.getUserCookie(request, "fc.idToken.facilioportal");
		
		if ( remoteScreenHeader != null && "true".equalsIgnoreCase(remoteScreenHeader.trim())) {
			return true;
		}
		else if (deviceToken != null && !"".equals(deviceToken) && facilioToken1 == null && facilioToken2 == null) {
			return true;
		}
		return false;
	}
	
	private String getPermalinkToken(HttpServletRequest request) {
		String remoteScreenHeader = request.getHeader("X-Permalink-Token");
		if (remoteScreenHeader != null && !"".equals(remoteScreenHeader.trim())) {
			return remoteScreenHeader;
		}
		return null;
	}
	
	private boolean handleRemoteScreenAuth(HttpServletRequest request) {
		try {
			String authRequired = ActionContext.getContext().getParameters().get("auth").getValue();
			if (authRequired != null && "false".equalsIgnoreCase(authRequired.trim())) {
				return true;
			}
			
			String deviceToken = FacilioCookie.getUserCookie(request, "fc.deviceToken");
			if (deviceToken != null && !"".equals(deviceToken)) {
				long connectedScreenId = Long.parseLong(AuthUtill.validateJWT(deviceToken, "auth0").getSubject().split(AuthUtill.JWT_DELIMITER)[0]);
				RemoteScreenContext remoteScreen = ScreenUtil.getRemoteScreen(connectedScreenId);
				if (remoteScreen != null) {
					Account currentAccount = new Account(AccountUtil.getOrgBean().getOrg(remoteScreen.getOrgId()), AccountUtil.getOrgBean().getSuperAdmin(remoteScreen.getOrgId()));
					currentAccount.setRemoteScreen(remoteScreen);
					
					AccountUtil.cleanCurrentAccount();
					AccountUtil.setCurrentAccount(currentAccount);
					request.setAttribute("ORGID", currentAccount.getOrg().getOrgId());
					request.setAttribute("USERID", currentAccount.getUser().getOuid());
					
					return true;
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception while check authentication from remote-screen.", e);
		}
		return false;
	}
}