package com.facilio.v3.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Log4j
public class SaveCommand extends FacilioCommand {
    private FacilioModule module;

    public SaveCommand(FacilioModule module) {
        this.module = module;
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = (Map<String, List<ModuleBaseWithCustomFields>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);

        Set<String> extendedModules = Constants.getExtendedModules(context); //For adding extended module which has module entry only for base module. Like Assets
        if (CollectionUtils.isEmpty(extendedModules)) {
            insertData((FacilioContext) context, module.getName(), recordMap.get(module.getName()));
        }
        else {
            for (String extendedModule : extendedModules) {
                insertData((FacilioContext) context, extendedModule, recordMap.get(extendedModule));
            }
        }
        return false;
    }

    private void insertData(FacilioContext context, String moduleName, List<ModuleBaseWithCustomFields> records) throws Exception {
        try {
            //Copied from genericaddmoduledatacommand
            ModuleBean modBean = Constants.getModBean();
            FacilioModule module = modBean.getModule(moduleName);

            List<FacilioField> fields = modBean.getAllFields(moduleName);
            if (AccountUtil.getCurrentOrg().getId() == 155 && FacilioConstants.Inspection.INSPECTION_TEMPLATE.equals(moduleName)) {
                LOGGER.info(MessageFormat.format("Fields of inspection template module : {0}", fields));
                LOGGER.info(MessageFormat.format("Records to be added : {0}", records));
            }

            InsertRecordBuilder<ModuleBaseWithCustomFields> insertRecordBuilder = new InsertRecordBuilder<>()
                    .module(module)
                    .fields(fields);

            Boolean setLocalId = (Boolean) context.get(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID);
            if (setLocalId != null && setLocalId) {
                insertRecordBuilder.withLocalId();
            }

            insertRecordBuilder.withChangeSet();
            insertRecordBuilder.ignoreSplNullHandling();

            if (module.isCustom()) {
                for (ModuleBaseWithCustomFields record : records) {
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

            CommonCommandUtil.appendChangeSetMapToContext(context, changeSet, moduleName);
        }
        catch (Exception e) {
            LOGGER.error(MessageFormat.format("Error occurred during save command for module : {0}", moduleName));
            throw e;
        }
    }
}
