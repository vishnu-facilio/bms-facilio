package com.facilio.bmsconsoleV3.signup.maintenanceApp;
import com.facilio.accounts.dto.NewPermission;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.ApplicationLayoutContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
public class AddMaintenanceApplicationLayout extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        //web layout for Maintenance App
        ApplicationContext maintenance = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        ApplicationLayoutContext webLayout = new ApplicationLayoutContext(maintenance.getId(), ApplicationLayoutContext.AppLayoutType.DUAL, ApplicationLayoutContext.LayoutDeviceType.WEB, FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        webLayout.setId(ApplicationApi.getAddApplicationLayout(webLayout));
        ApplicationApi.addMaintenancePortalWebGroupsForWebLayout(webLayout);
//        Role maintenanceManager = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(), FacilioConstants.DefaultRoleNames.MAINTENANCE_MANAGER);
//        NewPermission permission = new NewPermission(ApplicationApi.getWebTabForApplication(maintenance.getId(), "workorder").getId(),7231,0);
//        permission.setRoleId(maintenanceManager.getRoleId());
//        AccountUtil.getRoleBean().addNewPermission(maintenanceManager.getRoleId(),permission);
//        permission = new NewPermission(ApplicationApi.getWebTabForApplication(maintenance.getId(), "portfolio").getId(),29,0);
//        permission.setRoleId(maintenanceManager.getRoleId());
//        AccountUtil.getRoleBean().addNewPermission(maintenanceManager.getRoleId(),permission);
//        permission = new NewPermission(ApplicationApi.getWebTabForApplication(maintenance.getId(), "approval").getId(),32768,0);
//        permission.setRoleId(maintenanceManager.getRoleId());
//        AccountUtil.getRoleBean().addNewPermission(maintenanceManager.getRoleId(),permission);
//        permission = new NewPermission(ApplicationApi.getWebTabForApplication(maintenance.getId(), "preventivemaintenance").getId(),63,0);
//        permission.setRoleId(maintenanceManager.getRoleId());
//        AccountUtil.getRoleBean().addNewPermission(maintenanceManager.getRoleId(),permission);
//        permission = new NewPermission(ApplicationApi.getWebTabForApplication(maintenance.getId(), "assets").getId(),63,0);
//        permission.setRoleId(maintenanceManager.getRoleId());
//        AccountUtil.getRoleBean().addNewPermission(maintenanceManager.getRoleId(),permission);
//        permission = new NewPermission(ApplicationApi.getWebTabForApplication(maintenance.getId(), "assetreport").getId(),262176,32);
//        permission.setRoleId(maintenanceManager.getRoleId());
//        AccountUtil.getRoleBean().addNewPermission(maintenanceManager.getRoleId(),permission);
//        Role maintenanceTech = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(), FacilioConstants.DefaultRoleNames.MAINTENANCE_TECHNICIAN);
//        permission = new NewPermission(ApplicationApi.getWebTabForApplication(maintenance.getId(), "workorder").getId(),21120,0);
//        permission.setRoleId(maintenanceTech.getRoleId());
//        AccountUtil.getRoleBean().addNewPermission(maintenanceTech.getRoleId(),permission);
        return false;
    }
}