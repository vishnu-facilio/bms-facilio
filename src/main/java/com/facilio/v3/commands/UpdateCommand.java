package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import org.apache.commons.chain.Context;

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

        Class beanClass = (Class) context.get(Constants.BEAN_CLASS);
        List<FacilioField> fields = (List<FacilioField>) context.get(Constants.PATCH_FIELDS);
        if (fields == null) {
            fields = modBean.getAllFields(moduleName);
        }

        int totalCount = 0;

        for (ModuleBaseWithCustomFields record: recordMap.get(module.getName())) {
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
            totalCount += updateBuilder.update(record);
            Map<Long, List<UpdateChangeSet>> changeSet = updateBuilder.getChangeSet();
            CommonCommandUtil.appendChangeSetMapToContext(context,changeSet,module.getName());
        }

        context.put(Constants.ROWS_UPDATED, totalCount);

        return false;
    }
}
