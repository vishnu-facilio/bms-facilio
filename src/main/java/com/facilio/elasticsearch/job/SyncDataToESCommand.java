package com.facilio.elasticsearch.job;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.elasticsearch.SyncChainFactory;
import com.facilio.elasticsearch.context.SyncContext;
import com.facilio.elasticsearch.util.SyncUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class SyncDataToESCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        if (module == null) {
            throw new IllegalArgumentException("Invalid module");
        }

        SyncContext syncContext = SyncUtil.getSyncContext(module.getModuleId());
        if (syncContext != null) {
            FacilioChain removeSyncChain = SyncChainFactory.getRemoveSyncChain();
            FacilioContext removeSyncChainContext = removeSyncChain.getContext();
            removeSyncChainContext.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
            removeSyncChain.execute();
        }

        syncContext = new SyncContext();
        syncContext.setSyncModuleId(module.getModuleId());
        syncContext.setSyncModule(module);
        syncContext.setSyncing(true);
        syncContext.setLastSyncRecordId(0l);
        SyncUtil.updateSync(syncContext);

        List<FacilioField> allFields = modBean.getAllFields(syncContext.getSyncModule().getName());
        List<LookupField> lookupFields = new ArrayList<>();
        for (FacilioField field : allFields) {
            if (field instanceof LookupField) {
                lookupFields.add((LookupField) field);
            }
        }

        SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<>()
                .module(syncContext.getSyncModule())
                .select(modBean.getAllFields(syncContext.getSyncModule().getName()))
                .beanClass(FacilioConstants.ContextNames.getClassFromModule(module))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(module), String.valueOf(syncContext.getLastSyncRecordId()), NumberOperators.GREATER_THAN));
//        builder.fetchSupplements(lookupFields);
        SelectRecordsBuilder.BatchResult<ModuleBaseWithCustomFields> batchResult = builder.getInBatches(FieldFactory.getIdField(module).getCompleteColumnName() + " ASC", 1000);

        List<ModuleBaseWithCustomFields> wholeRecords = new ArrayList<>();
        while (batchResult.hasNext()) {
            List<ModuleBaseWithCustomFields> recordList = batchResult.get();
            ModuleBaseWithCustomFields moduleRecord = recordList.get(recordList.size() - 1);
            syncContext.setLastSyncRecordId(moduleRecord.getId());
            wholeRecords.addAll(recordList);
            SyncUtil.updateSync(syncContext);
        }

        context.put(FacilioConstants.ContextNames.RECORD_LIST, wholeRecords);

        return false;
    }
}
