package com.facilio.multiImport.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.multiImport.constants.ImportConstants;
import com.facilio.multiImport.context.ImportRowContext;
import com.facilio.multiImport.multiImportExceptions.ImportUpdateException;
import com.facilio.v3.commands.SaveOptions;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.logging.Logger;

public class ImportUpdateCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(ImportUpdateCommand.class.getName());
    private FacilioModule module;
    private Context context;

    public ImportUpdateCommand(FacilioModule module) {
        this.module = module;
    }

    private int totalCount = 0;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        this.context = context;
        Map<String, List<Pair<Long,ModuleBaseWithCustomFields>>> updateRecordMap = ImportConstants.getUpdateRecordMap(context);
        Map<Long, ImportRowContext> logIdVsRowContext = ImportConstants.getLogIdVsRowContextMap(context);


        SaveOptions defaultOptions = SaveOptions.of((FacilioContext) context);
        Set<String> extendedModules = Constants.getExtendedModules(context); //For adding extended module which has module entry only for base module. Like Assets
        if (CollectionUtils.isEmpty(extendedModules)) {
            removeErrorRecordsFromUpdateRecordMap(updateRecordMap,logIdVsRowContext, module.getName());
            Map<Long, List<UpdateChangeSet>> changeSet = updateData(module.getName(), defaultOptions, null, updateRecordMap.get(module.getName()),logIdVsRowContext);
            CommonCommandUtil.appendChangeSetMapToContext(context,changeSet,module.getName());
        }
        else {
            for (String extendedModule : extendedModules) {
                removeErrorRecordsFromUpdateRecordMap(updateRecordMap,logIdVsRowContext,extendedModule);
                SaveOptions saveOptions = Constants.getExtendedSaveOption(context, extendedModule);
                Map<Long, List<UpdateChangeSet>> changeSet = updateData(extendedModule, defaultOptions, saveOptions, updateRecordMap.get(extendedModule),logIdVsRowContext);
                CommonCommandUtil.appendChangeSetMapToContext(context, changeSet, extendedModule); // For automation of this module
                CommonCommandUtil.appendChangeSetMapToContext(context, changeSet, module.getName()); // For automation of parent module
            }
        }
        context.put(ImportConstants.UPDATE_RECORDS_COUNT, totalCount);

        return false;
    }

    private Map<Long, List<UpdateChangeSet>> updateData (String moduleName, SaveOptions defaultOptions, SaveOptions options, List<Pair<Long,ModuleBaseWithCustomFields>> records, Map<Long, ImportRowContext> logIdVsRowContext) throws Exception {
        if(CollectionUtils.isEmpty(records)){
            return Collections.EMPTY_MAP;
        }
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(moduleName);

        Class beanClass = getBeanClass(defaultOptions, options);
        List<FacilioField> fields = getPatchFields(defaultOptions, options);
        if (fields == null) {
            fields = modBean.getAllFields(moduleName);
        }
        Map<Long, List<UpdateChangeSet>> changeSet = new HashMap<>();
        for (Pair<Long,ModuleBaseWithCustomFields> logIdVsRecord: records) {
            if(logIdVsRecord == null) {
                continue;
            }
            long logId = logIdVsRecord.getKey();
            ImportRowContext rowContext = logIdVsRowContext.get(logId);
            long rowNumber = rowContext.getRowNumber();
            ModuleBaseWithCustomFields record = logIdVsRecord.getValue();

            if(record ==  null || record.getId()<0){
                continue;
            }

            long recordId = record.getId();

            try{
                UpdateRecordBuilder<ModuleBaseWithCustomFields> updateBuilder = new UpdateRecordBuilder<>()
                        .module(module)
                        .fields(fields)
                        .andCondition(CriteriaAPI.getIdCondition(recordId, module));

                updateBuilder.withChangeSet(beanClass);

                updateBuilder.ignoreSplNullHandling();

                //updating multi select field values
                addSupplements(updateBuilder, defaultOptions);
                addSupplements(updateBuilder, options);

                updateBuilder.update(record);
                changeSet.putAll(updateBuilder.getChangeSet());
                totalCount++;
            }catch (Exception e){
                LOGGER.severe("Import update failed for logId:"+logId+"  Exception:"+e);
                rowContext.setErrorOccurredRow(true);
                ImportUpdateException exception = new ImportUpdateException(e);
                rowContext.setErrorMessage(exception.getClientMessage());
            }

        }
        return changeSet;
    }

    private Class getBeanClass (SaveOptions defaultOptions, SaveOptions options) {
        return options != null && options.getBeanClass() != null ? options.getBeanClass() :
                defaultOptions != null ? defaultOptions.getBeanClass() : null;
    }

    private List<FacilioField> getPatchFields (SaveOptions defaultOptions, SaveOptions options) {
        List<FacilioField> fields = null;
        fields = getPatchFields(defaultOptions, fields);
        fields = getPatchFields(options, fields);
        return fields;
    }

    private List<FacilioField> getPatchFields (SaveOptions options, List<FacilioField> fields) {
        if (options != null && CollectionUtils.isNotEmpty(options.getFields())) {
            if (fields != null) {
                fields.addAll(options.getFields());
            }
            else {
                return options.getFields();
            }
        }
        return fields;
    }

    private void addSupplements (UpdateRecordBuilder updateRecordBuilder, SaveOptions options) {
        Collection<SupplementRecord> supplementFields = options == null ? null : options.getSupplements();
        if (CollectionUtils.isNotEmpty(supplementFields)) {
            updateRecordBuilder.updateSupplements(supplementFields);
        }
    }
    private void removeErrorRecordsFromUpdateRecordMap(Map<String, List<Pair<Long,ModuleBaseWithCustomFields>>> updateRecordMap, Map<Long, ImportRowContext> logIdVsRowContext,String moduleName){
        List<Pair<Long,ModuleBaseWithCustomFields>> records = updateRecordMap.get(moduleName);
        List<ModuleBaseWithCustomFields> importUpdateRecords = new LinkedList<>();

        Map<String,List<ModuleBaseWithCustomFields>> recordMap = (Map<String,List<ModuleBaseWithCustomFields>>)context.getOrDefault(Constants.RECORD_MAP,new HashMap<String,List<ModuleBaseWithCustomFields>>());

        if (CollectionUtils.isEmpty(records)){
            recordMap.put(moduleName,importUpdateRecords);
            return;
        }

        records.removeIf(pair -> {
            long logId = pair.getKey();
            if (logIdVsRowContext.get(logId).isErrorOccurredRow()){
                return true;
            }
            importUpdateRecords.add(pair.getValue());
            return false;
        });
        recordMap.put(moduleName, importUpdateRecords);
        Constants.setRecordMap(context,recordMap);
    }
}

