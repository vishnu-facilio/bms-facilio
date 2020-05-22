package com.facilio.bmsconsole.jobs;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.RuleRollupCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.job.JobContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.List;
import java.util.Map;

public class RuleRollupJob extends AlarmRollupJob {

    @Override
    public void execute(JobContext jc) throws Exception {

        long totalExecutionTime = 0;

        Long lastOccurredTime = null;

        long lastRolledUpDate = -1;
        long nextRolledUpDate = -1;
        Map map = null;

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        map = getLastRolledUpDate(ModuleFactory.getRuleRollupSummaryModule(), FieldFactory.getRuleRollupSummaryFields());
        if (MapUtils.isNotEmpty(map)) {
            if (map.containsKey("lastRolledUpDate")) {
                lastRolledUpDate = (long) map.get("lastRolledUpDate");
            }
        }
        if (lastRolledUpDate <= 0) {
            lastRolledUpDate = getLastRolledUpDateFromOccurrence();
        }

        if (lastRolledUpDate == -1) {
            // No occurrence found
            return;
        }

        while (totalExecutionTime <= TRANSACTION_TIME) {
            long startTime = System.currentTimeMillis();

            nextRolledUpDate = getNextRolledUpDate(lastRolledUpDate);

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

            totalExecutionTime += System.currentTimeMillis() - startTime;
            if (nextRolledUpDate < getPreviousDayEndTime()) {
                // get only last occurred time while running historical
                lastOccurredTime = NewAlarmAPI.getReadingAlarmLastOccurredTime();
            }
            lastRolledUpDate = updateOneTimeJob(nextRolledUpDate, lastOccurredTime);

            if (lastRolledUpDate >= getPreviousDayEndTime()) {
                break;
            }
        }

        scheduleNextJob(lastRolledUpDate, "RuleRollupJob-OneTime", jc);
        updateSummaryTable(ModuleFactory.getRuleRollupSummaryModule(), FieldFactory.getRuleRollupSummaryFields(), map, lastRolledUpDate);
    }
}
