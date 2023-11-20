package com.facilio.remotemonitoring.commands;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.ApplicationLayoutContext;
import com.facilio.bmsconsole.context.ApplicationRelatedAppsContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AddRemoteMonitoringApp extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ApplicationContext application = new ApplicationContext(AccountUtil.getCurrentOrg().getOrgId(), "Remote Monitoring", false,
                AppDomain.AppDomainType.FACILIO.getIndex(), FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING,
                ApplicationContext.AppLayoutType.SINGLE.getIndex(), "Remote Monitoring",
                ApplicationContext.AppCategory.FEATURE_GROUPING.getIndex());
        application.setConfig(FacilioUtil.parseJson("{\"canShowSitesSwitch\":true , \"canShowNotifications\":true , \"canShowProfile\":true}"));
        Map<String, Object> prop = FieldUtil.getAsProperties(application);
        List<FacilioField> fields = FieldFactory.getApplicationFields();
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getApplicationModule().getTableName())
                .fields(fields);
        insertBuilder.addRecord(prop);
        insertBuilder.save();

        ApplicationContext applicationContext = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        ApplicationLayoutContext layoutContext = new ApplicationLayoutContext(applicationContext.getId(), ApplicationLayoutContext.AppLayoutType.DUAL, ApplicationLayoutContext.LayoutDeviceType.WEB, FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        ApplicationApi.addApplicationLayout(layoutContext);

        ApplicationLayoutContext setupLayout = new ApplicationLayoutContext(applicationContext.getId(), ApplicationLayoutContext.AppLayoutType.SINGLE, ApplicationLayoutContext.LayoutDeviceType.SETUP, FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        ApplicationApi.addApplicationLayout(setupLayout);
        addRelatedApp();

        return false;
    }

    private static void addRelatedApp() throws Exception {
        List<String> relatedApplications = Arrays.asList(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
        long rmAppId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        List<ApplicationRelatedAppsContext> relatedApps = new ArrayList<>();
        for(String appLinkName : relatedApplications) {
            long appId = ApplicationApi.getApplicationIdForLinkName(appLinkName);
            ApplicationRelatedAppsContext relatedAppsContext = new ApplicationRelatedAppsContext();
            relatedAppsContext.setApplicationId(rmAppId);
            relatedAppsContext.setRelatedApplicationId(appId);
            relatedApps.add(relatedAppsContext);
        }
        ApplicationApi.addRelatedApplications(relatedApps);
    }
}
