package com.facilio.bmsconsoleV3.commands.dashboard;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsoleV3.context.dashboard.DashboardListPropsContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
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
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;

public class GetDashboardListCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception
    {
        Map<String, Object> orgInfo = CommonCommandUtil.getOrgInfo(AccountUtil.getCurrentOrg().getId(), "DASHBOARD_PUBLISHING_MIG");
        boolean isMigrationDone = false;
        if(orgInfo != null && orgInfo.containsKey("name") && orgInfo.get("name") != null && orgInfo.get("name").equals("DASHBOARD_PUBLISHING_MIG")){
            isMigrationDone = true;
        }
        DashboardListPropsContext dashboard_list_prop = (DashboardListPropsContext) context.get("dashboard_list_prop");
        List<DashboardContext> dashboard_list = new ArrayList<>();
        if(dashboard_list_prop.isOnlyMobile() && !dashboard_list_prop.isOnlyPublished())
        {
            dashboard_list = getDashboardFromFolders(dashboard_list_prop.getFolder_ids(), dashboard_list_prop.isOnlyMobile());
        }
        else if(dashboard_list_prop.isOnlyPublished() && !dashboard_list_prop.isOnlyMobile() && !isMigrationDone)
        {
            dashboard_list = getOnlyPublishedDashboards(dashboard_list_prop.getPublished_dashboard_ids(), dashboard_list_prop);
        }
        else if(dashboard_list_prop.isOnlyPublished() && dashboard_list_prop.isOnlyMobile() && !isMigrationDone)
        {
            dashboard_list = getOnlyPublishedDashboards(dashboard_list_prop.getPublished_dashboard_ids(), dashboard_list_prop);
        }
        else
        {
            dashboard_list = getDashboardFromFolders(dashboard_list_prop.getFolder_ids(), false);
        }
        if( dashboard_list != null && dashboard_list.size() > 0)
        {
            List<DashboardContext> new_dashboard_list = new ArrayList<>();
            if(AccountUtil.getCurrentUser().isPortalUser() && isMigrationDone)
            {
                for(DashboardContext dashboard : dashboard_list)
                {
                    if (dashboard.getClientMetaJsonString() != null) {
                        JSONParser parser = new JSONParser();
                        JSONObject client_meta_json = (JSONObject) parser.parse(dashboard.getClientMetaJsonString());
                        if (client_meta_json != null && client_meta_json.containsKey("isShow")) {
                            new_dashboard_list.add(dashboard);
                        }
                    }
                }
                dashboard_list = new ArrayList<>();
                dashboard_list.addAll(new_dashboard_list);
                dashboard_list_prop.setWithSharing(true);
            }
            for(DashboardContext dashboard : dashboard_list)
            {
                dashboard.setDashboardSharingContext(dashboard_list_prop.isWithSharing() ? DashboardUtil.getDashboardSharing(dashboard.getId()): null);
                List<DashboardTabContext> dashboardTabContexts = DashboardUtil.getDashboardTabs(dashboard.getId());
                dashboard.setIsTabPresent(!CollectionUtils.isEmpty(dashboardTabContexts));
                dashboard.setDashboardTabContexts(dashboard.isTabEnabled() && dashboard_list_prop.isWithTabs() ? dashboardTabContexts : null);
                if(dashboard_list_prop.isWithFilters()){
                    getDashboardFilters(dashboard);
                }
            }
        }
        dashboard_list_prop.setDashboards(dashboard_list);

        return false;
    }

    private List<DashboardContext> getDashboardFromFolders(List<Long> folder_ids, boolean onlyMobile) throws Exception
    {
        if(folder_ids == null || folder_ids.size() <=0 ){
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

        if(onlyMobile)
        {
            selectBuilder.andCondition(CriteriaAPI.getCondition("SHOW_HIDE_MOBILE", "mobileEnabled", "true", BooleanOperators.IS));
            selectBuilder.orderBy("DISPLAY_ORDER asc");
        }
        List<Map<String, Object>> props = selectBuilder.get();
        if(props != null) {
            return FieldUtil.getAsBeanListFromMapList(props, DashboardContext.class);
        }
        return null;
    }

    private void getDashboardFilters(DashboardContext dashboard)throws Exception
    {
        FacilioChain getDashboardFilterChain= ReadOnlyChainFactory.getFetchDashboardFilterChain();
        FacilioContext getDashboardFilterContext=getDashboardFilterChain.getContext();
        getDashboardFilterContext.put(FacilioConstants.ContextNames.DASHBOARD,dashboard);
        getDashboardFilterChain.execute();
    }

    private List<DashboardContext> getOnlyPublishedDashboards(List<Long> dashboard_ids, DashboardListPropsContext dashboard_list_prop) throws Exception
    {
        if(dashboard_ids == null || dashboard_ids.size() == 0)
        {
            return null;
        }
        StringJoiner ids = new StringJoiner(",");
        dashboard_ids.stream().forEach(f -> ids.add(String.valueOf(f)));
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getDashboardFields())
                .table(ModuleFactory.getDashboardModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("ID", "id", ids.toString(), NumberOperators.EQUALS))
                .andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
                .andCustomWhere("BASE_SPACE_ID IS NULL");
        if(dashboard_list_prop.isOnlyMobile())
        {
            selectBuilder.andCondition(CriteriaAPI.getCondition("SHOW_HIDE_MOBILE", "mobileEnabled", "true", BooleanOperators.IS));
        }
        List<Map<String, Object>> props = selectBuilder.get();
        if(props != null && props.size() > 0) {
            Set<Long> folderIds = new HashSet<>();
            for(Map<String, Object> prop : props)
            {
                DashboardContext dashboard = FieldUtil.getAsBeanFromMap(prop, DashboardContext.class);
                if(dashboard.getDashboardFolderId() != null){
                    folderIds.add(dashboard.getDashboardFolderId());
                }
            }
            if(folderIds.size() > 0) {
                getDashboardFolderFromFolderIds(folderIds, dashboard_list_prop);
            }
            return FieldUtil.getAsBeanListFromMapList(props, DashboardContext.class);
        }
        return null;
    }

    private void getDashboardFolderFromFolderIds(Set<Long> folder_ids, DashboardListPropsContext dashboard_list_prop)throws Exception
    {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getDashboardFolderFields())
                .table(ModuleFactory.getDashboardFolderModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(AccountUtil.getCurrentOrg().getOrgId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("ID", "id", StringUtils.join(folder_ids, ","), NumberOperators.EQUALS));

        List<Map<String, Object>> props = selectBuilder.get();
        if(props != null && props.size() > 0){
            dashboard_list_prop.setFolders(FieldUtil.getAsBeanListFromMapList(props, DashboardFolderContext.class));
        }
    }
}
