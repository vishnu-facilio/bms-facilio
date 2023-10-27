package com.facilio.bmsconsoleV3.commands.dashboard;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardTabContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsoleV3.context.dashboard.DashboardListPropsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class GetMobileDashboardListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        DashboardListPropsContext dashboard_list_prop = (DashboardListPropsContext) context.get("dashboard_list_prop");
        List<Long> folder_ids = dashboard_list_prop.getFolder_ids();
        List<DashboardContext> dashboard_list = new ArrayList<>();
        dashboard_list = getDashboardFromFolders(folder_ids);
        for(DashboardContext dashboard : dashboard_list)
        {
            List<DashboardTabContext> dashboardTabContexts = DashboardUtil.getDashboardTabs(dashboard.getId());
            dashboard.setIsTabPresent(!CollectionUtils.isEmpty(dashboardTabContexts));
        }
        dashboard_list_prop.setDashboards(dashboard_list);
        return false;
    }
    private List<DashboardContext> getDashboardFromFolders(List<Long> folder_ids) throws Exception
    {
        if(folder_ids == null || folder_ids.size() == 0){
            return null;
        }
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getDashboardFields());
        StringJoiner ids = new StringJoiner(",");
        folder_ids.stream().forEach(f -> ids.add(String.valueOf(f)));
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getDashboardFields())
                .table(ModuleFactory.getDashboardModule().getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("dashboardFolderId"), ids.toString(), NumberOperators.EQUALS))
                .andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
                .andCustomWhere("BASE_SPACE_ID IS NULL");

        selectBuilder.andCondition(CriteriaAPI.getCondition("SHOW_HIDE_MOBILE", "mobileEnabled", "true", BooleanOperators.IS));
        selectBuilder.orderBy("DISPLAY_ORDER asc");
        List<Map<String, Object>> props = selectBuilder.get();
        if(props != null) {
            return FieldUtil.getAsBeanListFromMapList(props, DashboardContext.class);
        }
        return null;
    }
}
