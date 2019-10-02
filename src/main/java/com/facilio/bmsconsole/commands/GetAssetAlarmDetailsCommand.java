package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflows.functions.FacilioFieldFunctions;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAssetAlarmDetailsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> assetIds = (List<Long>) context.get("assetIds");
        if (CollectionUtils.isNotEmpty(assetIds)) {

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            FacilioModule baseAlarmModule = modBean.getModule(FacilioConstants.ContextNames.BASE_ALARM);
            FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
            FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);

            List<FacilioField> fields = new ArrayList<>();
            FacilioField alarmResourceField = modBean.getField("resource", baseAlarmModule.getName());
            fields.add(alarmResourceField);

            FacilioField severityField = modBean.getField("severity", baseAlarmModule.getName());
            fields.add(severityField);

            fields.addAll(FieldFactory.getCountField(baseAlarmModule));

            AlarmSeverityContext clearSeverity = AlarmAPI.getAlarmSeverity("Clear");

            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .select(fields)
                    .table(baseAlarmModule.getTableName())
                    .innerJoin(assetModule.getTableName())
                        .on(baseAlarmModule.getTableName() + ".RESOURCE_ID = " + assetModule.getTableName() + ".ID")
                    .andCondition(CriteriaAPI.getCondition(severityField, String.valueOf(clearSeverity.getId()), NumberOperators.NOT_EQUALS))
                    .groupBy(alarmResourceField.getCompleteColumnName() + ", " + severityField.getCompleteColumnName())
                    .andCondition(CriteriaAPI.getCondition(alarmResourceField, assetIds, NumberOperators.EQUALS))
                    ;

            List<Map<String, Object>> maps = builder.get();
            Map assetMap = new HashMap();
            for (Map<String, Object> map : maps) {
                long resourceId = ((Number) map.get("resource")).longValue();
                List list = (List) assetMap.get(resourceId);
                if (list == null) {
                    list = new ArrayList();
                    assetMap.put(resourceId, list);
                }
                Map<String, Long> severityMap = new HashMap<>();
                severityMap.put("severityId", (Long) map.get("severity"));
                severityMap.put("count", (Long) map.get("count"));
                list.add(severityMap);
            }

            context.put("assetAlarmCount", assetMap);

            String readingModuleName = (String) context.get("readingModule");
            List<String> readingFields = (List<String>) context.get("readingFields");
            if (StringUtils.isNotEmpty(readingModuleName) && CollectionUtils.isNotEmpty(readingFields)) {
                Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(readingModuleName));
                List<FacilioField> readingFieldList = new ArrayList<>();
                for (String fieldName : readingFields) {
                    readingFieldList.add(fieldMap.get(fieldName));
                }

                List<ReadingDataMeta> readingDataMetaList = ReadingsAPI.getReadingDataMetaList(assetIds, readingFieldList, true, null, null);
                if (CollectionUtils.isNotEmpty(readingDataMetaList)) {
                    Map<Long, List<ReadingDataMeta>> assetReadingDataMeta = new HashMap<>();
                    for (ReadingDataMeta rdm : readingDataMetaList) {
                        List<ReadingDataMeta> list = assetReadingDataMeta.get(rdm.getResourceId());
                        if (list == null) {
                            list = new ArrayList<>();
                            assetReadingDataMeta.put(rdm.getResourceId(), list);
                        }
                        list.add(rdm);
                    }
                    context.put("assetReadings", assetReadingDataMeta);
                }
                System.out.println(readingDataMetaList);
            }
        }
        return false;
    }
}
