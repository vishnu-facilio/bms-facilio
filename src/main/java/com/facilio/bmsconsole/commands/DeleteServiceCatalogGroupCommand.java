package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.Map;

public class DeleteServiceCatalogGroupCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        if (id != null && id > 0) {
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getServiceCatalogModule().getTableName())
                    .select(FieldFactory.getServiceCatalogFields())
                    .andCondition(CriteriaAPI.getCondition("GROUP_ID", "groupId", String.valueOf(id), NumberOperators.EQUALS));
            Map<String, Object> data = builder.fetchFirst();

            if (data != null) {
                throw new IllegalArgumentException("Group is not empty to be deleted");
            }

            GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
                    .table(ModuleFactory.getServiceCatalogGroupModule().getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getServiceCatalogGroupModule()));
            deleteBuilder.delete();
        }
        return false;
    }
}
