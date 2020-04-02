package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
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
        Long resourceList = (Long) context.get(FacilioConstants.ContextNames.ASSET_ID);
        long readingFieldId = (long) context.get(FacilioConstants.ContextNames.FIELD_ID);
        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioField readingField = bean.getField(readingFieldId);
        FacilioModule readingModule = bean.getModule(readingField.getModuleId());
        //  List<Long> readingDataIds = new ArrayList<>();
        List<Long> parentIds = new ArrayList<>();

        List<FacilioField> readingsFields = bean.getAllFields(readingModule.getName());
        Map<String, FacilioField> sourcefieldMap = FieldFactory.getAsMap(readingsFields);
        LOGGER.info("Delete Reading Started ");
        int recordLimit = 5000;
        while (true) {
            SelectRecordsBuilder<ReadingContext> builder = new SelectRecordsBuilder<ReadingContext>()
                    .module(readingModule)
                    .beanClass(ReadingContext.class)
                    .select(readingsFields)
                    .andCondition(  CriteriaAPI.getCondition(readingField, CommonOperators.IS_NOT_EMPTY));
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
                MigrateFieldReadingDataCommand.deleteReadings(readingField, readingModule, readingsFields, sourcefieldMap, readingDataIds, true);
                parentIds.addAll(readingsList.stream().map(reading -> reading.getParentId()).collect(Collectors.toList()));
            }
            if (readingsList == null || readingsList.isEmpty() ) {
                LOGGER.info("Deletion Compeleted ");
                break;
            }
        }
        return false;
    }
}
