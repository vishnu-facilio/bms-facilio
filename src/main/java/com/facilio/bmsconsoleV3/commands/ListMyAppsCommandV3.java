package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.OrgUserApp;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListMyAppsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

//long ouId = AccountUtil.getCurrentUser().getOuid();
        Long ouId = (Long) context.get(FacilioConstants.ContextNames.ORG_USER_ID);

        //AppDomain appDomain = ApplicationApi.getAppDomainForApplication(AccountUtil.getCurrentApp().getId());

        String appDomain = (String)context.get(FacilioConstants.ContextNames.APP_DOMAIN);

        List<OrgUserApp> apps = getApplicationsForOrgUser(ouId,appDomain);
        //applications= ApplicationApi.getApplicationsForOrgUser(ouId,appDomain);
        context.put(FacilioConstants.ContextNames.MY_APPS, apps);


        return false;
    }
    public static List<OrgUserApp> getApplicationsForOrgUser(long ouId, String appDomain) throws Exception {
        List<FacilioField> fields = new ArrayList<>();
        fields.addAll(AccountConstants.getOrgUserAppsFields());
        AppDomain appDomainObj = IAMAppUtil.getAppDomain(appDomain);
        if (appDomainObj == null) {
            throw new IllegalArgumentException("Invalid App Domain");
        }

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table("ORG_User_Apps")
                .innerJoin(ModuleFactory.getApplicationModule().getTableName())
                .on("ORG_User_Apps.APPLICATION_ID = Application.ID")
                .andCondition(CriteriaAPI.getCondition("ORG_User_Apps.ORG_USERID", "ouid", String.valueOf(ouId),
                        NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("Application.DOMAIN_TYPE", "domainType",
                        String.valueOf(appDomainObj.getAppDomainType()), NumberOperators.EQUALS));
        ;

        selectBuilder.orderBy("ID asc");

        List<Map<String, Object>> props = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            List<OrgUserApp> appsList = FieldUtil.getAsBeanListFromMapList(props, OrgUserApp.class);
           for (OrgUserApp app : appsList) {
               app.setApplication(ApplicationApi.getApplicationForId(app.getApplicationId()));
               app.setRole(AccountUtil.getRoleBean().getRole(app.getRoleId()));
               app.getIsDefaultApp();
            }
            return appsList;
        }
        return null;

    }

}

