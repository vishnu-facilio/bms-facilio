package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.dto.OrgUserApp;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FetchPeopleApplicationListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject result = new JSONObject();
        Long pplId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        List<Map<String, Object>> rolesApps = new ArrayList<>();
        if(pplId != null) {
            List<Long> ouIds = PeopleAPI.getUserIdForPeople(pplId);
            if(CollectionUtils.isNotEmpty(ouIds)) {
                List<FacilioField> selectFields = new ArrayList<>(AccountConstants.getOrgUserAppsFields());
                selectFields.add(FieldFactory.getField("appName", "APPLICATION_NAME", ModuleFactory.getApplicationModule(), FieldType.STRING));
                selectFields.add(FieldFactory.getField("description", "DESCRIPTION", ModuleFactory.getApplicationModule(), FieldType.STRING));
                selectFields.add(FieldFactory.getField("roleName", "NAME", AccountConstants.getRoleModule(), FieldType.STRING));
                GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                        .select(selectFields)
                        .table(AccountConstants.getOrgUserAppsModule().getTableName())
                        .innerJoin(ModuleFactory.getApplicationModule().getTableName())
                        .on("Application.ID = ORG_User_Apps.APPLICATION_ID")
                        .innerJoin(AccountConstants.getRoleModule().getTableName())
                        .on("Role.ROLE_ID = ORG_User_Apps.ROLE_ID")
                        .andCondition(CriteriaAPI.getCondition("ORG_USERID", "orgUserid", StringUtils.join(ouIds, ','), NumberOperators.EQUALS));

                rolesApps = selectBuilder.get();
                context.put("","");
            }
        }
        result.put(FacilioConstants.ContextNames.RECORD_LIST,rolesApps);
        context.put(FacilioConstants.ContextNames.DATA,result);
        return false;
    }
}
