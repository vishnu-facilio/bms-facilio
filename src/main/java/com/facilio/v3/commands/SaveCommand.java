package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class SaveCommand extends FacilioCommand {
    private FacilioModule module;

    public SaveCommand(FacilioModule module) {
        this.module = module;
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        //Copied from genericaddmoduledatacommand
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = (Map<String, List<ModuleBaseWithCustomFields>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);

        String moduleName = module.getName();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);

        List<FacilioField> fields = modBean.getAllFields(moduleName);

        InsertRecordBuilder<ModuleBaseWithCustomFields> insertRecordBuilder = new InsertRecordBuilder<>()
                .module(module)
                .fields(fields)
                ;

        Boolean setLocalId = (Boolean) context.get(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID);
        if (setLocalId != null && setLocalId) {
            insertRecordBuilder.withLocalId();
        }

        insertRecordBuilder.withChangeSet();
        insertRecordBuilder.ignoreSplNullHandling();

        List<ModuleBaseWithCustomFields> records = recordMap.get(module.getName());

        if (module.isCustom()) {
            for (ModuleBaseWithCustomFields record: records) {
                CommonCommandUtil.handleLookupFormData(fields, record.getData());
            }
        }

        if (CollectionUtils.isEmpty(records)) {
            throw new IllegalArgumentException("Record cannot be null during addition");
        }

        insertRecordBuilder.addRecords(records);

        //inserting multi select field values
        List<SupplementRecord> supplementFields = (List<SupplementRecord>) context.get(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS);
        if (CollectionUtils.isNotEmpty(supplementFields)) {
            insertRecordBuilder.insertSupplements(supplementFields);
        }

        insertRecordBuilder.save();





        Map<Long, List<UpdateChangeSet>> changeSet = insertRecordBuilder.getChangeSet();

        CommonCommandUtil.appendChangeSetMapToContext(context,changeSet,moduleName);

        return false;
    }
}
