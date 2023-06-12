package com.facilio.v3.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
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
import java.util.Collection;
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

        SaveOptions defaultOptions = SaveOptions.of((FacilioContext) context);
        Set<String> extendedModules = Constants.getExtendedModules(context); //For adding extended module which has module entry only for base module. Like Assets
        if (CollectionUtils.isEmpty(extendedModules)) {
            Map<Long, List<UpdateChangeSet>> changeSet = insertData(module.getName(), defaultOptions, null, recordMap.get(module.getName()));
            CommonCommandUtil.appendChangeSetMapToContext(context, changeSet, module.getName());
        }
        else {
            for (String extendedModule : extendedModules) {
                SaveOptions saveOptions = Constants.getExtendedSaveOption(context, extendedModule);
                Map<Long, List<UpdateChangeSet>> changeSet = insertData(extendedModule, defaultOptions, saveOptions, recordMap.get(extendedModule));
                CommonCommandUtil.appendChangeSetMapToContext(context, changeSet, extendedModule); // For automation of this module
                CommonCommandUtil.appendChangeSetMapToContext(context, changeSet, module.getName()); // For automation of parent module
            }
        }
        return false;
    }

    private Map<Long, List<UpdateChangeSet>> insertData(String moduleName, SaveOptions defaultOptions, SaveOptions options, List<ModuleBaseWithCustomFields> records) throws Exception {
        try {
            //Copied from genericaddmoduledatacommand
            ModuleBean modBean = Constants.getModBean();
            FacilioModule module = modBean.getModule(moduleName);

            List<FacilioField> fields = modBean.getAllFields(moduleName);

            InsertRecordBuilder<ModuleBaseWithCustomFields> insertRecordBuilder = new InsertRecordBuilder<>()
                    .module(module)
                    .fields(fields);

            if (isSetLocalId(defaultOptions) || isSetLocalId(options)) {
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
            
            if (AccountUtil.getCurrentOrg().getId() == 172l && FacilioConstants.ContextNames.SERVICE_REQUEST.equals(moduleName)) {
                LOGGER.info(MessageFormat.format("Fields of SR module : {0}", fields));
                LOGGER.info(MessageFormat.format("Records to be added : {0}", records));
            }

            insertRecordBuilder.addRecords(records);

            //inserting multi select field values
            addSupplements(insertRecordBuilder, defaultOptions);
            addSupplements(insertRecordBuilder, options);

            insertRecordBuilder.save();

            Map<Long, List<UpdateChangeSet>> changeSet = insertRecordBuilder.getChangeSet();

            return changeSet;
        }
        catch (Exception e) {
            LOGGER.error(MessageFormat.format("Error occurred during save command for module : {0}", moduleName));
            throw e;
        }
    }

    private void addSupplements (InsertRecordBuilder insertRecordBuilder, SaveOptions options) {
        Collection<SupplementRecord> supplementFields = options == null ? null : options.getSupplements();
        LOGGER.info("Save Command - Supplement Field - "+supplementFields);
        if (CollectionUtils.isNotEmpty(supplementFields)) {
            insertRecordBuilder.insertSupplements(supplementFields);
        }
    }

    private boolean isSetLocalId (SaveOptions options) {
        return options != null && options.isSetLocalId();
    }
}
