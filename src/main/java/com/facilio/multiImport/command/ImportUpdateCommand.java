package com.facilio.multiImport.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.multiImport.constants.ImportConstants;
import com.facilio.multiImport.context.ImportRowContext;
import com.facilio.multiImport.enums.FieldTypeImportRowProcessor;
import com.facilio.multiImport.multiImportExceptions.ImportUpdateException;
import com.facilio.v3.commands.SaveOptions;
import com.facilio.v3.context.Constants;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
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
        Map<Long, ModuleBaseWithCustomFields> oldRecordMap = ImportConstants.getOldRecordsMap(context);


        SaveOptions defaultOptions = SaveOptions.of((FacilioContext) context);
        Set<String> extendedModules = Constants.getExtendedModules(context); //For adding extended module which has module entry only for base module. Like Assets
        if (CollectionUtils.isEmpty(extendedModules)) {
            removeErrorRecordsFromUpdateRecordMap(updateRecordMap,logIdVsRowContext, module.getName());
            Map<Long, List<UpdateChangeSet>> changeSet = updateData(module.getName(), defaultOptions, null, updateRecordMap.get(module.getName()),oldRecordMap,logIdVsRowContext);
            CommonCommandUtil.appendChangeSetMapToContext(context,changeSet,module.getName());
        }
        else {
            for (String extendedModule : extendedModules) {
                removeErrorRecordsFromUpdateRecordMap(updateRecordMap,logIdVsRowContext,extendedModule);
                SaveOptions saveOptions = Constants.getExtendedSaveOption(context, extendedModule);
                Map<Long, List<UpdateChangeSet>> changeSet = updateData(extendedModule, defaultOptions, saveOptions, updateRecordMap.get(extendedModule),oldRecordMap,logIdVsRowContext);
                CommonCommandUtil.appendChangeSetMapToContext(context, changeSet, extendedModule); // For automation of this module
                CommonCommandUtil.appendChangeSetMapToContext(context, changeSet, module.getName()); // For automation of parent module
            }
        }
        context.put(ImportConstants.UPDATE_RECORDS_COUNT, totalCount);

        return false;
    }

    private Map<Long, List<UpdateChangeSet>> updateData (String moduleName, SaveOptions defaultOptions, SaveOptions options,
                                                         List<Pair<Long,ModuleBaseWithCustomFields>> records,
                                                         Map<Long, ModuleBaseWithCustomFields> oldRecordMap,
                                                         Map<Long, ImportRowContext> logIdVsRowContext) throws Exception {
        if(CollectionUtils.isEmpty(records)){
            return Collections.EMPTY_MAP;
        }
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(moduleName);

        Class beanClass = getBeanClass(defaultOptions, options);
        List<FacilioField> fields = getPatchFields(defaultOptions, options);
        if (CollectionUtils.isEmpty(fields)) {
           return Collections.EMPTY_MAP;
        }

        Map<String,FacilioField> patchFieldMap = FieldFactory.getAsMap(fields);
        Set<String> patchFieldNames = patchFieldMap.keySet();

        Map<Long, List<UpdateChangeSet>> changeSet = new HashMap<>();
        for (Pair<Long,ModuleBaseWithCustomFields> logIdVsRecord: records) {
            if(logIdVsRecord == null) {
                continue;
            }
            long logId = logIdVsRecord.getKey();
            ImportRowContext rowContext = logIdVsRowContext.get(logId);
            long rowNumber = rowContext.getRowNumber();
            ModuleBaseWithCustomFields record = logIdVsRecord.getValue();
            ModuleBaseWithCustomFields oldRecord = oldRecordMap.get(logId);

            if(record ==  null || record.getId()<0){
                continue;
            }

            long recordId = record.getId();

            try{
                UpdateRecordBuilder<ModuleBaseWithCustomFields> updateBuilder = new UpdateRecordBuilder<>()
                        .module(module)
                        .fields(fields)
                        .andCondition(CriteriaAPI.getIdCondition(recordId, module));


                updateBuilder.ignoreSplNullHandling();

                //updating multi select field values
                addSupplements(updateBuilder, defaultOptions);
                addSupplements(updateBuilder, options);

                updateBuilder.update(record);
                changeSet.putAll(constructChangeSet(record,oldRecord,patchFieldNames,patchFieldMap));
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

    private Map<Long, List<UpdateChangeSet>> constructChangeSet(ModuleBaseWithCustomFields updateRecord, ModuleBaseWithCustomFields oldRecord,
                                                                Set<String> patchFieldNames, Map<String,FacilioField> patchFieldMap) throws Exception{

        Map<Long, List<UpdateChangeSet>> updateChangeSet = new HashMap<>();
        long id = updateRecord.getId();
        Map<String, Object> updateProps = FieldUtil.getAsProperties(updateRecord);
        Map<String, Object> oldProps = FieldUtil.getAsProperties(oldRecord);
        updateLookupFields(updateProps,patchFieldMap.values());
        updateLookupFields(oldProps,patchFieldMap.values());

        MapDifference<String, Object> difference = Maps.difference(oldProps, updateProps);
        List<UpdateChangeSet> currentChangeList = new ArrayList<>();
        getNewValues(id, patchFieldNames, patchFieldMap, difference.entriesOnlyOnRight(), currentChangeList);
        getDifference(id, patchFieldNames, patchFieldMap, difference.entriesDiffering(), currentChangeList);
        updateChangeSet.put(id, currentChangeList);

        return updateChangeSet;
    }
    private void getNewValues(long recordId, Set<String> fieldNames, Map<String, FacilioField> fieldMap, Map<String, Object> difference, List<UpdateChangeSet> changeList) {
        for(Map.Entry<String, Object> entry : difference.entrySet()) {
            String fieldName = entry.getKey();
            if (fieldNames.contains(fieldName)) {
                FacilioField field = fieldMap.get(fieldName);
                FieldTypeImportRowProcessor  fieldTypeImportRowProcessor = FieldTypeImportRowProcessor.getFieldTypeImportRowProcessor(field.getDataTypeEnum());
                List<UpdateChangeSet> currentChange = fieldTypeImportRowProcessor.constructChangeSet(recordId,field,null,entry.getValue());
                if(CollectionUtils.isNotEmpty(currentChange)){
                    changeList.addAll(currentChange);
                }
            }
        }
    }

    private void getDifference(long recordId, Set<String> fieldNames, Map<String, FacilioField> fieldMap, Map<String, MapDifference.ValueDifference<Object>> difference, List<UpdateChangeSet> changeList) {
        for(Map.Entry<String, MapDifference.ValueDifference<Object>> entry : difference.entrySet()) {
            String fieldName = entry.getKey();
            if (fieldNames.contains(fieldName)) {
                FacilioField field = fieldMap.get(fieldName);
                FieldTypeImportRowProcessor  fieldTypeImportRowProcessor = FieldTypeImportRowProcessor.getFieldTypeImportRowProcessor(field.getDataTypeEnum());
                List<UpdateChangeSet> currentChange = fieldTypeImportRowProcessor.constructChangeSet(recordId,field,entry.getValue().leftValue(),entry.getValue().rightValue());
                if(CollectionUtils.isNotEmpty(currentChange)){
                    changeList.addAll(currentChange);
                }
            }
        }
    }
    private void updateLookupFields(Map<String, Object> moduleProps, Collection<FacilioField> fields) throws Exception {
        for (FacilioField field : fields) {
            if (field.getDataTypeEnum() == FieldType.LOOKUP) {
                Map<String, Object> lookupProps = (Map<String, Object>) moduleProps.get(field.getName());
                if (lookupProps != null && !lookupProps.isEmpty()) {
                    if (lookupProps.get("id") != null) {
                        moduleProps.put(field.getName(), lookupProps.get("id"));
                    } else {
                        moduleProps.put(field.getName(), null);
                    }
                }
            }
        }
    }


}

