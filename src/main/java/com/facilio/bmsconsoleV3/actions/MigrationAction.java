package com.facilio.bmsconsoleV3.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.bmsconsoleV3.util.OldAppMigrationUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.PortalUtil;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.protocol.types.Field;


@Getter
@Setter
@Log4j
public class MigrationAction extends V3Action {

    private String fromUrl;
    private String redirectUrl;
    private String moduleName;
    private String previousRoute;
    private long recordId;


    public String oldAppMigration() {
        try {
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
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return SUCCESS;
    }

    public String getProperRedirectUrl() {
        try {
            redirectUrl = previousRoute;
            if (SignupUtil.maintenanceAppSignup()) {
                ApplicationContext migratedApp = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, true);
                if (migratedApp != null) {
                    long currentAppId = migratedApp.getId();
                    WebTabContext moduleTab = PortalUtil.getModuleTab(currentAppId, moduleName);
                    WebTabGroupContext moduleTabGroup = OldAppMigrationUtil.findWebTabGroupForWebTab(moduleTab.getId(), ApplicationLayoutContext.LayoutDeviceType.WEB.getIndex());
                    String tabRoute = moduleTab.getRoute();
                    String groupRoute = moduleTabGroup.getRoute();
                    String appLinkName = migratedApp.getLinkName();

                    if (StringUtils.isNotEmpty(tabRoute) && StringUtils.isNotEmpty(groupRoute)) {
                        redirectUrl = "/" + appLinkName + "/" + groupRoute + "/" + tabRoute + "/all/" + recordId + "/overview";
                    }
                }
            }
            setData("redirectUrl", redirectUrl);
            LOGGER.info("Route from notification redirected to :- " + redirectUrl);
        } catch (Exception e) {
            LOGGER.error("Error Occurred in notification route redirection :- " + redirectUrl);
        }
        return SUCCESS;
    }
}
