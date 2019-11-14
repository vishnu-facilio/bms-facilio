
package com.facilio.bmsconsole.interceptors;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.audit.AuditData;
import com.facilio.audit.DBAudit;
import com.facilio.audit.FacilioAudit;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ConnectedDeviceContext;
import com.facilio.bmsconsole.context.PortalInfoContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.iam.accounts.exceptions.AccountException;
import com.facilio.iam.accounts.exceptions.AccountException.ErrorCode;
import com.facilio.screen.context.RemoteScreenContext;
import com.facilio.server.ServerInfo;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Parameter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

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
			if(request.getAttribute("remoteScreen") != null && request.getAttribute("remoteScreen") instanceof RemoteScreenContext) {
				RemoteScreenContext remoteScreenContext = (RemoteScreenContext) request.getAttribute("remoteScreen");
				if(iamAccount.getOrg() != null) {
					Account tempAccount = new Account(iamAccount.getOrg(), null);
					tempAccount.setUserSessionId(iamAccount.getUserSessionId());
					AccountUtil.setCurrentAccount(tempAccount);
					User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(iamAccount.getOrg().getOrgId());
					Account account = new Account(iamAccount.getOrg(), superAdmin);
					account.setRemoteScreen(remoteScreenContext);
					account.setUserSessionId(iamAccount.getUserSessionId());
					
					AccountUtil.cleanCurrentAccount();
					AccountUtil.setCurrentAccount(account);
				}
				else {
					LOGGER.log(Level.FATAL, "Invalid remote Screen");
					return Action.LOGIN;
				}
			}
			else if(request.getAttribute("device") != null && request.getAttribute("device") instanceof ConnectedDeviceContext) {
				ConnectedDeviceContext connectedDevice = (ConnectedDeviceContext) request.getAttribute("device");
				if(iamAccount.getOrg() != null) {
					Account tempAccount = new Account(iamAccount.getOrg(), null);
					AccountUtil.setCurrentAccount(tempAccount);
					User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(iamAccount.getOrg().getOrgId());
					Account account = new Account(iamAccount.getOrg(), superAdmin);
					account.setConnectedDevice(connectedDevice);
					account.setUserSessionId(iamAccount.getUserSessionId());
					
					AccountUtil.cleanCurrentAccount();
					AccountUtil.setCurrentAccount(account);
				}
				else {
					LOGGER.log(Level.FATAL, "Invalid Device screen");
					return Action.LOGIN;
				}
			}
			else {
				User user = null;
				if(iamAccount.getOrg() == null) {
					LOGGER.log(Level.FATAL, "User seems to have been deactivated");
					return Action.LOGIN;
				}
				Account tempAccount = new Account(iamAccount.getOrg(), user);
				AccountUtil.setCurrentAccount(tempAccount);
			
				if(iamAccount.getUser() != null) {
					LOGGER.log(Level.FATAL, "orgid: " + iamAccount.getOrg().getOrgId() + " : " + iamAccount.getUser().getUid());
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
				account.setUserSessionId(iamAccount.getUserSessionId());
				AccountUtil.cleanCurrentAccount();
				AccountUtil.setCurrentAccount(account);
			}
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
            if(true){  // make false to intercept
				return arg0.invoke();
			} else {
				AuditData data = null;
				FacilioAudit audit = new DBAudit();
				int status = 200;
				try {
					data = getAuditData(arg0);
					if(data != null) {
						data.setId(audit.add(data));
					}
					return arg0.invoke();
				} catch (Exception e) {
					status = 500;
					LOGGER.info("Exception from action classs " + e.getMessage());
					throw e;
				} finally {
					if(data != null) {
						data.setEndTime(System.currentTimeMillis());
						data.setStatus(status);
						data.setQueryCount(AccountUtil.getCurrentAccount().getTotalQueries());
						audit.update(data);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("exception code 154");

			LOGGER.log(Level.FATAL, "error thrown from action class", e);
			throw e;
		}
	}

	private AuditData getAuditData(ActionInvocation actionInvocation) {
		if(AccountUtil.getCurrentUser() != null) {
			AuditData data = new AuditData();
			ActionProxy proxy = actionInvocation.getProxy();
			data.setOrgId(AccountUtil.getCurrentUser().getOrgId());
			data.setUserId(AccountUtil.getCurrentUser().getUid());
			data.setSessionId(AccountUtil.getCurrentUserSessionId());
			data.setOrgUserId(AccountUtil.getCurrentUser().getIamOrgUserId());
			data.setAction(proxy.getActionName());
			data.setMethod(proxy.getMethod());
			data.setModule(proxy.getConfig().getPackageName());
			data.setStartTime(System.currentTimeMillis());
			data.setServer(ServerInfo.getHostname());
			data.setThread(Long.parseLong(Thread.currentThread().getName()));
			return data;
		}
		return null;
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