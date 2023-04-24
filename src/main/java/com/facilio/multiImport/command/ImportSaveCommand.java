package com.facilio.multiImport.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.multiImport.constants.ImportConstants;
import com.facilio.multiImport.context.ImportRowContext;
import com.facilio.v3.commands.SaveOptions;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.text.MessageFormat;
import java.util.*;

@Log4j
public class ImportSaveCommand extends FacilioCommand {
    private FacilioModule module;
    private Context context;
    private int totalCount = 0;

    public ImportSaveCommand(FacilioModule module) {
        this.module = module;
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        this.context = context;
        Map<String, List<Pair<Long,ModuleBaseWithCustomFields>>> insertRecordMap = ImportConstants.getInsertRecordMap(context);
        Map<Long, ImportRowContext> logIdVsRowContext = ImportConstants.getLogIdVsRowContextMap(context);
        Map<String,List<ModuleBaseWithCustomFields>> recordMap = removeErrorRecordsFromInsertRecordMap(insertRecordMap,logIdVsRowContext,module.getName());

        SaveOptions defaultOptions = SaveOptions.of((FacilioContext) context);
        Set<String> extendedModules = Constants.getExtendedModules(context); //For adding extended module which has module entry only for base module. Like Assets
        if (CollectionUtils.isEmpty(extendedModules)) {
            Map<Long, List<UpdateChangeSet>> changeSet = insertData(module.getName(), defaultOptions, null, recordMap.get(module.getName()));
            CommonCommandUtil.appendChangeSetMapToContext(context, changeSet, module.getName());
        }
        else {
            for (String extendedModule : extendedModules) {
                removeErrorRecordsFromInsertRecordMap(insertRecordMap,logIdVsRowContext,extendedModule);
                SaveOptions saveOptions = Constants.getExtendedSaveOption(context, extendedModule);
                Map<Long, List<UpdateChangeSet>> changeSet = insertData(extendedModule, defaultOptions, saveOptions, recordMap.get(extendedModule));
                CommonCommandUtil.appendChangeSetMapToContext(context, changeSet, extendedModule); // For automation of this module
                CommonCommandUtil.appendChangeSetMapToContext(context, changeSet, module.getName()); // For automation of parent module
            }
        }

        context.put(ImportConstants.INSERT_RECORDS_COUNT,totalCount);
        return false;
    }

    private Map<Long, List<UpdateChangeSet>> insertData(String moduleName, SaveOptions defaultOptions, SaveOptions options, List<ModuleBaseWithCustomFields> records) throws Exception {
        try {

            if (CollectionUtils.isEmpty(records)) {
                return Collections.EMPTY_MAP;
            }

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

            insertRecordBuilder.addRecords(records);

            //inserting multi select field values
            addSupplements(insertRecordBuilder, defaultOptions);
            addSupplements(insertRecordBuilder, options);

            insertRecordBuilder.save();
            totalCount += records.size();

            Map<Long, List<UpdateChangeSet>> changeSet = insertRecordBuilder.getChangeSet();

            return changeSet;
        }
        catch (Exception e) {
            LOGGER.error(MessageFormat.format("Error occurred during ImportSave command for module : {0}", moduleName));
            throw e;
        }
    }

    private void addSupplements (InsertRecordBuilder insertRecordBuilder, SaveOptions options) {
        Collection<SupplementRecord> supplementFields = options == null ? null : options.getSupplements();
        if (CollectionUtils.isNotEmpty(supplementFields)) {
            insertRecordBuilder.insertSupplements(supplementFields);
        }
    }

    private boolean isSetLocalId (SaveOptions options) {
        return options != null && options.isSetLocalId();
    }
    private Map<String,List<ModuleBaseWithCustomFields>> removeErrorRecordsFromInsertRecordMap(Map<String, List<Pair<Long,ModuleBaseWithCustomFields>>> insertRecordMap, Map<Long, ImportRowContext> logIdVsRowContext,String moduleName){
        List<Pair<Long,ModuleBaseWithCustomFields>> records = insertRecordMap.get(moduleName);
        if (CollectionUtils.isEmpty(records)){
            return Collections.EMPTY_MAP;
        }
        List<ModuleBaseWithCustomFields> importRecords = new LinkedList<>();
        records.removeIf(pair -> {
            long logId = pair.getKey();
            ImportRowContext importRowContext = logIdVsRowContext.get(logId);
            if (importRowContext.isErrorOccurredRow()){
                importRowContext.setRowStatus(ImportRowContext.RowStatus.ADDING_FAILED);
                return true;
            }
            importRecords.add(pair.getValue());
            return false;
        });

        Map<String,List<ModuleBaseWithCustomFields>> recordMap = (Map<String,List<ModuleBaseWithCustomFields>>)context.getOrDefault(Constants.RECORD_MAP,new HashMap<String,List<ModuleBaseWithCustomFields>>());
        recordMap.put(moduleName, importRecords);
        Constants.setRecordMap(context,recordMap);

        return recordMap;
    }
}
