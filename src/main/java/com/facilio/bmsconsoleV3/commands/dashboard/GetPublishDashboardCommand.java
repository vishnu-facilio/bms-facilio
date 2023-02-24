package com.facilio.bmsconsoleV3.commands.dashboard;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.DashboardPublishContext;
import com.facilio.bmsconsoleV3.context.dashboard.DashboardListPropsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetPublishDashboardCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context)throws Exception
    {
        DashboardListPropsContext dashboard_list_prop = (DashboardListPropsContext) context.get("dashboard_list_prop");
        if(dashboard_list_prop != null && dashboard_list_prop.isOnlyPublished())
        {
            dashboard_list_prop.setPublished_dashboard_ids(getPublishedDashboard(dashboard_list_prop.getAppId()));
        }
        return false;
    }


    private List<Long> getPublishedDashboard(Long appId)throws Exception
    {
        List<Long> dashboardIds = new ArrayList<>();
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getDashboardPublishingFields())
                .table(ModuleFactory.getDashboardPublishingModule().getTableName())
                .andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
                .andCustomWhere("APP_ID = ?", appId != null ? appId : AccountUtil.getCurrentUser().getApplicationId());

        List<Map<String, Object>> props = selectBuilder.get();
        if(props != null && props.size() > 0)
        {
            for (Map<String, Object> prop : props)
            {
                DashboardPublishContext dashboardPublishing = FieldUtil.getAsBeanFromMap(prop, DashboardPublishContext.class);
                if (dashboardPublishing.getPublishingTypeEnum().equals(DashboardPublishContext.PublishingType.USER) && dashboardPublishing.getOrgUserId() == AccountUtil.getCurrentAccount().getUser().getOuid()) {
                    dashboardIds.add(dashboardPublishing.getDashboardId());
                } else if (dashboardPublishing.getPublishingTypeEnum().equals(DashboardPublishContext.PublishingType.ALL_USER)) {
                    dashboardIds.add(dashboardPublishing.getDashboardId());
                }
            }
        }
        return dashboardIds;
    }
}
