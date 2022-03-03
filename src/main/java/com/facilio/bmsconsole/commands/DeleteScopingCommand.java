package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.ScopingContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class DeleteScopingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long scopingId = (long) context.get(FacilioConstants.ContextNames.SCOPING_ID);
        if (scopingId > 0) {
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(AccountConstants.getOrgUserAppsModule().getTableName())
                    .select(AccountConstants.getOrgUserAppsFields())
                    .andCondition(CriteriaAPI.getCondition("SCOPING_ID", "scopingId", String.valueOf(scopingId), NumberOperators.EQUALS));             ;

            List<Map<String, Object>> list = builder.get();
            if(CollectionUtils.isNotEmpty(list)){
                throw new IllegalArgumentException("Scoping can't be deleted as its associated with user(s)");
            }
            ScopingContext scoping = ApplicationApi.getScoping(scopingId);
            if(scoping != null && scoping.isDefault()) {
                throw new IllegalArgumentException("Default Scoping can't be deleted");
            }
            ApplicationApi.deleteScopingConfig(scopingId);
            ApplicationApi.deleteScoping(scopingId);
        }
        return false;
    }
}
