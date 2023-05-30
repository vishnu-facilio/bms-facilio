package com.facilio.readingrule.rca.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingrule.rca.context.RCAScoreReadingContext;
import com.facilio.readingrule.rca.util.ReadingRuleRcaAPI;
import com.facilio.time.DateRange;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class RcaReadingFaultCountAndDurationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<RCAScoreReadingContext> readingContexts = (List<RCAScoreReadingContext>) context.get(FacilioConstants.ReadingRules.RCA.RCA_SCORE_READINGS);
        List<Long> rcaAlarmIds = readingContexts.stream().map(RCAScoreReadingContext::getRcaFaultId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(readingContexts)) {
            DateRange dateRange = ReadingRuleRcaAPI.getDateRange(context);

            List<Map<String, Object>> list = ReadingRuleRcaAPI.getAlarmDurationAndCount(rcaAlarmIds, dateRange.getStartTime(), dateRange.getEndTime());
            List<Long> alarmsWithOccurrence = list.stream().map(RcaReadingFaultCountAndDurationCommand::getAlarmId).collect(Collectors.toList());
            readingContexts.removeIf(reading -> !alarmsWithOccurrence.contains(reading.getRcaFaultId()));
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
}
