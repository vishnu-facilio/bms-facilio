package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

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
        ModuleBaseWithCustomFields record = recordMap.get(module.getName()).get(0);

        if(record != null) {
            String moduleName = module.getName();

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);

            List<FacilioField> fields = modBean.getAllFields(moduleName);

            InsertRecordBuilder<ModuleBaseWithCustomFields> insertRecordBuilder = new InsertRecordBuilder<ModuleBaseWithCustomFields>()
                    .module(module)
                    .fields(fields)
                    ;

            Boolean setLocalId = (Boolean) context.get(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID);
            if (setLocalId != null && setLocalId) {
                insertRecordBuilder.withLocalId();
            }

            Boolean withChangeSet = (Boolean) context.get(FacilioConstants.ContextNames.WITH_CHANGE_SET);
            if (withChangeSet != null && withChangeSet) {
                insertRecordBuilder.withChangeSet();
            }
            long id = insertRecordBuilder.insert(record);
            record.setId(id);
            context.put(FacilioConstants.ContextNames.RECORD_ID, id);
            if (withChangeSet != null && withChangeSet) {
                context.put(FacilioConstants.ContextNames.CHANGE_SET, insertRecordBuilder.getChangeSet());
            }
        }
        else {
            throw new IllegalArgumentException("Record cannot be null during addition");
        }
        return false;
    }
}
