package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.ApplicationLayoutContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EnableOauth2 extends SignUpData {
    @Override
    public void addData() throws Exception {
        AppDomain developerApp = IAMAppUtil.getAppDomain(FacilioProperties.getDeveloperAppDomain());
        ApplicationContext developerApplication = new ApplicationContext(AccountUtil.getCurrentOrg().getOrgId(), "Developer", false, developerApp.getAppDomainType(), FacilioConstants.ApplicationLinkNames.DEVELOPER_APP, ApplicationContext.AppLayoutType.SINGLE.getIndex(), "Developer Apis", ApplicationContext.AppCategory.WORK_CENTERS.getIndex());

        List<FacilioField> fields = FieldFactory.getApplicationFields();
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getApplicationModule().getTableName())
                .fields(fields);

        List<Map<String, Object>> props = FieldUtil.getAsMapList(Arrays.asList(developerApplication), ApplicationContext.class);
        insertBuilder.addRecords(props);
        insertBuilder.save();
        ApplicationContext devApp = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.DEVELOPER_APP);

        if (devApp.getId() > 0) {
            ApplicationLayoutContext devLayout = new ApplicationLayoutContext(devApp.getId(), ApplicationLayoutContext.AppLayoutType.SINGLE, ApplicationLayoutContext.LayoutDeviceType.WEB, FacilioConstants.ApplicationLinkNames.DEVELOPER_APP);
            ApplicationApi.addApplicationLayout(devLayout);
            ApplicationApi.addDevAppWebTabs(devLayout);
            Role devAdmin = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(), FacilioConstants.DefaultRoleNames.DEV_ADMIN);
            if (devAdmin == null) {
                devAdmin = new Role();
                devAdmin.setName("Dev Admin");
                devAdmin.setDescription("Dev Admin");
                devAdmin.setCreatedTime(System.currentTimeMillis());
                devAdmin.setIsPrevileged(true);
                long roleId = AccountUtil.getRoleBean().createRole(AccountUtil.getCurrentOrg().getOrgId(), devAdmin);
                devAdmin.setRoleId(roleId);
            }
            ApplicationApi.addAppRoleMapping(devAdmin.getRoleId(), devApp.getId());
        }

        long devAppId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.DEVELOPER_APP);
        if (devAppId > 0) {
            User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
            User clonedUser = FieldUtil.cloneBean(superAdmin, User.class);
            clonedUser.setApplicationId(devAppId);
            AccountUtil.getUserBean().addToORGUsersApps(clonedUser, false);
        }

    }
}
