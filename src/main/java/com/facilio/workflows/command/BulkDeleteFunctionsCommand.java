package com.facilio.workflows.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflowv2.util.UserFunctionAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BulkDeleteFunctionsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        FacilioModule module = ModuleFactory.getWorkflowModule();

        if (CollectionUtils.isEmpty(recordIds)) {
            return false;
        }

        FacilioField idField = FieldFactory.getIdField(module);
        List<FacilioField> fieldsList = new ArrayList<FacilioField>() {{
            add(idField);
            add(FieldFactory.getField("deleted", "SYS_DELETED", module, FieldType.BOOLEAN));
            add(FieldFactory.getField("deletedBy", "SYS_DELETED_BY", module, FieldType.NUMBER));
            add(FieldFactory.getField("deletedTime", "SYS_DELETED_TIME", module, FieldType.NUMBER));
        }};

        List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateContexts = UserFunctionAPI.constructUpdatePropsForDeleteStatus(module, recordIds);

        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .fields(fieldsList)
                .table(module.getTableName());
        updateBuilder.batchUpdate(Collections.singletonList(idField), batchUpdateContexts);

        return false;
    }
}
