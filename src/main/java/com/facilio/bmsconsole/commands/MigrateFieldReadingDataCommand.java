package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MigrateFieldReadingDataCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        long sourceId = (long) context.get(FacilioConstants.ContextNames.SOURCE_ID);

        long targetId = (long) context.get(FacilioConstants.ContextNames.TARGET_ID);
        List<Long> resources = (List<Long>) context.get(FacilioConstants.ContextNames.RESOURCE_LIST);

        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioField sourceField = bean.getField(sourceId);
        FacilioModule sourceModule = bean.getModule(sourceField.getModuleId());

        List<FacilioField> sourcefields = bean.getAllFields(sourceModule.getName());
        Map<String, FacilioField> sourcefieldMap = FieldFactory.getAsMap(sourcefields);
        int page = 0 ;
        int recordLimit = 5000;
        while (true) {
            SelectRecordsBuilder<ReadingContext> builder = new SelectRecordsBuilder<ReadingContext>()
                    .module(sourceModule)
                    .beanClass(ReadingContext.class)
                    .select(sourcefields)
                    .andCondition(CriteriaAPI.getCondition(sourceField, CommonOperators.IS_NOT_EMPTY));
            if (!resources.isEmpty()) {
                builder.andCondition(CriteriaAPI.getCondition(sourcefieldMap.get("parentId"),resources, NumberOperators.EQUALS));
            }
            page++;
            // int perPage = (int) pagination.get("perPage");

            int offset = ((page-1) * recordLimit);
            if (offset < 0) {
                offset = 0;
            }

            builder.offset(offset);
            builder.limit(recordLimit);
            List<ReadingContext> readingsList = builder.get();
            if (readingsList != null && !readingsList.isEmpty()) {

                addReading(targetId, sourceField.getName(), readingsList, resources);
                List<Long> readingDataIds = readingsList.stream().map(reading -> reading.getId()).collect(Collectors.toList());
                List<Long> parentIds = readingsList.stream().map(reading -> reading.getParentId()).collect(Collectors.toList());
                if (parentIds != null && parentIds.size() > 0) {
                    for (long assetId : parentIds) {
                        ReadingsAPI.deleteReadings(assetId, Collections.singletonList(sourceField), sourceModule, sourcefields, sourcefieldMap, readingDataIds, true);

                    }
                }
            } else {
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
        FacilioChain chain = TransactionChainFactory.onlyAddOrUpdateReadingsChain();
        chain.execute(context);
    }

}
