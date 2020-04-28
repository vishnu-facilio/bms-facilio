
package com.facilio.bmsconsole.interceptors;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.audit.AuditData;
import com.facilio.audit.DBAudit;
import com.facilio.audit.FacilioAudit;
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
    private static final HashSet<Long> ORGID_LIST = new HashSet<>();

    @Override
    public void init() {
        super.init();
        ORGID_LIST.add(78L);
        ORGID_LIST.add(146L);
        ORGID_LIST.add(176L);
        ORGID_LIST.add(183L);
        ORGID_LIST.add(246L);
        ORGID_LIST.add(155L);
        ORGID_LIST.add(285L);
        ORGID_LIST.add(320L);
        ORGID_LIST.add(315L);
        ORGID_LIST.add(316L);
    }

    @Override
    public String intercept(ActionInvocation arg0) throws Exception {

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
                    return Action.LOGIN;
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
                    return Action.LOGIN;
                }
            } else {
                User user = null;
                if (iamAccount.getOrg() == null) {
                    LOGGER.log(Level.DEBUG, "User seems to have been deactivated");
                    return Action.LOGIN;
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
                    if (appId <= 0) {
                        appId = ApplicationApi.getApplicationIdForAppDomain(request.getServerName());
                    }
                } catch (Exception e) {
                    throw new AccountException(ErrorCode.INVALID_APP_DOMAIN, "invalid appDomain");
                }
                if (iamAccount.getUser() != null) {
                	String authorisationReqd = ActionContext.getContext().getParameters().get("authorise").getValue();
                	if(StringUtils.isEmpty(authorisationReqd) || authorisationReqd.equals("true")) {
	                	user = AccountUtil.getUserBean().getUser(appId, iamAccount.getOrg().getOrgId(), iamAccount.getUser().getUid());
	                    if (user == null) {
	                        LOGGER.log(Level.DEBUG, "User - id " + iamAccount.getUser().getUid() + "doesnt have access to this app - " + request.getServerName());
	                        return "usernotinapp";
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
                    if (!useremail.endsWith(FacilioProperties.getConfig("admin.domain"))) {
                        LOGGER.log(Level.DEBUG, "you are not allowed to access this page from");
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
            String remoteIPAddress = request.getRemoteAddr();
            if (false) {  // make false to intercept
                return arg0.invoke();
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
                    if (StringUtils.isNotEmpty(remoteIPAddress) && data != null) {
                        data.setRemoteIPAddress(remoteIPAddress);
                    }
                    if (data != null && ORGID_LIST.contains(orgId)) {
                        AuditData finalData = data;
                        FacilioService.runAsServiceWihReturn(() -> audit.add(finalData));
                        data.setId(finalData.getId());
                    }
                    return arg0.invoke();
                } catch (Exception e) {
                    status = 500;
                    LOGGER.info("Exception from action classs " + e.getMessage());
                    throw e;
                } finally {
                    if (data != null && ORGID_LIST.contains(orgId)) {
                        data.setEndTime(System.currentTimeMillis());
                        data.setStatus(status);
                        data.setQueryCount(AccountUtil.getCurrentAccount().getTotalQueries());
                        AuditData finalData1 = data;
                        FacilioService.runAsServiceWihReturn(() -> audit.update(finalData1));
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