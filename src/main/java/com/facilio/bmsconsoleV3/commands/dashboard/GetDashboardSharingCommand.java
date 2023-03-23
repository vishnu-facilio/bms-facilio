package com.facilio.bmsconsoleV3.commands.dashboard;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardSharingContext;
import com.facilio.bmsconsoleV3.context.dashboard.DashboardListPropsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.delegate.context.DelegationType;
import com.facilio.delegate.util.DelegationUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetDashboardSharingCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(GetDashboardSharingCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception
    {
        DashboardListPropsContext dashboard_list_prop = (DashboardListPropsContext) context.get("dashboard_list_prop");
//        Map<String, Object>  keymap = CommonCommandUtil.getOrgInfo(AccountUtil.getCurrentOrg().getId() , "DASHBOARD_PUBLISHING_MIG");
        if(dashboard_list_prop.isWithSharing()) {
            List<DashboardContext> dashboards = new ArrayList<>();
            if(dashboard_list_prop.getDashboards() != null && dashboard_list_prop.getDashboards().size() > 0) {
                getDashboardWithSharing(dashboard_list_prop.getDashboards(), dashboards, dashboard_list_prop.isMigrationDone());
            }
            if (dashboards != null) {
                dashboard_list_prop.setDashboards(dashboards);
            }
        }
        return false;
    }

    private void getDashboardWithSharing(List<DashboardContext> dashboard_list, List<DashboardContext> dashboards, boolean isMigrationDone)throws Exception
    {
        Role currnt_user_role = AccountUtil.getCurrentUser() != null ? AccountUtil.getCurrentUser().getRole() : null;
        if (currnt_user_role != null && ( currnt_user_role.getName().equals(AccountConstants.DefaultSuperAdmin.SUPER_ADMIN) || (currnt_user_role.getIsPrevileged() != null && currnt_user_role.getIsPrevileged()))) {
            dashboards.addAll(dashboard_list);
            return ;
        }
        List<Integer> portalList = new LinkedList<>();
        List<Long> dashboardList = new ArrayList<>();
        portalList.add(DashboardSharingContext.SharingType.PORTAL.getIntVal());
        portalList.add(DashboardSharingContext.SharingType.ALL_PORTAL_USER.getIntVal());

        List<Long> dashboardIds = dashboard_list.stream().map(a -> a.getId()).collect(Collectors.toList());
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getDashboardSharingFields())
                .table(ModuleFactory.getDashboardSharingModule().getTableName())
                .andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
                .andCondition(CriteriaAPI.getCondition("Dashboard_Sharing.DASHBOARD_ID", "dashboardId", StringUtils.join(dashboardIds, ","), NumberOperators.EQUALS));

        if(!AccountUtil.getCurrentUser().isPortalUser() || isMigrationDone) {
            selectBuilder.andCustomWhere("Dashboard_Sharing.Sharing_type != ? ", DashboardSharingContext.SharingType.PORTAL.getIntVal());
        }
        else {
            selectBuilder.andCondition(CriteriaAPI.getCondition("Dashboard_Sharing.Sharing_type", "sharingType", StringUtils.join(portalList, ","), NumberOperators.EQUALS));
        }
        List<Map<String, Object>> props = selectBuilder.get();

        if (props != null && !props.isEmpty()) {
            User currentuser = AccountUtil.getCurrentAccount().getUser();
            List<User> alluser = new ArrayList<User>();
            alluser.add(currentuser);
            try {
                User delegateuser = DelegationUtil.getUser(AccountUtil.getCurrentAccount().getUser(), System.currentTimeMillis(), DelegationType.DASHBOARD);
                if (delegateuser != null) {
                    alluser.add(delegateuser);
                }
            } catch (Exception e) {
                LOGGER.debug("Exception occurred :" + e);
            }
            for (Map<String, Object> prop : props)
            {
                DashboardSharingContext dashboardSharing = FieldUtil.getAsBeanFromMap(prop, DashboardSharingContext.class);
                if (dashboardIds.contains(dashboardSharing.getDashboardId())) {
                    dashboardIds.remove(dashboardSharing.getDashboardId());
                }
                for(User user : alluser)
                {
                    if(!dashboardList.contains(dashboardSharing.getDashboardId())) {
                        if (dashboardSharing.getSharingTypeEnum().equals(DashboardSharingContext.SharingType.USER) && dashboardSharing.getOrgUserId() == user.getOuid()) {
                            dashboardList.add(dashboardSharing.getDashboardId());
                        }
                        else if (dashboardSharing.getSharingTypeEnum().equals(DashboardSharingContext.SharingType.ROLE) && dashboardSharing.getRoleId() == user.getRoleId()) {
                            dashboardList.add(dashboardSharing.getDashboardId());
                        }
                        else if (dashboardSharing.getSharingTypeEnum().equals(DashboardSharingContext.SharingType.GROUP)) {
                            List<Group> mygroups = AccountUtil.getGroupBean().getMyGroups(user.getOuid());
                            for (Group group : mygroups) {
                                if (dashboardSharing.getGroupId() == group.getId() && !dashboardList.contains(dashboardSharing.getDashboardId())) {
                                    dashboardList.add(dashboardSharing.getDashboardId());
                                }
                            }
                        }
                        else if (dashboardSharing.getSharingTypeEnum().equals(DashboardSharingContext.SharingType.PORTAL)) {
                            if(dashboardSharing.getOrgUserId() > 0) {
                                if(dashboardSharing.getOrgUserId() == user.getOuid()) {
                                    dashboardList.add(dashboardSharing.getDashboardId());
                                }
                            }
                            else {
                                dashboardList.add(dashboardSharing.getDashboardId());
                            }
                        }
                        else if (dashboardSharing.getSharingTypeEnum().equals(DashboardSharingContext.SharingType.ALL_PORTAL_USER)) {
                            dashboardList.add(dashboardSharing.getDashboardId());
                        }
                    }
                }
            }
            dashboardList.addAll(dashboardIds);
            for (DashboardContext dashboard : dashboard_list) {
                if(dashboardList.contains(dashboard.getId())){
                    dashboards.add(dashboard);
                }
            }
        }
        else
        {
            if(isMigrationDone || (AccountUtil.getCurrentUser().getAppDomain() != null && AccountUtil.getCurrentUser().getAppDomain().getAppDomainTypeEnum() == AppDomain.AppDomainType.FACILIO)) {
                dashboards.addAll(dashboard_list);
            }
        }
    }
}
