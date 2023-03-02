package com.facilio.readingrule.rca.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingAlarmOccurrenceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readingrule.rca.context.RCAScoreReadingContext;
import com.facilio.readingrule.rca.util.ReadingRuleRcaAPI;
import com.facilio.time.DateRange;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class RcaReadingFaultCountAndDurationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<RCAScoreReadingContext> readingContexts = (List<RCAScoreReadingContext>) context.get(FacilioConstants.ReadingRules.RCA.RCA_SCORE_READINGS);
        List<Long> rcaAlarmIds = readingContexts.stream().map(x -> x.getRcaFault().getId()).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(readingContexts)) {
            RCAScoreReadingContext firstReading = readingContexts.get(0);
            ModuleBean modBean = Constants.getModBean();
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.READING_ALARM_OCCURRENCE);
            DateRange dateRange = ReadingRuleRcaAPI.getDateRange(context);

            List<FacilioField> fields = modBean.getAllFields(module.getName());
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

            String clearedTimeFieldColumn = fieldMap.get("clearedTime").getColumnName();
            String createdTimeFieldColumn = fieldMap.get("createdTime").getColumnName();

            // duration of an occurrence
            StringBuilder durationAggrColumn = new StringBuilder("SUM(COALESCE(")
                    .append(clearedTimeFieldColumn).append(",") //if the occurrence is cleared
                    .append(Math.min(System.currentTimeMillis(), dateRange.getEndTime())) // else
                    .append(") - ")
                    .append(createdTimeFieldColumn).append(")");

            List<FacilioField> selectFields = new ArrayList<>();
            FacilioField durationField = FieldFactory.getField("duration", durationAggrColumn.toString(), FieldType.NUMBER);
            selectFields.add(durationField);
            selectFields.addAll(FieldFactory.getCountField(module));
            selectFields.add(fieldMap.get("alarm"));

            SelectRecordsBuilder<ReadingAlarmOccurrenceContext> builder = new SelectRecordsBuilder<ReadingAlarmOccurrenceContext>().module(module)
                    .beanClass(ReadingAlarmOccurrenceContext.class).select(selectFields)
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("alarm"), rcaAlarmIds, NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), String.valueOf(firstReading.getRcaFault().getResource().getId()), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), dateRange.getStartTime() + "," + dateRange.getEndTime(), DateOperators.BETWEEN))
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("isNewReadingRule"), String.valueOf(true), BooleanOperators.IS))
                    .groupBy(fieldMap.get("alarm").getCompleteColumnName());

            List<Map<String, Object>> list = builder.getAsProps();
            List<Long> alarmsWithOccurrence = list.stream().map(RcaReadingFaultCountAndDurationCommand::getAlarmId).collect(Collectors.toList());
            readingContexts.forEach(reading -> nullDurationAndCount(reading, alarmsWithOccurrence));
            if (CollectionUtils.isNotEmpty(list)) {
                for (Map<String, Object> prop : list) {
                    Long alarmId = getAlarmId(prop);
                    Optional<RCAScoreReadingContext> readingContextOpt = readingContexts.stream().filter(x -> x.getRcaFault().getId() == alarmId).findFirst();
                    if (readingContextOpt.isPresent()) {
                        RCAScoreReadingContext readingContext = readingContextOpt.get();
                        readingContext.setDuration(((BigDecimal) prop.get("duration")).longValue());
                        readingContext.setCount((Long) prop.get("count"));
                    }
                }
            }
            context.put(FacilioConstants.ReadingRules.RCA.RCA_SCORE_READINGS, readingContexts);
        }

        return false;
    }

    private static Long getAlarmId(Map<String, Object> map) {
        Map<String, Object> alarm = (Map<String, Object>) map.get("alarm");
        return (Long) alarm.get("id");
    }

    private static void nullDurationAndCount(RCAScoreReadingContext reading, List<Long> alarmsWithOccurrence){
        if(!alarmsWithOccurrence.contains(reading.getRcaFault().getId())) {
            reading.setDuration(0L);
            reading.setCount(0L);
            reading.setScore(0L);
        }
    }
}
