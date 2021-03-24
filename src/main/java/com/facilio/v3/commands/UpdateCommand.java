package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class UpdateCommand extends FacilioCommand {

    private FacilioModule module;

    public UpdateCommand(FacilioModule module) {
        this.module = module;
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<V3Context>> recordMap = (Map<String, List<V3Context>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        int totalCount = 0;
        Set<String> extendedModules = Constants.getExtendedModules(context); //For adding extended module which has module entry only for base module. Like Assets
        if (CollectionUtils.isEmpty(extendedModules)) {
            totalCount += updateData((FacilioContext) context, module.getName(), recordMap.get(module.getName()));
        }
        else {
            for (String extendedModule : extendedModules) {
                totalCount += updateData((FacilioContext) context, extendedModule, recordMap.get(extendedModule));
            }
        }
        context.put(Constants.ROWS_UPDATED, totalCount);

        return false;
    }

    private int updateData (FacilioContext context, String moduleName, List<V3Context> records) throws Exception {
        //copied from GenericUpdateModuleDataCommand
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(moduleName);

        Class beanClass = (Class) context.get(Constants.BEAN_CLASS);
        List<FacilioField> fields = (List<FacilioField>) context.get(Constants.PATCH_FIELDS);
        if (fields == null) {
            fields = modBean.getAllFields(moduleName);
        }

        int totalCount = 0;

        for (ModuleBaseWithCustomFields record: records) {
            if(record == null || record.getId() < 0) {
                continue;
            }

            long recordId = record.getId();

            UpdateRecordBuilder<ModuleBaseWithCustomFields> updateBuilder = new UpdateRecordBuilder<>()
                    .module(module)
                    .fields(fields)
                    .andCondition(CriteriaAPI.getIdCondition(recordId, module));

            updateBuilder.withChangeSet(beanClass);

            updateBuilder.ignoreSplNullHandling();

            //updating multi select field values
            List<SupplementRecord> supplementFields = (List<SupplementRecord>) context.get(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS);
            if (CollectionUtils.isNotEmpty(supplementFields)) {
                updateBuilder.updateSupplements(supplementFields);
            }

            totalCount += updateBuilder.update(record);

            Map<Long, List<UpdateChangeSet>> changeSet = updateBuilder.getChangeSet();
            CommonCommandUtil.appendChangeSetMapToContext(context,changeSet,module.getName());
        }

        return totalCount;
    }
}
