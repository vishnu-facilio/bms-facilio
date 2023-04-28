package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MigrateFieldReadingDataCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(MigrateFieldReadingDataCommand.class.getName());


    @Override
    public boolean executeCommand(Context context) throws Exception {

        long sourceId = (long) context.get(FacilioConstants.ContextNames.SOURCE_ID);

        long targetId = (long) context.get(FacilioConstants.ContextNames.TARGET_ID);
        List<Long> resources = (List<Long>) context.get(FacilioConstants.ContextNames.RESOURCE_LIST);

        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioField sourceField = bean.getField(sourceId);
        FacilioModule sourceModule = bean.getModule(sourceField.getModuleId());
        //  List<Long> readingDataIds = new ArrayList<>();
        List<Long> parentIds = new ArrayList<>();

        List<FacilioField> sourcefields = bean.getAllFields(sourceModule.getName());
        Map<String, FacilioField> sourcefieldMap = FieldFactory.getAsMap(sourcefields);
        LOGGER.info("Field Migration Started ");
        int recordLimit = 5000;
        while (true) {
            SelectRecordsBuilder<ReadingContext> builder = new SelectRecordsBuilder<ReadingContext>()
                    .module(sourceModule)
                    .beanClass(ReadingContext.class)
                    .select(sourcefields)
                    .andCondition(CriteriaAPI.getCondition(sourceField, CommonOperators.IS_NOT_EMPTY));
            if (resources != null && !resources.isEmpty()) {
                builder.andCondition(CriteriaAPI.getCondition(sourcefieldMap.get("parentId"),resources, NumberOperators.EQUALS));
            }
            int offset = 0;
            builder.offset(offset);
            builder.limit(recordLimit);
            List<ReadingContext> readingsList = builder.get();
            if (readingsList != null && !readingsList.isEmpty()) {
                addReading(targetId, sourceField.getName(), readingsList, resources);
                List<Long> batchParentIds = readingsList.stream().map(reading -> reading.getParentId()).collect(Collectors.toList());
                List<Long> readingDataIds = readingsList.stream().map(reading -> reading.getId()).collect(Collectors.toList());
                LOGGER.debug("Reading Ids ---> " + readingDataIds.size() );
                LOGGER.debug("Reading Ids ---> " + readingDataIds );
                updateReadingsToNull(sourceField, sourceModule, sourcefields, sourcefieldMap, readingDataIds, true);
//                if (batchParentIds != null && batchParentIds.size() > 0) {
//                    for (long assetId : batchParentIds) {
//                        deleteReadings(assetId, sourceField, sourceModule, sourcefields, sourcefieldMap, readingDataIds, true);
//                    }
//                }
                parentIds.addAll(readingsList.stream().map(reading -> reading.getParentId()).collect(Collectors.toList()));
            } else {
                if (parentIds != null && parentIds.size() > 0) {
                    for (long assetId : parentIds) {
                        deleteRdmEntry(assetId, Collections.singletonList(sourceField.getId()));
                    }
                }
               //  break;
            }
            if (readingsList == null || readingsList.isEmpty() ) {
                LOGGER.info("Field Migration Compeleted ");
                break;
            }
        }
        return false;
    }

    private void addReading ( long targetId, String oldFieldName, List<ReadingContext> readingsList, List<Long> resources) throws Exception {

        List<ReadingContext> newList = new ArrayList<>();

        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField targetField = bean.getField(targetId);
        FacilioModule targetModule = bean.getModule(targetField.getModuleId());

        readingsList.forEach(reading -> {
            ReadingContext newReading = new ReadingContext();
            newReading.setParentId(reading.getParentId());

            Object value = reading.getReading(oldFieldName);
            newReading.addReading(targetField.getName(), value);

            newReading.setTtime(reading.getTtime());
            newList.add(newReading);
        });

        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, targetField.getModule().getName());
        context.put(FacilioConstants.ContextNames.READINGS, newList);
        context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.KINESIS);
        context.put(FacilioConstants.ContextNames.ADJUST_READING_TTIME, false);
        FacilioChain chain = TransactionChainFactory.onlyAddOrUpdateReadingsChain();
        chain.execute(context);
    }

    public static void updateReadingsToNull(FacilioField targetFields, FacilioModule module, List<FacilioField> fields, Map<String, FacilioField> fieldMap,List<Long> readingDataIds, Boolean... deleteReadings) throws Exception {

        ReadingContext reading = new ReadingContext();
        // assetFields.forEach(field -> {
            Object value;
            if (targetFields.getDataTypeEnum() == FieldType.NUMBER || targetFields.getDataTypeEnum() == FieldType.DECIMAL) {
                value = -99;
            }
            else {
                value = null;
            }
           //  reading.addReading(field.getName(), value);
       //  });

        if (deleteReadings == null || deleteReadings.length == 0 || deleteReadings[0]) {

            if (fields == null) {
                ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                fields = bean.getAllFields(module.getName());
                fieldMap = FieldFactory.getAsMap(fields);
            }
            List<GenericUpdateRecordBuilder.BatchUpdateByIdContext> batchUpdates = new ArrayList<>();
            for (Long id : readingDataIds) {
                GenericUpdateRecordBuilder.BatchUpdateByIdContext batchValue = new GenericUpdateRecordBuilder.BatchUpdateByIdContext();
                batchValue.setWhereId(id);
                batchValue.addUpdateValue("value", value);
                batchUpdates.add(batchValue);
            }

//            UpdateRecordBuilder<ReadingContext> updateBuilder = new UpdateRecordBuilder<ReadingContext>()
//                    .module(module)
//                    .fields(fields)
//                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(parentId), NumberOperators.EQUALS));
//            if (readingDataIds != null) {
//                updateBuilder.andCondition(CriteriaAPI.getIdCondition(readingDataIds, module));
//            }
//            updateBuilder.update(reading);

            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .table(module.getTableName())
                    .fields(Collections.singletonList(fieldMap.get(targetFields.getName())));
            builder.batchUpdateById(batchUpdates);
        }

    }

    public static void deleteRdmEntry(long parentId, List<Long> fieldIds) throws Exception {
        ReadingDataMeta rdm = new ReadingDataMeta();
        rdm.setValue("-1");
        rdm.setReadingDataId(-99);
        rdm.setInputType(ReadingDataMeta.ReadingInputType.WEB);
        rdm.setReadingType(ReadingDataMeta.ReadingType.READ);
        rdm.setIsControllable(false);
        rdm.setControlActionMode(ReadingDataMeta.ControlActionMode.SANDBOX.getIntVal());
        ReadingsAPI.updateReadingDataMeta(parentId, fieldIds, rdm);

    }
}
