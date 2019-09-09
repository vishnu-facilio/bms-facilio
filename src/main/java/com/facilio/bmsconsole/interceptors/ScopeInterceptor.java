
package com.facilio.bmsconsole.interceptors;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.facilio.aws.util.FacilioProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Parameter;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.PortalInfoContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.iam.accounts.exceptions.AccountException;
import com.facilio.iam.accounts.exceptions.AccountException.ErrorCode;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class ScopeInterceptor extends AbstractInterceptor {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(ScopeInterceptor.class);
			
	@Override
	public void init() {
		super.init();
	}

	@Override
	public String intercept(ActionInvocation arg0) throws Exception {
		
		HttpServletRequest request = ServletActionContext.getRequest();
		IAMAccount iamAccount = (IAMAccount) request.getAttribute("iamAccount");
		
		if (iamAccount != null) {
			User user = null;
			if(iamAccount.getOrg() == null) {
				LOGGER.log(Level.FATAL, "User seems to have been deactivated");
				return Action.LOGIN;
			}
			Account tempAccount = new Account(iamAccount.getOrg(), user);
			AccountUtil.setCurrentAccount(tempAccount);
		
			if(iamAccount.getUser() != null) {
				user = AccountUtil.getUserBean().getUser(iamAccount.getOrg().getOrgId(), iamAccount.getUser().getUid());
				if (user == null) {
					Organization org = AccountUtil.getUserBean().getDefaultOrg(iamAccount.getUser().getUid());
					if(org != null) {
						user = AccountUtil.getUserBean().getUser(org.getOrgId(), iamAccount.getUser().getUid());
						if(user == null) {
							throw new AccountException(ErrorCode.USER_DOESNT_EXIST_IN_ORG, "User doesn't exists in org");
						}
						iamAccount.setOrg(org);
					}
					else {
						throw new AccountException(ErrorCode.USER_DOESNT_EXIST_IN_ORG, "User doesn't exists in org");
					}
				}
			}
			Account account = new Account(iamAccount.getOrg(), user);
			AccountUtil.cleanCurrentAccount();
			AccountUtil.setCurrentAccount(account);
		}

		HttpServletResponse response = ServletActionContext.getResponse();
		try {
			Account currentAccount = AccountUtil.getCurrentAccount();
			if (currentAccount != null) {
				if(request.getAttribute("isPortal") != null && (Boolean)request.getAttribute("isPortal")) {
					PortalInfoContext portalInfo = AccountUtil.getOrgBean().getPortalInfo(currentAccount.getOrg().getOrgId(), false);
					currentAccount.getOrg().setPortalId(portalInfo.getPortalId());
				}
				else {
					if(currentAccount.getUser() != null) {
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
					}
				}
			} else {
				String authRequired = ActionContext.getContext().getParameters().get("auth").getValue();
				if (authRequired == null || "".equalsIgnoreCase(authRequired.trim()) || "true".equalsIgnoreCase(authRequired)) {
					return Action.LOGIN;
				}
			}

			if (request.getRequestURL().indexOf("/admin") != -1) {
				if (currentAccount != null) {
					String useremail = currentAccount.getUser().getEmail();
					if (! useremail.endsWith(FacilioProperties.getConfig("admin.domain"))) {
						LOGGER.log(Level.FATAL, "you are not allowed to access this page from");
						return Action.LOGIN;
					}
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.FATAL, "error in auth interceptor", e);
			return Action.LOGIN;
		}

		try {
			AccountUtil.setReqUri(request.getRequestURI());
            AccountUtil.setRequestParams(request.getParameterMap());
			
            return arg0.invoke();
		} catch (Exception e) {
			System.out.println("exception code 154");

			LOGGER.log(Level.FATAL, "error thrown from action class", e);
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
	
	 
}