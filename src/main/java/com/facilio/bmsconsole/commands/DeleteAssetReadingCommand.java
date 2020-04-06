package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeleteAssetReadingCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(DeleteAssetReadingCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long startTime = (long) context.get(FacilioConstants.ContextNames.START_TIME);
        long endTime = (long) context.get(FacilioConstants.ContextNames.END_TIME);
//        List<Long> resourceList = (List<Long>) context.get(FacilioConstants.ContextNames.RESOURCE_LIST);
        long moduleId = (long) context.get(FacilioConstants.ContextNames.MODULE_ID);
        Long resourceList = (Long) context.get(FacilioConstants.ContextNames.ASSET_ID);
        long readingFieldId = (long) context.getOrDefault(FacilioConstants.ContextNames.FIELD_ID, -1);
        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule readingModule = bean.getModule(moduleId);
        List<FacilioField> readingsFields = new ArrayList<>();
        Map<String, FacilioField> sourcefieldMap ;

        FacilioField readingField = null;
        if (readingFieldId > 0) {
             readingField = bean.getField(readingFieldId);
             readingsFields.add(readingField);
             sourcefieldMap= FieldFactory.getAsMap(readingsFields);
        } else {
            readingsFields.addAll(bean.getAllFields(readingModule.getName()));
            sourcefieldMap= FieldFactory.getAsMap(readingsFields);
//            deleteModulesReading();
        }
        //  List<Long> readingDataIds = new ArrayList<>();
        List<Long> parentIds = new ArrayList<>();

        LOGGER.info("Delete Reading Started ");
        int recordLimit = 5000;
        while (true) {
            SelectRecordsBuilder<ReadingContext> builder = new SelectRecordsBuilder<ReadingContext>()
                    .module(readingModule)
                    .beanClass(ReadingContext.class)
                    .select(readingsFields)
                    .andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(), ""+readingModule.getModuleId(), NumberOperators.EQUALS));
            if (readingField != null) {
                builder.andCondition(CriteriaAPI.getCondition(readingField, CommonOperators.IS_NOT_EMPTY));
            }
            if (resourceList != null) {
                builder.andCondition(CriteriaAPI.getCondition(sourcefieldMap.get("parentId"),Collections.singletonList(resourceList), NumberOperators.EQUALS));
            }
            if (startTime > 0 && endTime > 0  ) {
                builder.andCondition(CriteriaAPI.getCondition(sourcefieldMap.get("ttime"), "" + startTime , NumberOperators.GREATER_THAN_EQUAL))
                        .andCondition(CriteriaAPI.getCondition(sourcefieldMap.get("ttime"), ""+endTime, NumberOperators.LESS_THAN_EQUAL));
            }
            int offset = 0;
            builder.offset(offset);
            builder.limit(recordLimit);
            List<ReadingContext> readingsList = builder.get();
            if (readingsList != null && !readingsList.isEmpty()) {
                List<Long> batchParentIds = readingsList.stream().map(reading -> reading.getParentId()).collect(Collectors.toList());
                List<Long> readingDataIds = readingsList.stream().map(reading -> reading.getId()).collect(Collectors.toList());
                LOGGER.debug("Reading Ids ---> " + readingDataIds.size() );
                LOGGER.debug("Reading Ids ---> " + readingDataIds );
                if (readingFieldId > 0) {
                    MigrateFieldReadingDataCommand.updateReadingsToNull(readingField, readingModule, readingsFields, sourcefieldMap, readingDataIds, true);
                    parentIds.addAll(readingsList.stream().map(reading -> reading.getParentId()).collect(Collectors.toList()));
                    // checkAndUpdateRdm(resourceList, readingField, endTime, startTime);
                } else {
                    deleteReadings(readingDataIds, readingModule);
//                    for (FacilioField field: readingsFields) {
//                        checkAndUpdateRdm(resourceList, field, endTime, startTime);
//                    }
                }
            }
            if (readingsList == null || readingsList.isEmpty() ) {
                LOGGER.info("Deletion Compeleted ");
                break;
            }
        }
        return false;
    }

    public static void deleteReadings(List<Long> readingDataIds, FacilioModule readingModule) throws Exception {

        if (readingDataIds != null || readingDataIds.size() != 0) {
            GenericDeleteRecordBuilder readingDelete = new GenericDeleteRecordBuilder();
            readingDelete.table(readingModule.getTableName())
                    .batchDeleteById(readingDataIds);
        }
    }
    public static void checkAndUpdateRdm (long resourceId, FacilioField field, long endTime, long startTime) throws Exception {
        ReadingDataMeta rdm = ReadingsAPI.getReadingDataMeta(resourceId, field);
        if (rdm.getTtime() > startTime && rdm.getTtime() < endTime) {
            MigrateFieldReadingDataCommand.deleteRdmEntry(resourceId, Collections.singletonList(field.getId()));
        }
    }

}
