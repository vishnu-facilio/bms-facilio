package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateCommand extends FacilioCommand {

    private FacilioModule module;

    public UpdateCommand(FacilioModule module) {
        this.module = module;
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        //copied from GenericUpdateModuleDataCommand
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = (Map<String, List<ModuleBaseWithCustomFields>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);

        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
        if (fields == null) {
            fields = modBean.getAllFields(moduleName);
        }

        Map<Long, List<UpdateChangeSet>> allChangesets = new HashMap<>();
        int totalCount = 0;

        for (ModuleBaseWithCustomFields record: recordMap.get(module.getName())) {
            if(record == null || record.getId() < 0) {
                continue;
            }

            long recordId = record.getId();

            UpdateRecordBuilder<ModuleBaseWithCustomFields> updateBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
                    .module(module)
                    .fields(fields)
                    .andCondition(CriteriaAPI.getIdCondition(recordId, module));

            updateBuilder.withChangeSet(ModuleBaseWithCustomFields.class);

            totalCount += updateBuilder.update(record);

            Map<Long, List<UpdateChangeSet>> changeSet = updateBuilder.getChangeSet();
            allChangesets.putAll(changeSet);
        }

        context.put(FacilioConstants.ContextNames.ROWS_UPDATED, totalCount);
        context.put(FacilioConstants.ContextNames.CHANGE_SET, allChangesets);

        return false;
    }
}
