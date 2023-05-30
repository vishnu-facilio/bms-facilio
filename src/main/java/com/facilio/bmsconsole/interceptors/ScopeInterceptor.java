
package com.facilio.bmsconsole.interceptors;

import com.amazonaws.regions.Regions;
import com.facilio.accounts.dto.*;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.audit.AuditData;
import com.facilio.audit.DBAudit;
import com.facilio.audit.FacilioAudit;
import com.facilio.audit.FacilioAuditOrgList;
import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.beans.WebTabBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.WebTabUtil;
import com.facilio.bmsconsoleV3.util.APIPermissionUtil;
import com.facilio.bmsconsoleV3.util.V3PermissionUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.util.DBConf;
import com.facilio.filters.AccessLogFilter;
import com.facilio.filters.MultiReadServletRequest;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.exceptions.AccountException;
import com.facilio.iam.accounts.exceptions.AccountException.ErrorCode;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.screen.context.RemoteScreenContext;
import com.facilio.server.ServerInfo;
import com.facilio.service.FacilioService;
import com.facilio.util.ValidatePermissionUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.extension.annotations.WithSpan;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Parameter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

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
    @WithSpan
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
                    AuthInterceptor.checkIfPuppeteerRequestAndLog(this.getClass().getSimpleName(), MessageFormat.format("Setting current account in thread local for remote with orgid => {0} and userid = {1}", DBConf.getInstance().getCurrentOrgId(), DBConf.getInstance().getCurrentUserId()), request);
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
                    AuthInterceptor.checkIfPuppeteerRequestAndLog(this.getClass().getSimpleName(), MessageFormat.format("Setting current account in thread local for device with orgid => {0} and userid = {1}", DBConf.getInstance().getCurrentOrgId(), DBConf.getInstance().getCurrentUserId()), request);
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
                AuthInterceptor.checkIfPuppeteerRequestAndLog(this.getClass().getSimpleName(), MessageFormat.format("Setting temp current account in thread local for normal with orgid => {0} and userid = {1}", DBConf.getInstance().getCurrentOrgId(), DBConf.getInstance().getCurrentUserId()), request);
                long appId = -1;
                try {
                    String appLinkName = null;
                    if (request.getAttribute("facilio.app.name") != null) {
                        appLinkName = (String) request.getAttribute("facilio.app.name");
                    }
                    if (StringUtils.isNotEmpty(appLinkName)) {
                    	if(!appLinkName.equalsIgnoreCase("tv")) {
                            ApplicationContext application = ApplicationApi.getApplicationForLinkName(appLinkName, true);
                            if (application != null) {
                                appId = application.getId();
                            } else {
                                String orgInitStatus = AccountUtil.handleOrgSignup(iamAccount.getOrg(), iamAccount.getUser());
                                if (orgInitStatus != null) {
                                    LOGGER.warn("Org Signup Chain not completed yet, please wait...");
                                    return orgInitStatus;
                                }
                                return ErrorUtil.sendError(ErrorUtil.Error.PAGE_NOT_FOUND);
                            }
                        }
                    }
                } catch (Exception e) {
                	
                	if(AccountUtil.getCurrentOrg().getId() == 1l || AccountUtil.getCurrentOrg().getId() == 350l) {
                		LOGGER.log(Level.ERROR, e.getMessage(), e);
                	}

                    throw new AccountException(ErrorCode.INVALID_APP_DOMAIN, "invalid app linkName");
                }
                if (iamAccount.getUser() == null) {
                    User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(iamAccount.getOrg().getOrgId());
                    iamAccount.setUser(superAdmin);
                }
                String authorisationReqd = ActionContext.getContext().getParameters().get("authorise").getValue();
                if (StringUtils.isEmpty(authorisationReqd) || authorisationReqd.equals("true")) {
                    boolean relaxedAccess = false;
                    if (appId > 0) {
                        user = AccountUtil.getUserBean().getUser(appId, iamAccount.getOrg().getOrgId(), iamAccount.getUser().getUid(), request.getServerName());
                    }
                    if (appId > 0 && user == null) {
                        //return "usernotinapp";
                        //temp handling - need to be removed
                        LOGGER.log(Level.DEBUG, "User - id " + iamAccount.getUser().getUid() + "doesnt have access to this app - id " + appId + " of org - id " + iamAccount.getOrg().getOrgId());
                        relaxedAccess = true;
                    } else if (appId <= 0) { // case whn there is no app link name in api(mainly for fetch details)
                        relaxedAccess = true;
                    }
                    if (relaxedAccess) {
                        user = AccountUtil.getUserBean().getUser(-1, iamAccount.getOrg().getOrgId(), iamAccount.getUser().getUid(), null);
                        if (user == null) {
                            // handling for org which signed up using new identity service
                            String orgInitStatus = AccountUtil.handleOrgSignup(iamAccount.getOrg(), iamAccount.getUser());
                            if (orgInitStatus != null) {
                                LOGGER.warn("Org Signup Chain not completed yet, please wait...");
                                return orgInitStatus;
                            }
                            return logAndReturn("unauthorized", null, startTime, request);
                        }
                        //fetching permissible  apps for this user corresponding to this domain
                        //setting the first permissible application(corresponding to this domain) if exists to this user
                        List<ApplicationContext> permissibleAppsForThisDomain = ApplicationApi.getApplicationsForOrgUser(user.getOuid(), request.getServerName());
                        if (CollectionUtils.isNotEmpty(permissibleAppsForThisDomain)) {
                            if(!ApplicationApi.isAuthorisedApplication(permissibleAppsForThisDomain,appId)) {
                                return ErrorUtil.sendError(ErrorUtil.Error.USER_NOT_IN_APP);
                            }
                            ApplicationContext appToBeAssigned = ApplicationApi.getDefaultOrFirstApp(permissibleAppsForThisDomain,user.getOuid());
                            ApplicationApi.setThisAppForUser(user, appToBeAssigned, false);
                        } else {
                            if(isAuthRequired()) {
                                LOGGER.log(Level.DEBUG, "Auth required - User - id " + iamAccount.getUser().getUid() + "doesnt have access to any of the apps belonging to this domain " + request.getServerName() + " of org - id " + iamAccount.getOrg().getOrgId());
                                //We can check logs for a somedays and uncomment usernotinapp
                                //return "usernotinapp";
                            }
                            //temp handling - need to be removed
                            //LOGGER.log(Level.DEBUG, "User - id " + iamAccount.getUser().getUid() + "doesnt have access to any of the apps belonging to this domain " + request.getServerName() + " of org - id " + iamAccount.getOrg().getOrgId());
                            ApplicationApi.setThisAppForUser(user, ApplicationApi.getApplicationIdForAppDomain(request.getServerName()), true);
                        }
                    }
                } else {
                    user = new User(iamAccount.getUser());
                }
                Account account = new Account(iamAccount.getOrg(), user);
                account.setUserSessionId(iamAccount.getUserSessionId());
                Map<String, Object> userSession = IAMUserUtil.getUserSession(iamAccount.getUserSessionId());
                boolean isProxySession = userSession == null ? false : (boolean) userSession.getOrDefault("isProxySession", false);
                if (isProxySession) {
                    String proxySessionToken = FacilioCookie.getUserCookie(request,"fc.idToken.proxy");
                    String proxyUserToken = request.getHeader("X-Proxy-Token");
                    if(proxyUserToken!=null && proxyUserToken.trim().length() > 0){
                        proxySessionToken = request.getHeader("X-Proxy-Token").replace("Bearer ", "");
                    }
                    Map<String, Object> proxySession = IAMUserUtil.getProxySession(proxySessionToken);
                    account.getUser().setProxy((String) proxySession.get("email"));
                }
                AccountUtil.cleanCurrentAccount();
                AccountUtil.setCurrentAccount(account);
                AuthInterceptor.checkIfPuppeteerRequestAndLog(this.getClass().getSimpleName(), MessageFormat.format("Setting current account in thread local for normal with orgid => {0} and userid = {1}", DBConf.getInstance().getCurrentOrgId(), DBConf.getInstance().getCurrentUserId()), request);
            }
        }


        try {
            if(request != null) {
                AccountUtil.setReqUri(request.getRequestURI());
                if (Regions.US_WEST_2.getName().equals(FacilioProperties.getRegion()) && StringUtils.isNotEmpty(request.getRequestURI()) &&
                        (request.getRequestURI().contains("getAvailableButtons") || request.getRequestURI().contains("getAvailableState"))
                ) {
                    LOGGER.info("Scope interceptor called for url : "+request.getRequestURI());
                }
                String currentTab = request.getHeader("X-Tab-Id");
                if (StringUtils.isNotEmpty(currentTab)) {
                    WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");
                    Long currentTabId = Long.parseLong(currentTab);
                    if(currentTabId != null) {
                        AccountUtil.setCurrentTab(tabBean.getWebTab(Long.parseLong(currentTab)));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.info("Setting Request URI error");
        }

        HttpServletResponse response = ServletActionContext.getResponse();
        try {
            Account currentAccount = AccountUtil.getCurrentAccount();
            if (currentAccount != null && request.getRequestURI().equalsIgnoreCase("/api/v2/orgsetup")) {
                // dont remove this if check, this is for bypassing site level scope handling
                LOGGER.info("/api/v2/orgsetup api called");
            }
            else if (currentAccount != null) {
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
                            AccountUtil.setValueGenerator(new HashMap<>());
                            if (sites != null && sites.size() == 1) {
                               AccountUtil.setCurrentSiteId(sites.get(0));
//								if (accessibleSpace == null) {
//									accessibleSpace = new ArrayList<>();
//									accessibleSpace.add(sites.get(0));
//									currentAccount.getUser().setAccessibleSpace(accessibleSpace);
//								}
                            } else {
                                String currentSite = null;
                                boolean setSiteValue = true;
                                try {
                                    setSiteValue = false;
                                    if (AccountUtil.getCurrentOrg() != null && AccountUtil.isFeatureEnabled(FeatureLicense.SCOPE_VARIABLE)) {
                                        if (AccountUtil.getCurrentAccount() != null && AccountUtil.getCurrentAccount().isFromMobile() != null && AccountUtil.getCurrentAccount().isFromMobile()) {
                                            setSiteValue = true;
                                        } else {
                                            if(StringUtils.isNotEmpty(request.getHeader("X-current-site"))) {
                                                FacilioCookie.eraseUserCookie(request, response, "fc.currentSite", null);
                                            }
                                        }
                                    } else {
                                        setSiteValue = true;
                                    }
                                }
                                catch (Exception e) {
                                    LOGGER.info("Error at site header value");
                                    setSiteValue = true;
                                }
                                if(setSiteValue) {
                                    currentSite = request.getHeader("X-current-site");
                                }
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

//									if (currentSiteId != -1 && accessibleSpace == null) {
//										accessibleSpace = new ArrayList<>();
//										accessibleSpace.add(currentSiteId);
//										currentAccount.getUser().setAccessibleSpace(accessibleSpace);
//									}
                                }
                                AccountUtil.setCurrentSiteId(currentSiteId);
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
                        if(AccountUtil.getCurrentOrg().getAllowUserTimeZone() != null && AccountUtil.getCurrentOrg().getAllowUserTimeZone())
                        {
                            if(AccountUtil.getCurrentUser().getTimezone() != null)
                            {
                                timezoneVar = AccountUtil.getCurrentUser().getTimezone();
                            }
                        }
                        if (StringUtils.isEmpty(timezoneVar)) {
                            timezoneVar = AccountUtil.getCurrentOrg().getTimezone();
                        }
                        AccountUtil.setTimeZone(timezoneVar);


                        Parameter action = ActionContext.getContext().getParameters().get("permission");
                        Parameter moduleName = ActionContext.getContext().getParameters().get("moduleName");
                        Parameter permissionModuleName = ActionContext.getContext().getParameters().get("permissionModuleName");
                        Boolean checkPermission = Boolean.valueOf(String.valueOf(ActionContext.getContext().getParameters().get("checkPermission")));
                        Parameter parentModuleName = ActionContext.getContext().getParameters().get("parentModuleName");
                        Parameter setupTab = ActionContext.getContext().getParameters().get("setupTab");
                        Boolean deprecated = Boolean.valueOf(String.valueOf(ActionContext.getContext().getParameters().get("deprecated")));
                        Boolean checkTabPermission = Boolean.valueOf(String.valueOf(ActionContext.getContext().getParameters().get("checkTabPermission")));

                        if (permissionModuleName != null && permissionModuleName.getValue() != null) {
                            moduleName = permissionModuleName;
                        }
                        if(parentModuleName != null && parentModuleName.getValue() != null) {
                            moduleName = parentModuleName;
                        }
                        boolean isSetupPermission = false;
                        if(setupTab != null && setupTab.getValue() != null) {
                            moduleName = getModuleNameParam("setup");

                            isSetupPermission = true;
                        }
                        String method = request.getMethod();
                        boolean isV3Permission = false;
                        boolean isTabPermision = false;

                        if(((checkPermission != null && checkPermission) || (checkTabPermission != null && checkTabPermission)) && action != null && action.getValue() != null) {
                            if(checkPermission != null && checkPermission) {
                                isV3Permission = true;
                            } else if(checkTabPermission != null && checkTabPermission) {
                                isTabPermision = true;
                            }
                            action = WebTabUtil.getActions(action,method);
                        }

                        try {
                            if (APIPermissionUtil.shouldCheckPermission(request.getRequestURI())) {
                                if (!(isV3Permission || isTabPermision || isSetupPermission)) {
                                    String authorisationReq = ActionContext.getContext().getParameters().get("authorise").getValue();
                                    if (StringUtils.isEmpty(authorisationReq) || authorisationReq.equals("true")) {
                                        LOGGER.info("API Permission missing for " + request.getRequestURI());
                                        if (!(FacilioProperties.isProduction() || FacilioProperties.isOnpremise())) {
                                            return logAndReturn("unauthorized", null, startTime, request);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            LOGGER.error("Error while checking API permission");
                        }


                        if(throwDeprecatedApiError(deprecated)) {
                            return logAndReturn("unauthorized", null, startTime, request);
                        }

                        if (action != null && action.getValue() != null && (isTabPermision || (moduleName != null && moduleName.getValue() != null)) && !WebTabUtil.isAuthorizedAccess(moduleName.getValue(), action.getValue(), isV3Permission,setupTab.getValue(), isSetupPermission, isTabPermision, method)) {
                            if (isSetupPermission || isV3Permission || isTabPermision) {
                                if (!(request.getRequestURI() != null && ValidatePermissionUtil.hasUrl(request.getRequestURI()))) {
                                    return logAndReturn("unauthorized", null, startTime, request);
                                }
                            }
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
            throw e;
        }
    }

    private String logAndReturn(String returnStr, ActionInvocation invoke, long startTime, HttpServletRequest request) throws Exception {
        long timeTaken = System.currentTimeMillis() - startTime;
        AuthInterceptor.logTimeTaken(this.getClass().getSimpleName(), timeTaken, request);
        AuthInterceptor.checkIfPuppeteerRequestAndLog(this.getClass().getSimpleName(), MessageFormat.format("Scope interceptor done with return {0}", invoke != null ? "(invoking will happen. no return)" : returnStr), request);
        String resp = invoke == null ? ErrorUtil.sendError(ErrorUtil.Error.NO_PERMISSION) : invoke.invoke();
        if (AccessLogFilter.isGetAvailableRequest(request)) {
            LOGGER.info("Scope interceptor Done for url : "+request.getRequestURI()+AuthInterceptor.getResponseCode());
        }
        return resp;
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


    private Parameter getModuleNameParam(String moduleName) {
        return new Parameter.Request(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
    }

    private boolean isAuthRequired() {
        String authRequired = ActionContext.getContext().getParameters().get("auth").getValue();
        if (authRequired == null || "".equalsIgnoreCase(authRequired.trim()) || "true".equalsIgnoreCase(authRequired)) {
            return true;
        }
        return false;
    }


    private static boolean throwDeprecatedApiError(Boolean deprecated) {
        try {
            if(AccountUtil.getCurrentOrg() != null && AccountUtil.isFeatureEnabled(FeatureLicense.THROW_403_WEBTAB)) {
                if(deprecated != null && deprecated) {
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.info("Error checking deprecated API");
        }
        return false;
    }
}