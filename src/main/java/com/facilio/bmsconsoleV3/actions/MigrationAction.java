package com.facilio.bmsconsoleV3.actions;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.bmsconsoleV3.util.OldAppMigrationUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;


@Getter
@Setter
@Log4j
public class MigrationAction extends V3Action {

    private String fromUrl;
    private String redirectUrl;
    private String previousRoute;
    private String moduleName;
    private String viewName;
    private String pageType;
    private long recordId;
    private String childModuleName;
    private long childRecordId;


    public String oldAppMigration() {
        try {
            User currentUser = AccountUtil.getCurrentUser();
            if (currentUser != null && currentUser.getEmail().contains("system+")) {
                ApplicationContext mainApp = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP, true);
                if (mainApp != null) {
                    long mainAppId = mainApp.getId();
                    FacilioChain chain = TransactionChainFactoryV3.getOldAppMigrationChain();
                    FacilioContext context = chain.getContext();
                    context.put(FacilioConstants.ContextNames.APP_ID, mainAppId);
                    chain.execute();
                } else {
                    LOGGER.error("App Id cannot be null");
                }
            } else {
                LOGGER.error("Current user don't have permission to access this api");
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return SUCCESS;
    }

    public String getProperRedirectUrl() {
        try {
            redirectUrl = previousRoute;
            if (SignupUtil.maintenanceAppSignup()) {
                ApplicationContext migratedApp = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
                if (migratedApp != null) {
                    long currentAppId = migratedApp.getId();
                    WebTabContext moduleTab = null;
                    if (moduleName.equals("dashboard")) {
                        moduleTab = OldAppMigrationUtil.getTabForTabType(currentAppId, WebTabContext.Type.DASHBOARD.getIndex());
                    } else if (moduleName.equals("analytics")) {
                        moduleTab = OldAppMigrationUtil.getTabForTabType(currentAppId, WebTabContext.Type.ANALYTICS.getIndex());
                    } else if (moduleName.equals("portfolio")) {
                        moduleTab = OldAppMigrationUtil.getCustomTab(currentAppId, "portfolio");
                    } else {
                        int tabType = WebTabContext.Type.MODULE.getIndex();
                        int deviceType = ApplicationLayoutContext.LayoutDeviceType.WEB.getIndex();
                        moduleTab = OldAppMigrationUtil.getModuleTab(currentAppId, deviceType, tabType, moduleName);
                    }
                    if (moduleTab != null) {
                        WebTabGroupContext moduleTabGroup = OldAppMigrationUtil.findWebTabGroupForWebTab(moduleTab.getId(), ApplicationLayoutContext.LayoutDeviceType.WEB.getIndex());
                        String tabRoute = moduleTab.getRoute();
                        String groupRoute = moduleTabGroup.getRoute();
                        String appLinkName = migratedApp.getLinkName();
                        if (StringUtils.isNotEmpty(tabRoute) && StringUtils.isNotEmpty(groupRoute)) {
                            switch (pageType) {
                                case "OVERVIEW":
                                    redirectUrl = "/" + appLinkName + "/" + groupRoute + "/" + tabRoute + "/" + viewName + "/" + recordId + "/overview";
                                    break;
                                case "EDIT":
                                    redirectUrl = "/" + appLinkName + "/" + groupRoute + "/" + tabRoute + "/" + recordId + "/edit";
                                    break;
                                case "CREATE":
                                    redirectUrl = "/" + appLinkName + "/" + groupRoute + "/" + tabRoute + "/new";
                                    break;
                                case "LIST":
                                case "DASHBOARD":
                                    redirectUrl = "/" + appLinkName + "/" + groupRoute + "/" + tabRoute + "/" + viewName;
                                    break;
                                case "ANALYTICS":
                                    redirectUrl = "/" + appLinkName + "/" + groupRoute + "/" + tabRoute + "/" + childModuleName;
                                    break;
                                case "SITE_SUMMARY":
                                    redirectUrl = "/" + appLinkName + "/" + groupRoute + "/" + tabRoute + "/sites/all/site/" + recordId + "/overview";
                                    break;
                                case "SITE_CHILD_SUMMARY":
                                    redirectUrl = "/" + appLinkName + "/" + groupRoute + "/" + tabRoute
                                            + "/sites/all/site/" + recordId + "/" + childModuleName + "/" + childRecordId;
                                    break;
                                case "PRINT_PAGE":
                                    if (moduleName.equals("purchaserequest")) {
                                        redirectUrl = "/" + appLinkName + "/" + groupRoute + "/" + tabRoute + "/prpdf";
                                    }
                                    if(moduleName.equals("workorder")){
                                        redirectUrl = "/" + appLinkName + "/pdf/summarydownloadreport";
                                    }
                                    if(moduleName.equals("quote")){
                                        redirectUrl = "/" + appLinkName + "/pdf/quotationpdf";
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
            setData("redirectUrl", redirectUrl);
            LOGGER.info("Route (" + previousRoute + ") from notification redirected to :- " + redirectUrl);
        } catch (Exception e) {
            LOGGER.error("Error occured while notification route redirection " + e);
            LOGGER.error("Error Occurred in notification route (" + previousRoute + ") redirection :- " + redirectUrl);
        }
        return SUCCESS;
    }
}
