package com.facilio.bmsconsole.jobs;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.RuleRollupCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.bmsconsole.context.ReadingAlarmOccurrenceContext;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleRollupJob extends FacilioJob {

    private final long HOUR_IN_MILLIS = 60 * 60 * 1000;
    private final long DAY_IN_MILLIS = 24 * HOUR_IN_MILLIS;

    @Override
    public void execute(JobContext jc) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getRuleRollupSummaryModule().getTableName())
                .select(FieldFactory.getRuleRollupSummaryFields())
                ;
        Map<String, Object> map = builder.fetchFirst();


        long lastRolledUpDate = -1;
        if (MapUtils.isNotEmpty(map)) {
            if (map.containsKey("lastRolledUpDate")) {
                lastRolledUpDate = (long) map.get("lastRolledUpDate");
            }
        }

        if (lastRolledUpDate <= 0) {
            FacilioModule readingAlarmOccurrence = modBean.getModule(FacilioConstants.ContextNames.READING_ALARM_OCCURRENCE);
            FacilioField createdTimeField = FieldFactory.getField("createdTime", "CREATED_TIME", FieldType.NUMBER);
            SelectRecordsBuilder<ReadingAlarmOccurrenceContext> occurrenceBuilder = new SelectRecordsBuilder<ReadingAlarmOccurrenceContext>()
                    .aggregate(BmsAggregateOperators.NumberAggregateOperator.MIN, createdTimeField)
                    .module(readingAlarmOccurrence);
            List<Map<String, Object>> props = occurrenceBuilder.getAsProps();
            if (CollectionUtils.isNotEmpty(props)) {
                lastRolledUpDate = (long) props.get(0).get("createdTime");
            }
        }

        if (lastRolledUpDate == -1) {
            // No occurrence found
            return;
        }

        long nextRolledUpDate = lastRolledUpDate + DAY_IN_MILLIS;

        long dayStartTime = DateTimeUtil.getDayStartTime() - 1; // previous day
        if (nextRolledUpDate > dayStartTime) {
            nextRolledUpDate = dayStartTime;
        }

        FacilioModule alarmModule = modBean.getModule(FacilioConstants.ContextNames.NEW_READING_ALARM);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(alarmModule.getName()));
        SelectRecordsBuilder<ReadingAlarm> ruleBuilder = new SelectRecordsBuilder<ReadingAlarm>()
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.DISTINCT, fieldMap.get("rule"))
                .module(alarmModule);
        if (lastRolledUpDate > 0) {
            ruleBuilder.andCondition(CriteriaAPI.getCondition("LAST_OCCURRED_TIME", "lastOccurredTime",
                    String.valueOf(lastRolledUpDate), NumberOperators.GREATER_THAN));
        }
        List<Map<String, Object>> props = ruleBuilder.getAsProps();
        if (CollectionUtils.isNotEmpty(props)) {
            for (Map<String, Object> prop : props) {
                Long ruleId = (Long) prop.get("rule");

                FacilioChain ruleRollupChain = TransactionChainFactory.getRuleRollupChain();
                Context ruleRollupCommand = ruleRollupChain.getContext();
                ruleRollupCommand.put(FacilioConstants.ContextNames.ID, ruleId);
                ruleRollupCommand.put(FacilioConstants.ContextNames.ROLL_UP_TYPE, RuleRollupCommand.RollupType.RULE);
                ruleRollupCommand.put(FacilioConstants.ContextNames.START_TIME, lastRolledUpDate);
                ruleRollupCommand.put(FacilioConstants.ContextNames.END_TIME, nextRolledUpDate);
                ruleRollupChain.execute();
            }
        }

        if (nextRolledUpDate < dayStartTime) {
            Long lastOccurredTime = NewAlarmAPI.getReadingAlarmLastOccurredTime();
            if (nextRolledUpDate < lastOccurredTime) {
                long nextExecutionTime = (System.currentTimeMillis() + (1000 * 60 * 1)) / 1000;
                // schedule next job in next one hour
                if ("RuleRollupJob-OneTime".equals(jc.getJobName())) {
                    jc.setNextExecutionTime(nextExecutionTime);
                } else {
                    FacilioTimer.scheduleOneTimeJobWithTimestampInSec(AccountUtil.getCurrentOrg().getOrgId(),
                            "RuleRollupJob-OneTime", nextExecutionTime,
                            "facilio");
                }
            }
            else {
                nextRolledUpDate = dayStartTime;
            }
        }
        else {
            if ("RuleRollupJob-OneTime".equals(jc.getJobName())) {
                jc.setNextExecutionTime(-1);
            }
        }

        if (map == null) {
            map = new HashMap<>();
            map.put("lastRolledUpDate", nextRolledUpDate);

            GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                    .fields(FieldFactory.getRuleRollupSummaryFields())
                    .table(ModuleFactory.getRuleRollupSummaryModule().getTableName());
            insertRecordBuilder.insert(map);
        }
        else {
            map.put("lastRolledUpDate", nextRolledUpDate);

            GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                    .fields(FieldFactory.getRuleRollupSummaryFields())
                    .table(ModuleFactory.getRuleRollupSummaryModule().getTableName())
                    .andCondition(CriteriaAPI.getIdCondition((Long) map.get("id"), ModuleFactory.getRuleRollupSummaryModule()));
            updateBuilder.update(map);
        }
    }
}
