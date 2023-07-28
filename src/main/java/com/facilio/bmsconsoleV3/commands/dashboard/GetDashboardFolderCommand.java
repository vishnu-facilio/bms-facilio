package com.facilio.bmsconsoleV3.commands.dashboard;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.DashboardFolderContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.context.dashboard.DashboardListPropsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetDashboardFolderCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context)throws Exception
    {
        DashboardListPropsContext dashboard_list_prop = (DashboardListPropsContext) context.get("dashboard_list_prop");
        List<DashboardFolderContext> dashboard_folder_list = new ArrayList<>();
        getDashboardFolderList(dashboard_list_prop.getAppId(), dashboard_folder_list);
        if (dashboard_folder_list != null) {
            List<Long> folder_ids = dashboard_folder_list.stream().map(a -> a.getId()).collect(Collectors.toList());
            dashboard_list_prop.setFolders(dashboard_folder_list);
            dashboard_list_prop.setFolder_ids(folder_ids);
        }
        return false;
    }

    private void getDashboardFolderList(Long appId, List<DashboardFolderContext> dashboard_folder_list)throws Exception
    {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getDashboardFolderFields())
                .table(ModuleFactory.getDashboardFolderModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(AccountUtil.getCurrentOrg().getOrgId()), NumberOperators.EQUALS));

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getDashboardFolderFields());
        Criteria appCriteria = new Criteria();

        ApplicationContext app = appId != null ? ApplicationApi.getApplicationForId(appId): AccountUtil.getCurrentApp();
        appCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("appId"), String.valueOf(app.getId()), NumberOperators.EQUALS));
        if(app != null && app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP)) {
            appCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("appId"), CommonOperators.IS_EMPTY));
        }
        selectBuilder.andCriteria(appCriteria);
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.DASHBOARD_V2)){
            selectBuilder.andCondition(CriteriaAPI.getCondition("IS_NEW","newFlow",String.valueOf(true),NumberOperators.EQUALS));
        }
        else{
            selectBuilder.andCondition(CriteriaAPI.getCondition("IS_NEW","newFlow",String.valueOf(true),NumberOperators.NOT_EQUALS));
        }
        List<Map<String, Object>> props = selectBuilder.get();
        if (props != null && !props.isEmpty())
        {
            for(Map<String, Object> prop :props)
            {
                DashboardFolderContext dashboardFolderContext = FieldUtil.getAsBeanFromMap(prop, DashboardFolderContext.class);
                dashboard_folder_list.add(dashboardFolderContext);
            }
        }
    }
}
