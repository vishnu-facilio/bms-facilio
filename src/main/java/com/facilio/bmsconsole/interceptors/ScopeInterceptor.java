
package com.facilio.bmsconsole.interceptors;

import com.facilio.accounts.dto.*;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.audit.AuditData;
import com.facilio.audit.DBAudit;
import com.facilio.audit.FacilioAudit;
import com.facilio.audit.FacilioAuditOrgList;
import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.ConnectedDeviceContext;
import com.facilio.bmsconsole.context.PortalInfoContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.iam.accounts.exceptions.AccountException;
import com.facilio.iam.accounts.exceptions.AccountException.ErrorCode;
import com.facilio.screen.context.RemoteScreenContext;
import com.facilio.server.ServerInfo;
import com.facilio.service.FacilioService;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.commons.collections4.CollectionUtils;
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
    private static HashSet<Long> orgIdList = new HashSet<>();

    @Override
    public void init() {
        super.init();
        orgIdList = FacilioAuditOrgList.getAuditOrgs();
    }

    @Override
    public String intercept(ActionInvocation arg0) throws Exception {
        long startTime = System.currentTimeMillis();
        HttpServletRequest request = ServletActionContext.getRequest();
        IAMAccount iamAccount = (IAMAccount) request.getAttribute("iamAccount");
        if (iamAccount != null) {
            if (request.getAttribute("remoteScreen") != null && request.getAttribute("remoteScreen") instanceof RemoteScreenContext) {
                RemoteScreenContext remoteScreenContext = (RemoteScreenContext) request.getAttribute("remoteScreen");
                if (iamAccount.getOrg() != null) {
                    Account tempAccount = new Account(iamAccount.getOrg(), null);
                    tempAccount.setUserSessionId(iamAccount.getUserSessionId());
                    AccountUtil.setCurrentAccount(tempAccount);
                    User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(iamAccount.getOrg().getOrgId());
                    Account account = new Account(iamAccount.getOrg(), superAdmin);
                    account.setRemoteScreen(remoteScreenContext);
                    account.setUserSessionId(iamAccount.getUserSessionId());

                    AccountUtil.cleanCurrentAccount();
                    AccountUtil.setCurrentAccount(account);
                } else {
                    LOGGER.log(Level.DEBUG, "Invalid remote Screen");
                    return logAndReturn(Action.LOGIN, null, startTime, request);
                }
            } else if (request.getAttribute("device") != null && request.getAttribute("device") instanceof ConnectedDeviceContext) {
                ConnectedDeviceContext connectedDevice = (ConnectedDeviceContext) request.getAttribute("device");
                if (iamAccount.getOrg() != null) {
                    Account tempAccount = new Account(iamAccount.getOrg(), null);
                    AccountUtil.setCurrentAccount(tempAccount);
                    User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(iamAccount.getOrg().getOrgId());
                    Account account = new Account(iamAccount.getOrg(), superAdmin);
                    account.setConnectedDevice(connectedDevice);
                    account.setUserSessionId(iamAccount.getUserSessionId());

                    AccountUtil.cleanCurrentAccount();
                    AccountUtil.setCurrentAccount(account);
                } else {
                    LOGGER.log(Level.DEBUG, "Invalid Device screen");
                    return logAndReturn(Action.LOGIN, null, startTime, request);
                }
            } else {
                User user = null;
                if (iamAccount.getOrg() == null) {
                    LOGGER.log(Level.DEBUG, "User seems to have been deactivated");
                    return logAndReturn(Action.LOGIN, null, startTime, request);
                }
                Account tempAccount = new Account(iamAccount.getOrg(), user);
                AccountUtil.setCurrentAccount(tempAccount);

                long appId = -1;
                try {
                    String appLinkName = null;
                    if (request.getAttribute("facilio.app.name") != null) {
                        appLinkName = (String) request.getAttribute("facilio.app.name");
                    }
                    if (StringUtils.isNotEmpty(appLinkName)) {
                    	
                        ApplicationContext application = ApplicationApi.getApplicationForLinkName(appLinkName);
                        if (application != null) {
                            appId = application.getId();
                        }
                    }
                } catch (Exception e) {
                	
                	if(AccountUtil.getCurrentOrg().getId() == 1l || AccountUtil.getCurrentOrg().getId() == 350l) {
                		LOGGER.log(Level.ERROR, e.getMessage(), e);
                	}
                	
                    throw new AccountException(ErrorCode.INVALID_APP_DOMAIN, "invalid app linkName");
                }
                if (iamAccount.getUser() != null) {
                    String authorisationReqd = ActionContext.getContext().getParameters().get("authorise").getValue();
                	if(StringUtils.isEmpty(authorisationReqd) || authorisationReqd.equals("true")) {
                	    boolean relaxedAccess = false;
                	    if(appId > 0) {
                            user = AccountUtil.getUserBean().getUser(appId, iamAccount.getOrg().getOrgId(), iamAccount.getUser().getUid(), request.getServerName());
                        }
                        if (appId > 0 && user == null) {
	                        //return "usernotinapp";
                            //temp handling - need to be removed
                            LOGGER.log(Level.DEBUG, "User - id " + iamAccount.getUser().getUid() + "doesnt have access to this app - id " + appId + " of org - id " + iamAccount.getOrg().getOrgId());
                            relaxedAccess = true;
                        }
	                    else if(appId <= 0) { // case whn there is no app link name in api(mainly for fetch details)
	                        relaxedAccess = true;
	                    }
	                    if(relaxedAccess) {
	                        user = AccountUtil.getUserBean().getUser(-1, iamAccount.getOrg().getOrgId(), iamAccount.getUser().getUid(), null);
                            if(user == null) {
                                return logAndReturn("unauthorized", null, startTime, request);
                            }
                            //fetching permissible apps for this user corresponding to this domain
                            //setting the first permissible application(corresponding to this domain) if exists to this user
                            List<ApplicationContext> permissibleAppsForThisDomain = ApplicationApi.getApplicationsForOrgUser(user.getOuid(), request.getServerName());
                            if (CollectionUtils.isNotEmpty(permissibleAppsForThisDomain)) {
                                ApplicationApi.setThisAppForUser(user, permissibleAppsForThisDomain.get(0));
                            } else {
                                //return "usernotinapp";
                                //temp handling - need to be removed
                                LOGGER.log(Level.DEBUG, "User - id " + iamAccount.getUser().getUid() + "doesnt have access to any of the apps belonging to this domain " + request.getServerName() + " of org - id " + iamAccount.getOrg().getOrgId());
                                ApplicationApi.setThisAppForUser(user, ApplicationApi.getApplicationIdForAppDomain(request.getServerName()));
                            }
                        }
                    }
                	else {
                		user = new User(iamAccount.getUser());
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
            	String shouldApplySwitchScope = ActionContext.getContext().getParameters().get("shouldApplySwitchScope").getValue();
            	if(StringUtils.isEmpty(shouldApplySwitchScope) || shouldApplySwitchScope.equals("true")) {
            		AccountUtil.setShouldApplySwitchScope(true);
            	}
            	else {
            		AccountUtil.setShouldApplySwitchScope(false);
            	}
                if (request.getAttribute("isPortal") != null && (Boolean) request.getAttribute("isPortal")) {
                    PortalInfoContext portalInfo = AccountUtil.getOrgBean().getPortalInfo(currentAccount.getOrg().getOrgId(), false);
                    currentAccount.getOrg().setPortalId(portalInfo.getPortalId());
                } else {
                    if (currentAccount.getUser() != null) {
//						List<Long> accessibleSpace = null;
//						if (currentAccount.getUser() != null) {
//							accessibleSpace = currentAccount.getUser().getAccessibleSpace();
//						}
                        if (currentAccount != null) {
                            List<Long> sites = CommonCommandUtil.getMySiteIds();
                            if (sites != null && sites.size() == 1) {
                               AccountUtil.setCurrentSiteId(sites.get(0));
//								if (accessibleSpace == null) {
//									accessibleSpace = new ArrayList<>();
//									accessibleSpace.add(sites.get(0));
//									currentAccount.getUser().setAccessibleSpace(accessibleSpace);
//								}
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
                                        for (long id : sites) {
                                            if (id == currentSiteId) {
                                                found = true;
                                                break;
                                            }
                                        }
                                        if (!found) {
                                            throw new IllegalArgumentException("Invalid Site.");
                                        }
                                    }
                                    AccountUtil.setCurrentSiteId(currentSiteId);
//									if (currentSiteId != -1 && accessibleSpace == null) {
//										accessibleSpace = new ArrayList<>();
//										accessibleSpace.add(currentSiteId);
//										currentAccount.getUser().setAccessibleSpace(accessibleSpace);
//									}
                                }
                            }
                        }
                        request.setAttribute("ORGID", currentAccount.getOrg().getOrgId());
                        request.setAttribute("USERID", currentAccount.getUser().getOuid());


                        String timezoneVar = null;
                        if (AccountUtil.getCurrentAccount().getCurrentSiteId() > 0) {
                            SiteContext site = SpaceAPI.getSiteSpace(AccountUtil.getCurrentAccount().getCurrentSiteId());
                            if (site != null) {
                                timezoneVar = site.getTimeZone();
                            }
                        }
                        if (StringUtils.isEmpty(timezoneVar)) {
                            timezoneVar = AccountUtil.getCurrentOrg().getTimezone();
                        }
                        AccountUtil.setTimeZone(timezoneVar);

                        Parameter action = ActionContext.getContext().getParameters().get("permission");
                        Parameter moduleName = ActionContext.getContext().getParameters().get("moduleName");
                        Parameter isNewPermission = ActionContext.getContext().getParameters().get("isNewPermission");
                        Parameter permissionModuleName = ActionContext.getContext().getParameters().get("permissionModuleName");
                        if (permissionModuleName != null && permissionModuleName.getValue() != null) {
                            moduleName = permissionModuleName;
                        }
                        boolean isNewPerm = isNewPermission != null && Boolean.parseBoolean(isNewPermission.getValue());
                        if (action != null && action.getValue() != null && moduleName != null && moduleName.getValue() != null && !isAuthorizedAccess(moduleName.getValue(), action.getValue(), isNewPerm)) {
                            return logAndReturn("unauthorized", null, startTime, request);
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
                    return logAndReturn(Action.LOGIN, null, startTime, request);
                }
            }

            if (request.getRequestURI().startsWith("/admin")) {
                if (currentAccount != null) {
                    String useremail = currentAccount.getUser().getEmail();
                    if (!useremail.endsWith(FacilioProperties.getConfig("admin.domain"))) {
                        LOGGER.log(Level.DEBUG, "you are not allowed to access this page from");
                        return logAndReturn(Action.LOGIN, null, startTime, request);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.FATAL, "error in auth interceptor", e);
            return logAndReturn(Action.LOGIN, null, startTime, request);
        }

        try {
            AccountUtil.setReqUri(request.getRequestURI());
            AccountUtil.setRequestParams(request.getParameterMap());
            String remoteIPAddress = request.getRemoteAddr();
            if (false) {  // make false to intercept
                return logAndReturn(null, arg0, startTime, request);
            } else {
                AuditData data = null;
                FacilioAudit audit = new DBAudit();
                long orgId = 0L;
                if (AccountUtil.getCurrentOrg() != null) {
                    orgId = AccountUtil.getCurrentOrg().getOrgId();
                }
                int status = 200;
                try {
                    data = getAuditData(arg0);
//                    if (StringUtils.isNotEmpty(remoteIPAddress) && data != null) {
//                        data.setRemoteIPAddress(remoteIPAddress);
//                    }
//                    if (data != null && orgIdList.contains(orgId)) {
//                        AuditData finalData = data;
//                        FacilioService.runAsServiceWihReturn(() -> audit.add(finalData));
//                        data.setId(finalData.getId());
//                    }
                    return logAndReturn(null, arg0, startTime, request);
                } catch (Exception e) {
                    status = 500;
                    LOGGER.info("Exception from action classs " + e.getMessage());
                    throw e;
                } finally { // temp disabled Facilio audit data
//                    if (data != null && orgIdList.contains(orgId)) {
//                        data.setEndTime(System.currentTimeMillis());
//                        data.setStatus(status);
//                        data.setQueryCount(AccountUtil.getCurrentAccount().getTotalQueries());
//                        AuditData finalData1 = data;
//                        FacilioService.runAsServiceWihReturn(() -> audit.update(finalData1));
//                    }
                }
            }
        } catch (Exception e) {
            System.out.println("exception code 154");

            LOGGER.log(Level.FATAL, "error thrown from action class", e);
            throw e;
        }
    }

    private String logAndReturn(String returnStr, ActionInvocation invoke, long startTime, HttpServletRequest request) throws Exception {
        long timeTaken = System.currentTimeMillis() - startTime;
        AuthInterceptor.logTimeTaken(this.getClass().getSimpleName(), timeTaken, request);
        return invoke == null ? returnStr : invoke.invoke();
    }

    private AuditData getAuditData(ActionInvocation actionInvocation) {
        if (AccountUtil.getCurrentUser() != null) {
            AuditData data = new AuditData();
            ActionProxy proxy = actionInvocation.getProxy();
            data.setOrgId(AccountUtil.getCurrentUser().getOrgId());
            data.setUserId(AccountUtil.getCurrentUser().getUid());
            data.setSessionId(AccountUtil.getCurrentUserSessionId());
            data.setOrgUserId(AccountUtil.getCurrentUser().getIamOrgUserId());
            data.setAction(proxy.getConfig().getName());
            data.setMethod(proxy.getMethod());
            data.setModule(proxy.getConfig().getPackageName());
            data.setStartTime(System.currentTimeMillis());
            data.setServer(ServerInfo.getHostname());
            data.setThread(Long.parseLong(Thread.currentThread().getName()));

            return data;
        }
        return null;
    }

    private boolean isAuthorizedAccess(String moduleName, String action, boolean isNewPermission) throws Exception {

        if (action == null || "".equals(action.trim()) || AccountUtil.getCurrentUser().getRoleId() <= 0) {
            return true;
        }

        if (AccountUtil.getCurrentUser() == null) {
            return false;
        }
        if (AccountUtil.isFeatureEnabled(FeatureLicense.WEB_TAB)) {
            HttpServletRequest request = ServletActionContext.getRequest();
            String currentTab = request.getHeader("X-Tab-Id");
            if (currentTab != null && !currentTab.isEmpty()) {
                long tabId = Long.parseLong(currentTab);
                return PermissionUtil.currentUserHasPermission(tabId, moduleName, action);
            }
        } else {
            if (isNewPermission) {
                return true;
            }
            return PermissionUtil.currentUserHasPermission(moduleName, action);
        }
        return false;
    }


}