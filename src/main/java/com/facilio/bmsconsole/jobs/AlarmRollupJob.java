package com.facilio.bmsconsole.jobs;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingAlarmOccurrenceContext;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AlarmRollupJob extends FacilioJob {

    protected final long TRANSACTION_TIME = 1800000 / 2;

    private final long HOUR_IN_MILLIS = 60 * 60 * 1000;
    private final long DAY_IN_MILLIS = 24 * HOUR_IN_MILLIS;

    protected long getPreviousDayEndTime() {
        return DateTimeUtil.getDayStartTime() - 1; // previous day
    }

    protected long getNextRolledUpDate(long lastRolledUpDate) {
        long nextRolledUpDate = lastRolledUpDate + DAY_IN_MILLIS;

        long dayStartTime = getPreviousDayEndTime();
        if (nextRolledUpDate > dayStartTime) {
            nextRolledUpDate = dayStartTime;
        }
        return nextRolledUpDate;
    }

    protected Map getLastRolledUpDate(FacilioModule module, List<FacilioField> fields) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(fields)
                ;
        Map<String, Object> map = builder.fetchFirst();
        return map;
    }

    protected long getLastRolledUpDateFromOccurrence() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule readingAlarmOccurrence = modBean.getModule(FacilioConstants.ContextNames.READING_ALARM_OCCURRENCE);
        FacilioField createdTimeField = FieldFactory.getField("createdTime", "CREATED_TIME", FieldType.NUMBER);
        SelectRecordsBuilder<ReadingAlarmOccurrenceContext> occurrenceBuilder = new SelectRecordsBuilder<ReadingAlarmOccurrenceContext>()
                .aggregate(BmsAggregateOperators.NumberAggregateOperator.MIN, createdTimeField)
                .module(readingAlarmOccurrence);
        List<Map<String, Object>> props = occurrenceBuilder.getAsProps();
        if (CollectionUtils.isNotEmpty(props)) {
            return (long) props.get(0).get("createdTime");
        }

        return -1l;
    }

    protected Long updateOneTimeJob(Long nextRolledUpDate, Long lastOccurredTime) throws Exception {
        if (nextRolledUpDate < getPreviousDayEndTime()) {
            if (nextRolledUpDate < lastOccurredTime) {
                return nextRolledUpDate;
            }
            else {
                nextRolledUpDate = getPreviousDayEndTime();
            }
        }

        return nextRolledUpDate;
    }

    protected void scheduleNextJob(Long nextRolledUpDate, final String jobName, JobContext jc) throws Exception {
        if (nextRolledUpDate < getPreviousDayEndTime()) {
            long nextExecutionTime = (System.currentTimeMillis() + (1000 * 60 * 1)) / 1000;
            // schedule next job in next one hour
            if (jobName.equals(jc.getJobName())) {
                jc.setNextExecutionTime(nextExecutionTime); // set the next execution time, so that scheduler will run this job again
            } else {
                FacilioTimer.scheduleOneTimeJobWithTimestampInSec(AccountUtil.getCurrentOrg().getOrgId(),
                        jobName, nextExecutionTime,
                        "facilio");
            }
        }
        else {
            if ("RuleRollupJob-OneTime".equals(jc.getJobName())) {
                jc.setNextExecutionTime(-1);
            }
        }
    }

    protected void updateSummaryTable(FacilioModule module, List<FacilioField> fields,
                                      Map<String, Object> map, long nextRolledUpDate) throws Exception {
        if (map == null) {
            map = new HashMap<>();
            map.put("lastRolledUpDate", nextRolledUpDate);

            GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                    .fields(fields)
                    .table(module.getTableName());
            insertRecordBuilder.insert(map);
        }
        else {
            map.put("lastRolledUpDate", nextRolledUpDate);

            GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                    .fields(fields)
                    .table(module.getTableName())
                    .andCondition(CriteriaAPI.getIdCondition((Long) map.get("id"), module));
            updateBuilder.update(map);
        }
    }
}
