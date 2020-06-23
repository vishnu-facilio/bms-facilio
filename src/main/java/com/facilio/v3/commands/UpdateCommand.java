package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
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
        Map<String, List<V3Context>> recordMap = (Map<String, List<V3Context>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        //copied from GenericUpdateModuleDataCommand

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);

        List<FacilioField> fields = (List<FacilioField>) context.get(Constants.PATCH_FIELDS);
        if (fields == null) {
            fields = modBean.getAllFields(moduleName);
        }

        Map<Long, List<UpdateChangeSet>> allChangesets = new HashMap<>();
        int totalCount = 0;

        for (V3Context record: recordMap.get(module.getName())) {
            if(record == null || record.getId() < 0) {
                continue;
            }

            long recordId = record.getId();

            UpdateRecordBuilder<V3Context> updateBuilder = new UpdateRecordBuilder<V3Context>()
                    .module(module)
                    .fields(fields)
                    .andCondition(CriteriaAPI.getIdCondition(recordId, module));

            updateBuilder.withChangeSet(V3Context.class);

            updateBuilder.ignoreSplNullHandling();
            totalCount += updateBuilder.update(record);
            Map<Long, List<UpdateChangeSet>> changeSet = updateBuilder.getChangeSet();
            allChangesets.putAll(changeSet);
        }

        context.put(Constants.ROWS_UPDATED, totalCount);
        context.put(FacilioConstants.ContextNames.CHANGE_SET_MAP, allChangesets);

        return false;
    }
}
