package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.Collections;

public class DeleteTabGroupCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        if (id != null && id > 0) {
            if (checkTabForGroup(id)) {
                throw new IllegalArgumentException("Web Tab are found inside group");
            }

            WebTabGroupContext webTabGroup = ApplicationApi.getWebTabGroup(id);

            GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                    .table(ModuleFactory.getWebTabGroupModule().getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getWebTabGroupModule()));
            builder.delete();

            if (webTabGroup == null) {
                throw new IllegalArgumentException("Invalid web group");
            }
            ApplicationApi.incrementLayoutVersionByIds(Collections.singletonList(webTabGroup.getLayoutId()));
        }
        return false;
    }

    private boolean checkTabForGroup(long id) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabWebGroupModule().getTableName())
                .select(FieldFactory.getWebTabWebGroupFields())
                .andCondition(CriteriaAPI.getCondition("WEBTAB_GROUP_ID", "groupId", String.valueOf(id), NumberOperators.EQUALS));
        return builder.fetchFirst() != null;
    }
}
