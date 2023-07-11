package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetRolesAppsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> appIds = (List<Long>) context.get(FacilioConstants.ContextNames.APP_ID);
        List<FacilioField> fields = new ArrayList<>();
        Map<String,FacilioField> roleFieldsMap = FieldFactory.getAsMap(AccountConstants.getRoleFields());
        Map<String,FacilioField> roleAppFieldsMap = FieldFactory.getAsMap(AccountConstants.getRolesAppsFields());
        fields.add(roleFieldsMap.get("roleId"));
        fields.add(roleFieldsMap.get("name"));
        fields.add(roleAppFieldsMap.get("applicationId"));

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(AccountConstants.getRoleModule().getTableName())
                .innerJoin(AccountConstants.getRolesAppsModule().getTableName())
                .on(AccountConstants.getRoleModule().getTableName() + ".ROLE_ID = " + AccountConstants.getRolesAppsModule().getTableName() + ".ROLE_ID");

        if(CollectionUtils.isNotEmpty(appIds)) {
            selectBuilder.andCondition(CriteriaAPI.getCondition(roleAppFieldsMap.get("applicationId"), StringUtils.join(appIds, ","), NumberOperators.EQUALS));
        }
        List<Map<String,Object>> props = selectBuilder.get();
        context.put(FacilioConstants.ContextNames.ROLES_APPS,props);
        return false;
    }
}
